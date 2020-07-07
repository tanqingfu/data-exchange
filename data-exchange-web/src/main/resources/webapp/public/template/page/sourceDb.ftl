<#include "../layout/mainLayout.ftl">
<#--<link rel="stylesheet" href="/static/module/common.css">-->
<link rel="stylesheet" href="/static/module/index1.css" >
<@layout>
<div style="padding: 20px" class="content">
<#--    <h3>元数据管理</h3>-->
    <div style="width:20%;background-color:#fff;min-height: 100%;float:left;overflow:auto;padding:20px">
        <div id="test4" class="demo-tree"></div>
    </div>
    <div class="content-table" style="width:79%;float:right;min-height: 100%;overflow:auto">
        <div class="content-input layui-fluid layui-col-space10 clearfix">
            <div class="line">
                <span class="label">关键字</span>
                <input type="text" name="title" lay-verify="title" autocomplete="off" placeholder="请输入关键字" class="layui-input">
            </div>
            <div class="line">
                <button type="button" class="layui-btn" >高级检索</button>
            </div>
            <div class="line right">
                <button type="button" class="layui-btn layui-btn-primary btn layui-btn-disabled" id="delItems" disabled><i class="layui-icon">&#xe640;</i>删除</button>
            </div>
        </div>
        <div class="table">
            <table id="dataTable" lay-filter="dataTable"></table>
        </div>
        <div class="page" >
            <div id="demo7"></div>
        </div>

    </div>
