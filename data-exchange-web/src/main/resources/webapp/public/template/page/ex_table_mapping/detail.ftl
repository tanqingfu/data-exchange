<#include "../../layout/mainLayout.ftl">
<link rel="stylesheet" href="../../../static/module/index1.css">
<link rel="stylesheet" href="../../../static/module/add.css">
<@layout>

<div class="content">
    <div class="layui-breadcrumb clearfix" lay-separator=">">
        <a href="/exJobTask/page">数据交换</a>
        <a><cite>任务详情</cite></a>
        <div class="right">
            <button type="button" class="layui-btn layui-btn-primary layui-btn-sm back">返回</button>
        </div>
    </div>
    <div class="add-from">
        <form class="layui-form layout-form" id="updateItemForm" style="width:80% " >
            <div class="layui-form-item">
                    <div class="layui-form-item">
                        <label class="layui-form-label">交换任务名称：</label>
                        <div class="layui-input-block">
                            <span class="detail-item">${info.taskName!''}</span>
                        </div>
                    </div>
                <div class="layui-col-md6">
                    <div class="layui-form-item">
                        <label class="layui-form-label">源数据库：</label>
                        <div class="layui-input-block">
                            <span class="detail-item">${info.sourceDbName!''}</span>
                        </div>
                    </div>
                </div>
                <div class="layui-col-md6">
                    <div class="layui-form-item">
                        <label class="layui-form-label">源库用户名：</label>
                        <div class="layui-input-block">
                            <span class="detail-item">${info.sourceUser!''}</span>
                        </div>
                    </div>
                </div>
                <div class="layui-col-md6">
                    <div class="layui-form-item">
                        <label class="layui-form-label">源数据表名：</label>
                        <div class="layui-input-block">
                            <span class="detail-item">${info.sourceTable!''}</span>
                        </div>
                    </div>
                </div>
                <div class="layui-col-md6">
                    <div class="layui-form-item">
                        <label class="layui-form-label">目标数据库：</label>
                        <div class="layui-input-block">
                            <span class="detail-item">${info.destDbName!''}</span>
                        </div>
                    </div>
                </div>
                <div class="layui-col-md6">
                    <div class="layui-form-item">
                        <label class="layui-form-label">目标库用户名：</label>
                        <div class="layui-input-block">
                            <span class="detail-item">${info.destUser!''}</span>
                        </div>
                    </div>
                </div>
                <div class="layui-col-md6">
                    <div class="layui-form-item">
                        <label class="layui-form-label">目标数据表名：</label>
                        <div class="layui-input-block">
                            <span class="detail-item">${info.destTable!''}</span>
                        </div>
                    </div>
                </div>
                <table class="layui-table"  style="width: 80%;margin-top: 30px;text-align: center; border-color:#ccc">
                    <colgroup>
                        <col width="50%">
                        <col width="50%">
                        <col>
                    </colgroup>
                    <thead>
                    <tr>
                        <th>交换量统计时间段/小时</th>
                        <th>数据交换量</th>
                    </tr>
                    </thead>
                    <tbody>
                        <#list param as param>
                        <tr>
                            <td style="padding-top: 20px">${param.swaData!''}</td>
                            <td style="padding-top: 20px">${param.swaGross!''}</td>
                        </tr>
                        </#list>
                    </tbody>
                </table>
            </div>
        </form>
    </div>
