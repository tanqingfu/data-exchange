<#include "../../layout/mainLayout.ftl">

<link rel="stylesheet" href="/static/module/add.css">
<link href="/static/module/common.css" rel="stylesheet">
<link href="/static/module/index1.css" rel="stylesheet">
<@layout>
    <div style="padding: 20px;" class="content">
        <div class="content-table">
            <div class="content-input layui-fluid layui-col-space10 clearfix">
                <div class="line">
                    <span class="label">同步账号</span>
                    <input type="text" name="account" id="account" lay-verify="title" autocomplete="off"
                           placeholder="请输入同步账号" class="layui-input">
                </div>
                <div class="line">
                    <button type="button" class="layui-btn" onclick="window.reloadTable()" id="queryForm">查询</button>
                </div>
                <div class="line right">
                    <button type="button" class="layui-btn layui-btn-primary btn layui-btn-disabled" id="delItems"
                            disabled><i class="layui-icon">&#xe640;</i>删除
                    </button>
                    <button type="button" class="layui-btn s-btn" id="newGatherDict"
                            onclick="window.location.href = '/exSyncmonUserinfo/page/newPage'"><i
                                class="layui-icon">&#xe608;</i>新建
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
        layui.use(['laydate', 'table', 'layer', 'global', 'laypage'], function () {
            var table = layui.table, layer = layui.layer, $ = layui.$, laydate = layui.laydate, laypage = layui.laypage,
                form = layui.form;
            // 表格
            var dataTable = table.render({
                elem: '#dataTable',
                url: '/exSyncmonUserinfo/pageData', //数据接口
                where: {},
                // even:true,
                skin: 'line',
                done: function (res, curr, count) {
                    tablePage(res.count)
                },
                cols: [[ //表头
                    {type: 'checkbox'},
                    {field: 'syncdbUser', title: '同步账号', minWidth: 80},
                    // {field: 'syncDesc', title: '同步账号名称'},
                    {field: 'syncdbName', title: '数据库名称', minWidth: 80},
//                   {field: 'syncdbId', title: '同步账号id', minWidth:100},
                    // {field: 'syncdbType', title: '数据库类型', minWidth: 100},
                    {field: 'syncdbIp', title: '同步IP地址', minWidth: 80},
                    {field: 'syncdbPort', title: '同步端口', minWidth: 80},
                    // {field: 'syncdbPasswd', title: '同步密码'},
                    {field: 'createTime', title: '创建时间', minWidth: 80},
                     {field: 'gatherDesc', title: '所属交换节点',minWidth:80},
                    {
                        field: 'validFlag',
                        title: '状态',
                        align: 'center',
                        fixed: 'right',
                        minWidth: 100,
                        templet: function (d) {
                            return '<input type="checkbox" name="flag" value="' + d.syncdbId + '" lay-skin="switch" lay-text="启用|禁用" lay-filter="flagDemo" ' + (d.validFlag === '启用中' ? 'checked' : '') + '>'
                        }
                    },
                    {
                        title: '操作', fixed: 'right', minWidth: 160, templet: function (d) {
                            return '<div>' +
                                '<a class="layui-btn layui-btn-xs layui-btn-primary primary" lay-event="detail">详情</a>' +
                                '<a class="layui-btn layui-btn-xs layui-btn-primary" lay-event="edit">编辑</a>' +
                                '<a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>' +
                                '</div>';
                        }
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
                var fields = {"account": $("#account").val(), current: curr || 1, size: limit || 10};
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

            // 监听启用/禁用
            form.on('switch(flagDemo)', function (obj) {
                var url = '/exSyncmonUserinfo/start',
                    tip = '确定启用该同步账号吗？',
                    syncdbId = this.value
                var syncdbId = this.value
                if (!obj.elem.checked) {
                    url = '/exSyncmonUserinfo/stop'
                    tip = '确定禁用该同步账号吗？'
                }
                layer.confirm(tip, {icon: 3, title: '提示'}, function (index) {
                    layer.close(index);
                    $.ajax({
                        url: url,
                        data: {"syncdbId": syncdbId},
                        type: "POST",
                        success: function () {
                            reloadTable();
                        }
                    })
                }, function (index) {
                    if (!obj.elem.checked) {
                        $(obj.elem).attr('checked',true);
                    }else{
                        $(obj.elem).removeAttr('checked');
                    }
                    form.render()
                });

            });

            // 监听表格操作栏
            table.on('tool(dataTable)', function (obj) { //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
                var data = obj.data; //获得当前行数据
                var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
                var tr = obj.tr; //获得当前行 tr 的DOM对象
                if (layEvent === 'detail') { //详情
                    $.ajax({
                        url: '/exSyncmonUserinfo/page/detailPage',
                        data: {syncdbId: data.syncdbId},
                        async: false,
                        success: function (data, status, xhr) {
                            layer.open({
                                type: 1,
                                title: '详情',
                                resize: false,
                                area: ['700px', '400px'],
                                content: data //注意，如果str是object，那么需要字符拼接。
                            });
                        }
                    });
                } else if (layEvent === 'del') { //删除
                    layer.confirm('确定要删除该条数据吗？', function (index) {
                        $.ajax({
                            url: '/exSyncmonUserinfo/delete',
                            data: {syncdbId: data.syncdbId},
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
                    location.href="/exSyncmonUserinfo/page/updatePage?syncdbId="+data.syncdbId;
                } else if (layEvent === 'start') {
                    $.ajax({
                        url: '/exSyncmonUserinfo/start',
                        data: {"syncdbId": data.syncdbId},
                        type: "POST",
                        success: function () {
                            reloadTable();
                        }
                    })
                } else if (layEvent === 'stop') {
                    $.ajax({
                        url: '/exSyncmonUserinfo/stop',
                        data: {"syncdbId": data.syncdbId},
                        type: "POST",
                        success: function () {
                            reloadTable();
                        }
                    })
                }
            });

            // 监听复选框
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

            // 新建
            $('#newItem').click(function () {
                $.ajax({
                    url: '/exSyncmonUserinfo/page/newPage',
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
                        url: '/exSyncmonUserinfo/deleteBatch',
                        data: {syncdbId: data.map(item => item.syncdbId)},
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