<#include "../../layout/mainLayout.ftl">
<link rel="stylesheet" href="../../static/module/common.css">
<link href="../../static/module/index1.css" rel="stylesheet">
<@layout>

<div style="padding: 20px;" class="content">
<#--    <h3>数据表配置</h3>-->
    <div class="content-table">
        <div class="content-input layui-fluid layui-col-space10 clearfix">

            <div class="line">
                <span class="label">转换任务名称</span>
                <input type="text" name="taskName"  id="taskName" lay-verify="title" autocomplete="off" placeholder="请输入转换任务名称" class="layui-input">
            </div>
            <div class="line">
                <button type="button" class="layui-btn" onclick="window.reloadTable()" id="queryForm" >查询</button>
            </div>
            <div class="line right">
            <button type="button" class="layui-btn layui-btn-primary btn layui-btn-disabled" id="delItems" disabled><i class="layui-icon">&#xe640;</i>删除</button>
                <button type="button" class="layui-btn s-btn" id="newGatherDict"
                        onclick="window.location.href = '/exJobTask/page/newPage'">新增转换任务
                </button>
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
            url: '/exJobTask/pageData', //数据接口
            where: {},
            // even: true,
            skin: 'line',
            done: function (res, curr, count) {
                tablePage(res.count, curr)
            },
//            page: true, //开启分页
            cols: [[ //表头
                {type: 'checkbox'},
//                {field: 'id', title: '序号'},
                {field: 'jobtaskName', title: '转换任务名称', minWidth: 120},
                {field: 'sourceDbName', title: '源数据库', minWidth: 150},
//                {field: 'sourceUser', title: '源库用户名', minWidth: 100},
                {field: 'sourceTb', title: '源数据表', minWidth: 120},
                {field: 'destDbName', title: '目标数据库', minWidth: 150},
//                {field: 'destUser', title: '目标库用户名', minWidth: 100},
                {field: 'destTb', title: '目标数据表', minWidth: 120},
                {field: 'createTime', title: '创建时间', minWidth: 170},
//                {field: 'modifyTime', title: '修改时间'},
                {field: 'validFlag', title: '状态', minWidth: 100},
                {
                    title: '操作',fixed:'right',width: 160, toolbar: '<div>' +
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


        window.reloadTable = function (reset = false, curr, limit) {
            var fields = {"taskName": $("#taskName").val(), current: curr || 1, size: limit || 10};
//            $.each($('#queryForm').serializeArray(), function (i, field) {
//                fields[field.name] = field.value;
//            });
            dataTable.reload({
                where: fields,
                page: reset?{
                    curr: 1 //重新从第 1 页开始
                }:undefined
            });
        };
        window.flagReload = function () {
            reloadTable(false,tablePageCurr);
        }

        table.on('tool(dataTable)', function (obj) { //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
            var data = obj.data; //获得当前行数据
            var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
            var tr = obj.tr; //获得当前行 tr 的DOM对象

            if (layEvent === 'detail') { //详情
                location.href="/exJobTask/page/detailPage?id="+data.jobtaskId;
//                $.ajax({
//                    url: '/exTableMapping/taskPage/detailPage',
//                    data: {id: data.id},
//                    async: false,
//                    success: function (data, status, xhr) {
//                        layer.open({
//                            type: 1,
//                            title: '详情',
//                            area: ['950px', '650px'],
//                            content: data //注意，如果str是object，那么需要字符拼接。
//                        });
//                    }
//                });
            } else if (layEvent === 'del') { //删除
                layer.confirm('确定要删除该条数据吗？', function (index) {
                    $.ajax({
                        url: '/exJobTask/delete',
                        data: {id: data.jobtaskId},
                        async: false,
                        success: function (data, status, xhr) {
                            if (data.success) {
                                layer.close(index);
                                reloadTable(false,tablePageCurr);
                            }
                        }
                    });
                });
            } else if (layEvent === 'edit') { //编辑
                location.href = "/exJobTask/page/editTask?jobtaskId=" + data.jobtaskId;
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
            reloadTable(false);
        });

        $('#newItem').click(function () {
            location.href = "exJobTask/page/newPage";
        });

        $('#delItems').click(function () {
            var checkStatus = table.checkStatus('dataTable'), data = checkStatus.data;
            layer.confirm('确认批量删除？', function (index) {
                $.ajax({
                    url: '/exJobTask/deleteBatch',
                    data: {id: data.map(item => item.jobtaskId)},
                async: false,
                        traditional: true,
                        success: function (data, status, xhr) {
                    if (data.success) {
                        layer.close(index);
                        layer.msg('删除成功', {
                            time:1000
                        });
                        reloadTable(false,tablePageCurr);
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