</div>
<script>
    layui.use(['laydate', 'table', 'layer', 'global', 'laypage', 'layer','form'], function () {
        var table = layui.table, $ = layui.$, laydate = layui.laydate, laypage = layui.laypage, form = layui.form
            layer = layui.layer;
        var dataTable = table.render({
            elem: '#dataTable',
            url: '/exTableMapping/detailInfos', //数据接口
            where: {'id':${id}},
            even: true,
            skin: 'nob',
            done: function (res, curr, count) {
                tablePage(res.count, curr)
            },
            cols: [[ //表头
                {field: 'taskName', title: '交换任务名称'},
                {field: 'sourceDbName', title: '源数据库'},
                {field: 'sourceUser', title: '源库用户名'},
                {field: 'sourceTable', title: '源数据表名'},
                {field: 'destDbName', title: '目标数据库'},
                {field: 'destUser', title: '目标库用户名'},
                {field: 'destTable', title: '目标数据表名'},
                {field: 'swaData', title: '交换量统计时间段/小时'},
                {field: 'swaGross', title: '数据交换量'}
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
            dataTable.reload({
                where: fields
            });
        };
        $('.back').click(function () {
            location.href = "/exTableMapping/taskPage"
        });
        form.render();
    });
</script>
</@layout>











<#--<#include "../../layout/mainLayout.ftl">-->
<#--<link rel="stylesheet" href="../../../static/module/index1.css">-->
<#--<link rel="stylesheet" href="../../../static/module/add.css">-->
<#--<@layout>-->

<#--<div class="content">-->
    <#--<div class="layui-breadcrumb clearfix" lay-separator=">">-->
        <#--<a href="/exTableMapping/taskPage">数据源管理</a>-->
        <#--<a><cite>转换详情</cite></a>-->
        <#--<div class="right">-->
            <#--<button type="button" class="layui-btn layui-btn-primary layui-btn-sm back">返回</button>-->
        <#--</div>-->
    <#--</div>-->
    <#--<div class="add-from">-->
        <#--<div class="table">-->
            <#--<table id="dataTable" lay-filter="dataTable">-->
            <#--</table>-->
        <#--</div>-->
        <#--<div class="page">-->
            <#--<div id="demo7"></div>-->
        <#--</div>-->
    <#--</div>-->
<#--</div>-->


<#--<script>-->
    <#--layui.use(['laydate', 'table', 'layer', 'global', 'laypage', 'layer'], function () {-->
        <#--var table = layui.table, layer = layui.layer, $ = layui.$, laydate = layui.laydate, laypage = layui.laypage,-->
                <#--layer = layui.layer;-->
        <#--var dataTable = table.render({-->
            <#--elem: '#dataTable',-->
            <#--url: '/exTableMapping/detailInfos', //数据接口-->
            <#--where: {'id':${id}},-->
            <#--even: true,-->
            <#--skin: 'nob',-->
            <#--done: function (res, curr, count) {-->
                <#--tablePage(res.count, curr)-->
            <#--},-->
<#--//            page: true, //开启分页-->
            <#--cols: [[ //表头-->
<#--//                {type: 'checkbox'},-->
<#--//                {field: 'id', title: '序号'},-->
                <#--{field: 'taskName', title: '交换任务名称'},-->
                <#--{field: 'sourceDbName', title: '源数据库'},-->
                <#--{field: 'sourceUser', title: '源库用户名'},-->
                <#--{field: 'sourceTable', title: '源数据表名'},-->
                <#--{field: 'destDbName', title: '目标数据库'},-->
                <#--{field: 'destUser', title: '目标库用户名'},-->
                <#--{field: 'destTable', title: '目标数据表名'},-->
                <#--{field:'swaData',title:'交换量统计时间段/小时'},-->
                <#--{field:'swaGross',title:'数据交换量'}-->
            <#--]]-->
        <#--});-->

        <#--//分页-->
        <#--var tablePage = function (page) {-->
            <#--laypage.render({-->
                <#--elem: 'demo7'-->
                <#--, count: page,-->
                <#--layout: ['prev', 'page', 'next', 'count'],-->
                <#--curr: tablePageCurr-->
                <#--, jump: function (obj, first) {-->
                    <#--if (!first) {-->
                        <#--tablePageCurr = obj.curr-->
                        <#--window.reloadTable(false, obj.curr, obj.limit)-->
                    <#--}-->
                <#--}-->
            <#--});-->
        <#--}, tablePageCurr = 1-->

        <#--window.reloadTable = function (reset = false, curr, limit) {-->
            <#--var fields = {"taskName": $("#taskName").val(), current: curr || 1, size: limit || 10};-->
<#--//            $.each($('#queryForm').serializeArray(), function (i, field) {-->
<#--//                fields[field.name] = field.value;-->
<#--//            });-->
            <#--dataTable.reload({-->
                <#--where: fields-->
            <#--});-->
        <#--};-->
    <#--});-->





    <#--$('.back').click(function () {-->
        <#--location.href = "/exTableMapping/taskPage"-->
    <#--});-->
    <#--layui.use(['form', 'global'], function () {-->
        <#--var form = layui.form, $ = layui.$;-->
        <#--window.onClose = function () {-->
            <#--layer.closeAll();-->
        <#--};-->
        <#--form.render();-->
    <#--});-->
<#--</script>-->
<#--</@layout>-->