<#include "../../layout/mainLayout.ftl">
<@layout>
    <div class="layui-card">
        <div class="layui-card-header">
            <span class="layui-breadcrumb">
                ${breadcrumb!''}
            </span>
        </div>
    </div>
    <div class="layui-card">
        <div class="layui-card-body">
            <form class="layui-form" id="queryForm">
                <div class="layui-form-item">
                    <label class="layui-form-label" style="width: auto">主键</label>
                    <div class="layui-input-inline">
                        <input type="text" name="key" class="layui-input">
                    </div>
                    <label class="layui-form-label" style="width: auto">主键选择</label>
                    <div class="layui-input-inline">
                        <select name="id">
                            <option value=""></option>
                            <option value="1">1</option>
                            <option value="2">2</option>
                            <option value="3">3</option>
                            <option value="4">4</option>
                        </select>
                    </div>
                    <label class="layui-form-label" style="width: auto">创建日期</label>
                    <div class="layui-input-inline">
                        <input type="text" name="createTime" id="createTime" class="layui-input" autocomplete="off">
                    </div>
                    <label class="layui-form-label" style="width: auto">创建时间范围</label>
                    <div class="layui-input-inline">
                        <input type="text" name="createTimeRange" id="createTimeRange" class="layui-input" autocomplete="off">
                    </div>
                    <button type="button" class="layui-btn" lay-submit="" id="queryPage">查询</button>
                    <div style="float:right;">
                        <button type="button" class="layui-btn layui-btn-danger layui-btn-disabled" id="delItems" disabled><i class="layui-icon">&#xe640;</i>删除</button>
                        <button type="button" class="layui-btn" id="newItem"><i class="layui-icon">&#xe608;</i>新建</button>
                    </div>
                </div>
            </form>
            <table id="dataTable" lay-filter="dataTable"></table>
        </div>
    </div>
</@layout>
<@js>
    <script>
        layui.use(['laydate', 'table', 'layer', 'global'], function () {
            var table = layui.table, layer = layui.layer, $ = layui.$, laydate = layui.laydate;
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
                url: '/exOrganizationDict/pageData', //数据接口
                where: {},
                page: true, //开启分页
                cols: [[ //表头
                    {type: 'checkbox'},
                    {field: 'orgId', title: '机构id'},
                    {field: 'orgCode', title: '组织机构代码'},
                    {field: 'orgName', title: '组织机构名称'},
                    {field: 'orgLevel', title: '机构所属等级'},
                    {field: 'orgParentid', title: '组织机构上级id'},
                    {
                        title: '操作', toolbar: '<div>' +
                            '<a class="layui-btn layui-btn-xs layui-btn-primary" lay-event="detail">详情</a>' +
                            '<a class="layui-btn layui-btn-xs layui-btn-primary" lay-event="edit">编辑</a>' +
                            '<a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>' +
                            '</div>',
                        minWidth: 180
                    }
                ]]
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
                        url: '/exOrganizationDict/detailPage',
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
                            url: '/exOrganizationDict/delete',
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
                        url: '/exOrganizationDict/updatePage',
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
                    url: '/exOrganizationDict/newPage',
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
                        url: '/exOrganizationDict/deleteBatch',
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