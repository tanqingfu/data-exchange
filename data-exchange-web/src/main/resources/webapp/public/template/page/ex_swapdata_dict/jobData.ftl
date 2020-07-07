<#include "../../layout/mainLayout.ftl">
<link href="/static/module/common.css" rel="stylesheet">
<link href="/static/module/index1.css" rel="stylesheet">
<@layout>

<div style="padding: 20px;" class="content">
<#--    <h3>作业日志</h3>-->
    <div class="content-table">
        <div class="content-input layui-fluid layui-col-space10 layui-form">
            <div class="line">
                <span class="label">任务选择</span>
                <select name="jobId" id="jobId">
                    <option value="">请选择任务</option>
                </select>
            </div>
            <div class="line">
                <button type="button" class="layui-btn" onclick="window.reloadTable()" id="queryForm" >查询</button>
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
        layui.use(['laydate', 'table', 'layer', 'global','laypage','form'], function () {
            var table = layui.table, layer = layui.layer, $ = layui.$, laydate = layui.laydate, laypage = layui.laypage,form = layui.form;
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
                url: '/exSwapdataDict/pageData2', //数据接口
                where: {},
                 // even: true,
                 skin: 'line',
                 done: function (res, curr, count) {
                     tablePage(res.count, curr)
                 },
// <!--                page: true, //开启分页-->
                cols: [[ //表头
                    // {type: 'checkbox'},
                    {field: 'job_name', title: '作业名称'},
                    {field: 'sourcedb_desc', title: '源库名'},
                    {field: 'destdb_desc', title: '目标库名'},
                    {field: 'swa_gross', title: '总交换量'}
                    // {field: 'swa_data', title: '交换的时间/24小时制'}
                    // {field: 'swaGross', title: '交换数据 总量'},
                    // {
                    //     title: '操作', toolbar: '<div>' +
                    //         '<a class="layui-btn layui-btn-xs layui-btn-primary" lay-event="detail">详情</a>' +
                    //         '<a class="layui-btn layui-btn-xs layui-btn-primary" lay-event="edit">编辑</a>' +
                    //         '<a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>' +
                    //         '</div>',
                    //     minWidth: 180
                    // }
                ]]
            });

            $.ajax({
                type: 'post',
                url: '/exSwapdataDict/queryJobAllInfo',
                success: function (response){
                    var data = response.data;
                    var option = "<option value=''>请选择任务</option>";//初始化option的选项
                    for (var i = 0; i < data.length; i++) {
                        option += "<option value='" + data[i]['jobId'] + "'>" + data[i]['jobName'] + "</option>";//拼接option中的内容
                    }
                    $("#jobId").html(option);//将option的拼接内容加入select中，注意选择器是select的ID

                    form.render('select');//重点：重新渲染select
                }
            })
            //分页
        var tablePage = function (page) {
            laypage.render({
                elem: 'demo7'
                , count: page
                ,layout: ['prev', 'page', 'next', 'count']
                ,curr: tablePageCurr
                , jump: function (obj, first) {
                    if (!first) {
                        tablePageCurr = obj.curr
                        window.reloadTable(false, obj.curr, obj.limit)
                    }
                }
            });
        },tablePageCurr = 1
            window.reloadTable = function (reset = false, curr, limit) {
                var fields = {};
                fields = {jobId: $('select[name="jobId"]').val(), current: curr || 1, size: limit || 10}
                dataTable.reload({
                    where: fields,
                });
            };

            table.on('tool(dataTable)', function (obj) { //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
                var data = obj.data; //获得当前行数据
                var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
                var tr = obj.tr; //获得当前行 tr 的DOM对象

                if (layEvent === 'detail') { //详情
                    $.ajax({
                        url: '/exSwapdataDict/detailPage',
                        data: {id: data.id},
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
                            url: '/exSwapdataDict/delete',
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
                        url: '/exSwapdataDict/updatePage',
                        data: {id: data.id},
                        async: false,
                        success: function (data, status, xhr) {
                            layer.open({
                                type: 1,
                                title: '编辑',
                                area: ['750px', '500px'],
                                content: data
                            });
                        }
                    });
                }
            });

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

            $('#queryPage').click(function () {
                reloadTable(true);
            });

            $('#newItem').click(function () {
                $.ajax({
                    url: '/exSwapdataDict/newPage',
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

            $('#delItems').click(function () {
                var checkStatus = table.checkStatus('dataTable'), data = checkStatus.data;
                layer.confirm('确认批量删除？', function (index) {
                    $.ajax({
                        url: '/exSwapdataDict/deleteBatch',
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
                            }
                        }
                    })
                });
            });
        });
    </script>
</@js>