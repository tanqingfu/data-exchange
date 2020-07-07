<#include "../../layout/mainLayout.ftl">
<link href="../../static/module/common.css" rel="stylesheet">
<link href="../../static/module/index1.css" rel="stylesheet">
<@layout>
<div style="padding: 20px;" class="content">
    <div class="content-table">
        <div class="content-input layui-fluid layui-col-space10">
            <div class="line">
                <span class="label">交换节点名称</span>
                <input type="text" name="gatherDesc" id="gatherDesc" lay-verify="title" autocomplete="off"
                       placeholder="请输入交换节点名称" class="layui-input">
            </div>
            <div class="line">
                <button type="button" class="layui-btn" lay-submit="" id="queryForm" onclick="reloadTable()">
                    查询
                </button>
            </div>
            <div class="right right-btn">
                <button type="button" class="layui-btn s-btn" id="newItem">新建交换节点</button>
            </div>
        </div>

        <div class="table">
            <table id="dataTable" lay-filter="dataTable"></table>
        </div>
        <div class="page">
            <div id="demo7"></div>
        </div>
    </div>
</div>

</@layout>
<@js>
<script>
    layui.use(['laydate', 'table', 'layer', 'global', 'laypage', 'layer'], function () {
        var table = layui.table, layer = layui.layer, $ = layui.$, laydate = layui.laydate, laypage = layui.laypage,
                layer = layui.layer;
        laydate.render({
            elem: '#test1',
            type: 'date'
        });

        laydate.render({
            elem: '#createTime',
            type: 'date'
        });
        laydate.render({
            elem: '#createTimeRange',
            type: 'datetime',
            range: true
        });
        // 表格
        var dataTable = table.render({
            elem: '#dataTable',
            url: '/exGatherDict/pageData', //数据接口
            where: {},
//            even: true,
            skin: 'line',
            done: function (res, curr, count) {
                tablePage(res.count)
            },
            cols: [[ //表头
                {field: 'gatherDesc', minWidth: '100', title: '交换节点名称'},
                {field: 'serviceIp', minWidth: '100', title: '服务器IP'},
                {field: 'sshPort', minWidth: '100', title: 'ssh端口'},
                {field: 'sshUser', minWidth: '100', title: 'ssh用户名'},
                {field: 'serviceDesc', minWidth: '100', title: '服务器描述'},
                {field: 'gatherPath', minWidth: '100', title: '交换节点路径'},
            ]]
        });
        $('#queryPage').click(function () {
            reloadTable();
        });
        //分页
        var tablePage = function (page) {
            laypage.render({
                elem: 'demo7'
                , count: page
                , layout: ['prev', 'page', 'next', 'count']
                ,curr: tablePageCurr
                , jump: function (obj, first) {
                    if (!first) {
                        tablePageCurr = obj.curr
                        window.reloadTable(false, obj.curr, obj.limit)
                    }
                }
            });
        }, tablePageCurr = 1

        window.reloadTable = function (reset = false, curr, limit) {
            var fields = {"gatherDesc": $("#gatherDesc").val(), current: curr || 1, size: limit || 10};
            dataTable.reload({
                where: fields,
                page: reset ? {
                    curr: 1 //重新从第 1 页开始
                } : undefined
            });
        };
        table.on('tool(dataTable)', function (obj) { //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
            var data = obj.data; //获得当前行数据
            var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
            var tr = obj.tr; //获得当前行 tr 的DOM对象

            if (layEvent === 'detail') { //详情
                $.ajax({
                    url: '/exGatherDict/detailPage',
                    data: {gatherId: data.gatherId, sourceDb: data.sourceDb},
                    async: false,
                    success: function (data, status, xhr) {
                        layer.open({
                            type: 1,
                            title: '详情',
                            area: ['750px', '500px'],
                            content: data //注意，如果str是object，那么需要字符拼接。
                        });
                    }
                });
            } else if (layEvent === 'del') { //删除
                layer.confirm('确定要删除该条数据吗？', function (index) {
                    $.ajax({
                        url: '/exGatherDict/delete',
                        data: {gatherId: data.gatherId},
                        async: false,
                        success: function (data, status, xhr) {
                            if (data.success) {
                                layer.close(index);
                                reloadTable();
                            }
                        }
                    });
                });
            } else if (layEvent === 'edit') { //编辑
                location.href = "/exGatherDict/updatePage?gatherId=" + data.gatherId + "&sourceDb=" + data.sourceDb;
            }
        });

        table.on('checkbox(dataTable)', function (obj) {
            var checkStatus = table.checkStatus('dataTable'), data = checkStatus.data;
            if (data.length > 0) {
                $('#delItems').removeClass('layui-btn-disabled');
                $('#delItems').removeAttr('disabled');
            } else {
                $('#delItems').addClass('layui-btn-disabled');
                $('#delItems').attr('disabled', 'disabled');
            }
        });

        $('#queryPage').click(function () {
            reloadTable();
        });

        // 新建
        $('#newItem').click(function () {
            window.location.href="/exGatherDict/addGatherDictPage";
        });

        // 删除
        $('#delItems').click(function () {
            var checkStatus = table.checkStatus('dataTable'), data = checkStatus.data;
            layer.confirm('确认批量删除？', function (index) {
                $.ajax({
                    url: '/exGatherDict/deleteBatch',
                    data: {gatherId: data.map(item => item.gatherId)},
                async: false,
                        traditional:true,
                        success:function (data, status, xhr) {
                    if (data.success) {
                        layer.close(index);
                        layer.msg('删除成功', {
                            time: 1000
                        });
                        reloadTable();
                        $('#delItems').addClass('layui-btn-disabled');
                        $('#delItems').attr('disabled', 'disabled');
                    }
                }
            })
            });
        });
    });
</script>
</@js>