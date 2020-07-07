<#include "../layout/mainLayout.ftl">
<link href="../../static/module/common.css" rel="stylesheet">
<link href="../../static/module/index1.css" rel="stylesheet">
<link href="../../static/module/collectTask.css" rel="stylesheet">
<@layout>

    <div style="padding: 20px;" class="content">
        <div class="layui-breadcrumb clearfix" lay-separator=">">
            <a href="/jump/sourceDb">元数据管理</a>
            <a><cite>元数据详情</cite></a>
            <div class="right">
                <button type="button" class="layui-btn layui-btn-primary layui-btn-sm back">返回</button>
            </div>
        </div>
        <div class="content-table" style="margin-top:30px">
            <div class="msg-box" style="overflow: hidden;">
                <h3>基本信息</h3>
                <ul>
                    <li>
                        <span class="msg-name">元数据信息:</span>
                        <span>123</span>
                    </li>
                    <li>
                        <span class="msg-name">元数据名称:</span>
                        <span>123</span>
                    </li>
                    <li>
                        <span class="msg-name">元数据编码:</span>
                        <span>123</span>
                    </li>
                    <li>
                        <span class="msg-name">元数据类型:</span>
                        <span>123</span>
                    </li>
                    <li>
                        <span class="msg-name">来源数据库:</span>
                        <span>123</span>
                    </li>
                    <li>
                        <span class="msg-name">数据更新周期:</span>
                        <span>123</span>
                    </li>
                    <li class="describe-box">
                        <span class="msg-name describe-title">描述:</span>
                        <span class="describe">123</span>
                    </li>
                </ul>
            </div>
            <div class="table">
                <div class="field-msg">
                    <h3>字段信息</h3>
                </div>
                <table id="dataTable" lay-filter="dataTable"></table>
                <table class="layui-table" lay-even="" lay-skin="nob">
                    <colgroup>
                        <col width="150">
                        <col width="150">
                        <col width="150">
                        <col width="150">
                        <col width="150">
                        <col width="200">
                        <col>
                    </colgroup>
                    <thead>

                    <tr>
                        <th>序号</th>
                        <th>字段名称</th>
                        <th>字段含义</th>
                        <th>字段类型</th>
                        <th>字段长度</th>
                        <th>字段精度</th>
                        <th>是否主键</th>
                        <th>类型</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>1</td>
                        <td>iduserpassword</td>
                        <td>id</td>
                        <td>int</td>
                        <td>11</td>
                        <td></td>
                        <td>是</td>
                        <td>int</td>
                    </tr>
                    </tbody>
                </table>
            </div>

        </div>
    </div>
