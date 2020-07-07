<#include "../../layout/mainLayout.ftl">
<link href="../../../static/module/common.css" rel="stylesheet">
<link href="../../../static/module/index1.css" rel="stylesheet">
<@layout>

<div style="padding: 20px;" class="content">
    <div class="content-table">
        <div class="content-input layui-fluid layui-col-space10 clearfix">
            <div class="line">
                <span class="label">数据库名</span>
                <input type="text" name="dbName"  id="dbName" lay-verify="title" autocomplete="off" placeholder="请输入数据库名" class="layui-input">
            </div>
            <div class="line">
                <button type="button" class="layui-btn" lay-submit="" id="queryForm" onclick="reloadTable()" >查询</button>
            </div>
            <div class="line right">
                <button type="button" class="layui-btn layui-btn-primary btn layui-btn-disabled" id="delItems" disabled><i class="layui-icon">&#xe640;</i>删除</button>
                <button type="button" class="layui-btn s-btn" id="newDbMapping"  onclick="window.location.href = '/exDbMapping/page/addDbMappingPage'">配置交换库</button>
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
        layui.use(['laydate', 'table', 'layer', 'global','laypage'], function () {
            var table = layui.table, layer = layui.layer, $ = layui.$, laydate = layui.laydate,laypage = layui.laypage;
            laydate.render({
                elem: '#createTime',
                type: 'date'
            });
            laydate.render({
                elem: '#createTimeRange',
                type: 'datetime',
                range: true
            });
            var dataTable = table.render({
                elem: '#dataTable',
                url: '/exDbMapping/pageData', //数据接口
                where: {},
                skin:'line',
                done: function (res, curr, count) {
                    tablePage(res.count)
                },
                cols: [[ //表头
                    {type: 'checkbox'},
                    // {field: 'id', title: '序号',minWidth: 80},
                    {field: 'gatherDesc', title: '交换节点名称',minWidth: 150},
                    {field: 'sourceDbName', title: '源数据库',minWidth: 150},
                    // {field: 'sourceDb', title: '源数据库描述',minWidth: 150},
                    {field: 'destDbName', title: '目标数据库',minWidth: 150},
                    // {field: 'destDb', title: '目标数据库描述',minWidth: 150},
                    {field: 'validFlag', title: '状态',minWidth: 100},
                    {field: 'createTime', title: '创建时间',minWidth: 180},
                    {
                        title: '操作',fixed:'right',minWidth: 160, toolbar: '<div>' +
                            '<a class="layui-btn layui-btn-xs layui-btn-primary primary" lay-event="detail">详情</a>' +
                            '<a class="layui-btn layui-btn-xs layui-btn-primary" lay-event="edit">编辑</a>' +
                            '<a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>' +
                            '</div>'
                    }
                ]]
            });

             //分页
            var tablePage = function (page) {
                laypage.render({
                    elem: 'demo7'
                    , count: page,
                    layout: ['prev', 'page', 'next', 'count'],
                    curr: tablePageCurr
                    , jump: function (obj, first) {
                        if (!first) {
                            tablePageCurr = obj.curr
                            window.reloadTable(false, obj.curr, obj.limit)
                        }
                    }
                });
            }, tablePageCurr = 1

            // 刷新表格
            window.reloadTable = function (reset = false, curr, limit) {
            var fields = {"dbName": $("#dbName").val(), current: curr || 1, size: limit || 10};
            $.each($('#queryForm').serializeArray(), function (i, field) {
                fields[field.name] = field.value;
            });
            dataTable.reload({
                where: fields,
                page: reset?{
                    curr: 1 //重新从第 1 页开始
                }:undefined
            });
        };

            // 监听表格操作栏
            table.on('tool(dataTable)', function (obj) { //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
                var data = obj.data; //获得当前行数据
                var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
                var tr = obj.tr; //获得当前行 tr 的DOM对象

                if (layEvent === 'detail') { //详情
                    $.ajax({
                        url: '/exDbMapping/detailPage',
                        data: {id: data.id},
                        async: false,
                        success: function (data, status, xhr) {
                            layer.open({
                                type: 1,
                                title: '详情',
                                resize: false,
                                area: ['750px', '500px'],
                                content: data //注意，如果str是object，那么需要字符拼接。
                            });
                        }
                    });
                } else if (layEvent === 'del') { //删除
                    layer.confirm('确定要删除该条数据吗？', function (index) {
                        $.ajax({
                            url: '/exDbMapping/delete',
                            data: {id: data.id},
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
                    $.ajax({
                        url: '/exDbMapping/updatePage',
                        data: {id: data.id},
                        async: false,
                        success: function (data, status, xhr) {
                            layer.open({
                                type: 1,
                                title: '编辑',
                                area: ['750px', '400px'],
                                content: data,
                                resize: false
                            });
                        }
                    });
                }
            });

            // 监听表格复选框
            table.on('checkbox(dataTable)', function(obj) {
                var checkStatus = table.checkStatus('dataTable') ,data = checkStatus.data;
                if (data.length > 0) {
                    $('#delItems').removeClass('layui-btn-disabled');
                    $('#delItems').removeAttr('disabled');
                } else {
                    $('#delItems').addClass('layui-btn-disabled');
                    $('#delItems').attr('disabled', 'disabled');
                }
            });

            // 新建
            $('#newItem').click(function () {
                $.ajax({
                    url: '/exDbMapping/newPage',
                    async: false,
                    success: function (data, status, xhr) {
                        layer.open({
                            type: 1,
                            title: '新建',
                            area: ['750px', '500px'],
                            content: data
                        });
                    }
                });
            });

            // 删除
            $('#delItems').click(function () {
                var checkStatus = table.checkStatus('dataTable'), data = checkStatus.data;
                layer.confirm('确认批量删除？', function (index) {
                    $.ajax({
                        url: '/exDbMapping/deleteBatch',
                        data: {id: data.map(item => item.id)},
                        async: false,
                        traditional: true,
                        success: function (data, status, xhr) {
                            if (data.success) {
                                layer.close(index);
                                layer.msg('删除成功', {
                                    time:1000
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