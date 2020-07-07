<#include "../../layout/mainLayout.ftl">
<link href="/static/module/common.css" rel="stylesheet">
<link href="/static/module/index1.css" rel="stylesheet">

<@layout>
<div style="padding: 20px;" class="content">
<#--    <h3>数据表配置</h3>-->
    <div class="content-table">
        <div style="overflow: hidden;">
            <div class="content-input">
                <div class="layui-form-item">
                    <label class="layui-form-label" style="width: 115px">交换节点名称</label>
                    <div class="layui-input-block">
                        <input type="text" name="title" lay-verify="title" autocomplete="off" placeholder="请输入交换节点名称" class="layui-input">
                    </div>
                </div>
                <div class="layui-form-item ml" style="clear:none">
                    <div class="layui-inline">
                        <label class="layui-form-label" style="width: 115px">创建日期</label>
                        <div class="layui-input-inline">
                            <input type="text" class="layui-input" id="test1" placeholder="yyyy-MM-dd">
                        </div>
                    </div>
                </div>
                <div class="layui-form-item ml" style="clear:none">
                    <div class="layui-inline">
                        <label class="layui-form-label" style="width: auto">创建时间范围</label>
                        <div class="layui-input-inline">
                            <input type="text" name="createTimeRange" id="createTimeRange" class="layui-input" autocomplete="off" style="width:330px">
                        </div>
                    </div>
                </div>
                <div class="button">
                    <button type="button" class="layui-btn layui-btn-primary btn-look" style="margin-left:100px">查询</button>
                </div>
            </div>
            <div class="right right-btn">
                <button type="button" class="layui-btn layui-btn-primary btn">删除</button>
                <button type="button" class="layui-btn s-btn" id="newGatherDict"  onclick="window.location.href = '/jump/addGatherDict'">新建</button>
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
        layui.use(['laydate', 'table', 'layer', 'global','laypage','layer'], function () {
            var table = layui.table, layer = layui.layer, $ = layui.$, laydate = layui.laydate,laypage = layui.laypage,layer = layui.layer;;
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
                url: '/exFieldMapping/pageData', //数据接口
                where: {},
                skin:'nob',
                even:true,
//               page: true, //开启分页
                cols: [[ //表头
                    {type: 'checkbox'},
                    {field: 'sourceTable', title: '源同步表名'},
                    {field: 'sourceSyncName', title: '源表字段名'},
                    {field: 'sourceFunc', title: '函数-是否单独定义'},
                    {field: 'destTable', title: '目标同步表名'},
                    {field: 'destSyncName', title: '目标表字段名'},
                    {field: 'destFunc', title: '函数-是否单独定义'},
                    {field: 'id', title: '序号'},
                    {field: 'validFlag', title: '是否有效'},
                    {
                        title: '操作', toolbar: '<div>' +
                            '<a class="layui-btn layui-btn-xs layui-btn-primary" lay-event="detail" style="color: #3986F4; border-color:#3986F4">详情</a>' +
                            '<a class="layui-btn layui-btn-xs layui-btn-primary" lay-event="edit" style="color: #4FBEBF; border-color: #4FBEBF">编辑</a>' +
                            '<a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del" style="background-color: rgba(0,0,0,0);color: #999;border:1px solid #999">删除</a>' +
                            '</div>',
                        minWidth: 180
                    }
                ]]
            });

             //分页
            laypage.render({
                elem: 'demo7'
                ,count: 100
                ,layout: ['count', 'prev', 'page', 'next', 'limit', 'refresh', 'skip']
                ,jump: function(obj){
                }
            });

            window.reloadTable = function (reset = false) {
                var fields = {};
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

            table.on('tool(dataTable)', function (obj) { //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
                var data = obj.data; //获得当前行数据
                var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
                var tr = obj.tr; //获得当前行 tr 的DOM对象

                if (layEvent === 'detail') { //详情
                    $.ajax({
                        url: '/exFieldMapping/detailPage',
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
                            url: '/exFieldMapping/delete',
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
                        url: '/exFieldMapping/updatePage',
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
                    url: '/exFieldMapping/newPage',
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
                        url: '/exFieldMapping/deleteBatch',
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