</@layout>
<@js>
    <script>
        //     layui.use(['laydate', 'table', 'layer', 'global','laypage','layer'], function () {
        //         var table = layui.table, layer = layui.layer, $ = layui.$, laydate = layui.laydate,laypage = layui.laypage,layer = layui.layer;
        //
        //         laydate.render({
        //             elem: '#test1',
        //             type: 'date'
        //         });
        //         laydate.render({
        //             elem: '#createTime',
        //             type: 'date'
        //         });
        //         laydate.render({
        //             elem: '#createTimeRange',
        //             type: 'datetime',
        //             range: true
        //         });
        //         var dataTable = table.render({
        //             elem: '#dataTable',
        //             url: '/exDbDict/pageData', //数据接口
        //             where: {},
        //             even:true,
        //             skin:'nob',
        // //                page: true, //开启分页
        //             cols: [[ //表头
        //                 // {field: 'num', title: '序号'},
        //                 {field: 'dbDesc', title: '字段名称'},
        //                 {field: 'dbIp', title: '字段含义'},
        //                 {field: 'dbPort', title: '字段类型'},
        //                 {field: 'dbUser', title: '字段长度'},
        //                 {field: 'dbPasswd', title: '字段精度'},
        //                 {field: 'dbName', title: '是否主键'},
        //                 // {field: 'dbType', title: '类型'},
        //                 // {
        //                 //     title: '操作', toolbar: '<div>' +
        //                 // '<a class="layui-btn layui-btn-xs layui-btn-primary" lay-event="detail" style="color: #3986F4; border-color:#3986F4">详情</a>' +
        //                 // '<a class="layui-btn layui-btn-xs layui-btn-primary" lay-event="edit" style="color: #4FBEBF; border-color: #4FBEBF">编辑</a>' +
        //                 // '<a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del" style="background-color: rgba(0,0,0,0);color: #999;border:1px solid #999">删除</a>' +
        //                 // '</div>',
        //                 //     minWidth: 180
        //                 // }
        //             ]]
        //         });
        //
        //         //分页
        //         laypage.render({
        //             elem: 'demo7'
        //             ,count: 100
        //             ,layout: ['count', 'prev', 'page', 'next', 'limit', 'refresh', 'skip']
        //             ,jump: function(obj){
        //                 console.log(obj)
        //             }
        //         });
        //
        //         window.reloadTable = function (reset = false) {
        //             var fields = {};
        //             $.each($('#queryForm').serializeArray(), function (i, field) {
        //                 fields[field.name] = field.value;
        //             });
        //             dataTable.reload({
        //                 where: fields,
        //                 page: reset?{
        //                     curr: 1 //重新从第 1 页开始
        //                 }:undefined
        //             });
        //         };
        //
        //         table.on('tool(dataTable)', function (obj) { //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
        //             var data = obj.data; //获得当前行数据
        //             var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
        //             var tr = obj.tr; //获得当前行 tr 的DOM对象
        //
        //             if (layEvent === 'detail') { //详情
        //                 $.ajax({
        //                     url: '/exDbDict/detailPage',
        //                     data: {dbId: data.dbId},
        //                     async: false,
        //                     success: function (data, status, xhr) {
        //                         layer.open({
        //                             type: 1,
        //                             title: '详情',
        //                             area: ['750px', '500px'],
        //                             content: data //注意，如果str是object，那么需要字符拼接。
        //                         });
        //                     }
        //                 });
        //             } else if (layEvent === 'del') { //删除
        //                 layer.confirm('确认删除？', function (index) {
        //                     $.ajax({
        //                         url: '/exDbDict/delete',
        //                         data: {dbId: data.dbId},
        //                         async: false,
        //                         success: function (data, status, xhr) {
        //                             if (data.success) {
        //                                 layer.close(index);
        //                                 reloadTable();
        //                             }
        //                         }
        //                     });
        //                 });
        //             } else if (layEvent === 'edit') { //编辑
        //                 location.href="/exDbDict/updatePage?dbId="+data.dbId;
        //             /*    $.ajax({
        //                     url: '/exDbDict/updatePage',
        //                     data: {dbId: data.dbId},
        //                     async: false,
        //                     success: function (data, status, xhr) {
        //                         layer.open({
        //                             type: 1,
        //                             title: '编辑',
        //                             area: ['750px', '500px'],
        //                             content: data
        //                         });
        //                     }
        //                 });*/
        //             }
        //         });
        //
        //         table.on('checkbox(dataTable)', function(obj) {
        //             var checkStatus = table.checkStatus('dataTable') ,data = checkStatus.data;
        //             if (data.length > 0) {
        //                 $('#delItems').removeClass('layui-btn-disabled');
        //                 $('#delItems').removeAttr('disabled');
        //             } else {
        //                 $('#delItems').addClass('layui-btn-disabled');
        //                 $('#delItems').attr('disabled', 'disabled');
        //             }
        //         });
        //
        //         $('#queryPage').click(function () {
        //             reloadTable(true);
        //         });
        $('.back').click(function () {
            location.href = "/jump/sourceDb"
        })
        //
        //         $('#newItem').click(function () {
        //             location.href="/jump/addDbDict";
        //          /*   $.ajax({
        //                 url: '/exDbDict/newPage',
        //                 async: false,
        //                 success: function (data, status, xhr) {
        //                     layer.open({
        //                         type: 1,
        //                         title: '新建',
        //                         area: ['750px', '500px'],
        //                         content: data
        //                     });
        //                 }
        //             });*/
        //         });
        //
        //         $('#delItems').click(function () {
        //             var checkStatus = table.checkStatus('dataTable'), data = checkStatus.data;
        //             layer.confirm('确认批量删除？', function (index) {
        //                 $.ajax({
        //                     url: '/exDbDict/deleteBatch',
        //                     data: {dbIds: data.map(item => item.dbId)},
        //                 async: false,
        //                         traditional: true,
        //                         success: function (data, status, xhr) {
        //                     if (data.success) {
        //                         layer.close(index);
        //                         layer.msg('删除成功', {
        //                             time:1000
        //                         });
        //                         reloadTable();
        //                     }
        //                 }
        //             })
        //             });
        //         });
        //     });
    </script>
</@js>