</div>
</@layout>
<@js>
<script>
layui.use(['tree', 'util'], function(){
  var tree = layui.tree
  ,layer = layui.layer
  ,util = layui.util

  //模拟数据
  ,data = [{
    title: '系统分类'
    ,id: 1
    ,field: 'name1'
    ,checked: true
    ,spread: true
    ,children: [{
      title: '技术元数据'
      ,id: 3
      ,field: 'name11'
      ,href: '#'
      ,children: [{
        title: 'TABLE'
        ,id: 23
        ,field: ''
      },{
        title: 'VIEW'
        ,id: 7
        ,field: ''
      },{
        title: 'PROC'
        ,id: 8
        ,field: ''
      }]
    },{
      title: '业务元数据'
      ,id: 4
      ,spread: true
      ,children: [{
        title: 'DIM'
        ,id: 9
        ,field: ''
<#--        ,disabled: true-->
      },{
        title: 'KPI'
        ,id: 10
        ,field: ''
      }]
    },{
      title: '二级1-3'
      ,id: 20
      ,field: ''
      ,children: [{
        title: '三级1-3-1'
        ,id: 21
        ,field: ''
      },{
        title: '三级1-3-2'
        ,id: 22
        ,field: ''
      }]
    }]
  },{
    title: '自定义分类'
    ,id: 2
    ,field: ''
    ,spread: true
    ,children: [{
      title: '二级2-1'
      ,id: 5
      ,field: ''
      ,spread: true
      ,children: [{
        title: '三级2-1-1'
        ,id: 11
        ,field: ''
      },{
        title: '三级2-1-2'
        ,id: 12
        ,field: ''
      }]
    },{
      title: '二级2-2'
      ,id: 6
      ,field: ''
      ,children: [{
        title: '三级2-2-1'
        ,id: 13
        ,field: ''
      },{
        title: '三级2-2-2'
        ,id: 14
        ,field: ''
        ,disabled: true
      }]
    }]
  }]
  //基本演示
<#--  tree.render({-->
<#--    elem: '#test12'-->
<#--    ,data: data-->
<#--    ,showCheckbox: false  //是否显示复选框-->
<#--    ,id: 'demoId1'-->
<#--    ,isJump: false //是否允许点击节点时弹出新窗口跳转-->
<#--    ,click: function(obj){-->
<#--      var data = obj.data;  //获取当前点击的节点数据-->
<#--      layer.msg('状态：'+ obj.state + '<br>节点数据：' + JSON.stringify(data));-->
<#--    }-->
<#--  });-->

   tree.render({
    elem: '#test4'
    ,data: data
    ,isJump: true  //link 为参数匹配
  });
});


    //表格数据
    layui.use(['laydate', 'table', 'layer', 'global','laypage','layer'], function () {
        var table = layui.table, layer = layui.layer, $ = layui.$, laydate = layui.laydate,laypage = layui.laypage,layer = layui.layer;
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
            url: '/exTableMapping/pageData', //数据接口
            where: {},
            even:true,
            skin:'nob',
            textalign:'center',
             done: function (res, curr, count) {
                    tablePage(res.count)
                },
//                page: true, //开启分页
            cols: [[ //表头
                {type: 'checkbox'},
                {field: 'taskName', title: '名称', minWidth:150},
                {field: 'sourceDb', title: '标识', minWidth:100},
                {field: 'sourceUser', title: '编码', minWidth:100},
                {field: 'sourceTable', title: '元模型名称', minWidth:150},
                {field: 'destDb', title: '数据域/数据源', minWidth:120},
                {field: 'destUser', title: '最新操作时间', minWidth:120},
                {field: 'destTable', title: '关联度', minWidth:100},
                {field: 'createTime', title: '审核状态', minWidth:170},
                {
                    title: '操作',fixed: 'right', minWidth:200, toolbar: '<div>' +
                '<a class="layui-btn layui-btn-xs layui-btn-primary primary" lay-event="collect">采集</a>' +
                '<a class="layui-btn layui-btn-xs layui-btn-primary" lay-event="detail">详情</a>' +
//                '<a class="layui-btn layui-btn-xs layui-btn-primary" lay-event="edit" style="color: #4FBEBF; border-color: #4FBEBF">编辑</a>' +
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
                where: fields
            });
        };

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
                    url: '/exTableMapping/taskPage/detailPage',
                    data: {id: data.id},
                    async: false,
                    success: function (data, status, xhr) {
                        layer.open({
                            type: 1,
                            title: '详情',
                            area: ['950px', '650px'],
                            content: data //注意，如果str是object，那么需要字符拼接。
                        });
                    }
                });
            } else if (layEvent === 'del') { //删除
                layer.confirm('确认删除？', function (index) {
                    $.ajax({
                        url: '/exTableMapping/delete',
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
                location.href="taskPage/editTask?id="+data.id+"&gatherDesc="+data.gatherDesc+"&taskName="+data.taskName+"&sourceDb="+data.sourceDb+"&sourceTable="+data.sourceTable+"&destDb="+data.destDb+"&destTable="+data.destTable;
              /*  $.ajax({
                    url: '/jump/editTask',
                    data: {id: data.id,gatherDesc:data.gatherDesc,taskName:data.taskName,sourceDb:data.sourceDb,sourceTable:data.sourceTable,destDb:data.destDb,destTable:data.destTable},
                    async: false,
                    success: function (data, status, xhr) {
                        layer.open({
                            type: 1,
                            title: '编辑',
                            area: ['750px', '500px'],
                            content: data
                        });
                    }
                });*/
            } else if(layEvent === 'collect'){
                location.href="/exClassifyDict/jobData"

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
            location.href="jump/additionalTasks";
         /*   $.ajax({
                url: '/exTableMapping/newPage',
                async: false,
                success: function (data, status, xhr) {
                    layer.open({
                        type: 1,
                        title: '新建',
                        area: ['750px', '500px'],
                        content: data
                    });
                }
            });*/
        });

        $('#delItems').click(function () {
            var checkStatus = table.checkStatus('dataTable'), data = checkStatus.data;
            layer.confirm('确认批量删除？', function (index) {
                $.ajax({
                    url: '/exTableMapping/deleteBatch',
                    data: {ids: data.map(item => item.id)},
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
    function edit() {

    }
</script>
</@js>