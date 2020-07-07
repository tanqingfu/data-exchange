<#include "../../layout/mainLayout.ftl">
<link href="/static/module/common.css" rel="stylesheet">
<link href="/static/module/index1.css" rel="stylesheet">
<@layout>
<div style="padding: 20px;" class="content">
<#--    <h3>作业信息</h3>-->
    <div class="content-table">
        <div class="content-input layui-fluid layui-col-space10 clearfix">
            <div class="line">
                <span class="label">作业名称</span>
                <input type="text" name="jobName" id="jobName" lay-verify="title" autocomplete="off"
                       placeholder="请输入作业名称" class="layui-input">
            </div>
            <div class="line">
                <button type="button" class="layui-btn" onclick="window.reloadTable()" id="queryForm">查询</button>
            </div>
            <div class="line right">
                <button type="button" class="layui-btn layui-btn-primary btn layui-btn-disabled" id="delItems" disabled>
                    <i class="layui-icon">&#xe640;</i>删除
                </button>
                <button type="button" class="layui-btn" id="newItem"><i class="layui-icon">&#xe608;</i>新建作业</button>
            </div>
        </div>
        <div class="table">
            <table id="dataTable" lay-filter="dataTable">

            </table>
        </div>
        <div class="page">
            <div id="demo7"></div>
        </div>
    </div>
</div>
</@layout>
<@js>
<script>
    var data = null

    layui.use(['laydate', 'table', 'layer', 'global', 'laypage', 'element'], function () {
        var table = layui.table, layer = layui.layer, $ = layui.$, laydate = layui.laydate, laypage = layui.laypage,
                form = layui.form
        var element = layui.element;
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
            url: '/exJobInfo/pageData', //数据接口
            where: {},
            // even: true,
            skin: 'line',
            done: function (res, curr, count) {
                tablePage(res.count)
                element.init();
            },
            cols: [[ //表头
                {type: 'checkbox'},
                {field: 'jobName', title: '作业名称', minWidth: 150},
                // {field: 'jobDesc', title: '作业描述', minWidth: 150},
//                {field: 'validFlag', title: '有效标志'},
                {field: 'jobState', title: '作业状态', minWidth: 100},
//                {field: 'jobState', title: '输入', minWidth: 100},
//                {field: 'jobState', title: '输出', minWidth: 100},
//                {
//                    field: 'schedule', title: '运行进度', minWidth: 100,
//                    templet: function (d) {
//                        return '<div class="layui-progress layui-progress-big" lay-showPercent="yes" style="margin-top: 6px; width:150px"> <div class="layui-progress-bar" lay-percent="40%"></div> </div>';
//                    }
//                },
                {field: 'ip', title: '作业服务器', minWidth: 100},
                {
                    field: 'jobStatus', title: '作业控制', align: 'center', fixed: 'right', width: 170,
                    templet: function (d) {
                        return '<input type="checkbox" name="flag" value="' + d.jobId + '" lay-skin="switch" lay-text="开启|停止" lay-filter="flagDemo" ' + (d.jobStatus != 2 ? 'checked' : '') + '>' +
                                '<a title="继续" data="' + d.jobId + '" class="layui-btn layui-icon layui-icon-play layui-btn-xs proceed' + (d.jobStatus == 2 || d.jobState === '增量迁移正在作业' || d.jobState === '全量迁移正在作业' ? ' layui-btn-disabled' : '') + '" ' +
                                ' style="background-color: transparent; font-size:21px;' + (d.jobStatus == 2 || d.jobState === '增量迁移正在作业' || d.jobState === '全量迁移正在作业' ? ' color:lightgray;' : 'color:green;') + 'border:none;"></a>' +
                                '<a title="暂停" data="' + d.jobId + '" class="layui-btn layui-icon layui-icon-pause layui-btn-xs suspend' + (d.jobStatus == 2 || d.jobState === '增量迁移暂停作业' || d.jobState === '全量迁移暂停作业' ? ' layui-btn-disabled' : '') + '" ' +
                                ' style="background-color: transparent; font-size:21px;' + (d.jobStatus == 2 || d.jobState === '增量迁移暂停作业' || d.jobState === '全量迁移暂停作业' ? ' color:lightgray;' : 'color:red;') + 'border:none;"></a>'
                    }
                },
                {
                    title: '操作', fixed: 'right', width: 160, templet: function (d) {
                    return '<div>' +
                            '<a class="layui-btn layui-btn-xs layui-btn-primary" lay-event="detail"' +
                            ' style="color: #3986F4; border-color:#3986F4;margin-right:5px">详情</a>' +
                            '<a class="layui-btn layui-btn-xs layui-btn-primary" lay-event="edit">编辑</a>' +
                            '<a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="del">删除</a>' +
                            '</div>';
                }
                }
            ]]
        });

        // 输入输出
        <#--function getProgress() {-->
            <#--$.ajax({-->
                <#--url: 'exJobInfo/progress',-->
                <#--data: {"input":${param.input}, "output":${param.output}},-->
                <#--type: "GET",-->
                <#--success: (function (data) {-->
                    <#--var active = {-->
                        <#--loading: function (othis) {-->
                            <#--var DISABLED = 'layui-btn-disabled';-->
                            <#--if (othis.hasClass(DISABLED)) return;-->
                            <#--var n = 0, timer = setInterval(function () {-->
                                <#--n = n + Math.random() * 10 | 0;-->
                                <#--if (n > 100) {-->
                                    <#--n = 100;-->
                                    <#--clearInterval(timer);-->
                                    <#--othis.removeClass(DISABLED);-->
                                <#--}-->
                                <#--element.progress('demo', n + '%');-->
                            <#--}, 300 + Math.random() * 1000);-->

                            <#--othis.addClass(DISABLED);-->
                        <#--}-->
                    <#--};-->

                    <#--$('.site-demo-active').on('click', function () {-->
                        <#--var othis = $(this), type = $(this).data('type');-->
                        <#--active[type] ? active[type].call(this, othis) : '';-->
                    <#--}-->
                <#--})-->
            <#--});-->
        <#--}-->
        //分页

        var tablePage = function (page) {
            laypage.render({
                elem: 'demo7'
                , count: page
                , layout: ['prev', 'page', 'next', 'count']
                , curr: tablePageCurr
                , jump: function (obj, first) {
                    if (!first) {
                        tablePageCurr = obj.curr
                        window.reloadTable(false, obj.curr, obj.limit)
                    }
                }
            });
        }, tablePageCurr = 1


        window.reloadTable = function (reset = false, curr, limit) {
            var fields = {"jobName": $("#jobName").val(), current: curr || 1};
            $.each($('#queryForm').serializeArray(), function (i, field) {
                fields[field.name] = field.value;
            });
            dataTable.reload({
                where: fields,
                page: reset ? {
                    curr: 1 //重新从第 1 页开始
                } : undefined
            });
        };

        window.flagReload = function () {
            reloadTable(false, tablePageCurr);
        }
        form.on('switch(flagDemo)', function (obj) {
            var jobId = this.value
            if (obj.elem.checked) {
                window.flagClose = function (type) {
                    if (type) {
                        flagReload()
                    } else {
                        $(obj.elem).removeAttr('checked');
                        form.render();
                    }
                }
                $.ajax({
                    url: '/exJobInfo/jobType',
                    async: false,
                    success: function (data, status, xhr) {
                        layer.open({
                            type: 1,
                            content: '<div class="jobStart" data="' + jobId + '">' + data + '</div>',
                            title: '提示',
                            resize: false,
                            area: ['560px', '300px']
                        })
                    }
                });
            } else {
                $.ajax({
                    url: '/exJobInfo/stop',
                    data: {"jobId": jobId},
                    type: "POST",
                    success: function () {
                        reloadTable(false, tablePageCurr);
                    }
                })
            }

        });
        $(document).on('click', '.suspend', function (data) {
            if ($(this).hasClass('layui-btn-disabled')) return
            $.ajax({
                url: '/exJobInfo/suspend',
                data: {"jobId": $(this).attr('data')},
                type: "POST",
                success: function () {
                    reloadTable(false, tablePageCurr);
                }
            });
        })
        $(document).on('click', '.proceed', function (data) {
            if ($(this).hasClass('layui-btn-disabled')) return
            $.ajax({
                url: '/exJobInfo/proceed',
                data: {"jobId": $(this).attr('data')},
                type: "POST",
                success: function () {
                    reloadTable(false, tablePageCurr);
                }
            });
        })

        table.on('tool(dataTable)', function (obj) { //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
            data = obj.data; //获得当前行数据
            var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
            var tr = obj.tr; //获得当前行 tr 的DOM对象


            if (layEvent === 'detail') { //详情
                window.location = "/exJobInfo/jobPage/detailPage?id=" + data.jobId;
            } else if (layEvent === 'del') { //删除
                layer.confirm('确定要删除该条数据吗？', function (index) {
                    $.ajax({
                        url: '/exJobInfo/delete',
                        data: {id: data.jobId},
                        async: false,
                        success: function (data, status, xhr) {
                            if (data.success) {
                                layer.close(index);
                                reloadTable(false, tablePageCurr);
                            }
                        }
                    });
                });
            } else if (layEvent === 'edit') { //编辑
                $.ajax({
                    url: '/exJobInfo/jobPage/updatePage',
                    data: {id: data.jobId},
                    async: false,
                    success: function (data, status, xhr) {
                        layer.open({
                            type: 1,
                            title: '编辑',
                            area: ['750px', '500px'],
                            content: data,
                            yes: function (index) {
                                layer.close(index);
                                reloadTable(false, tablePageCurr);
                            }
                        });
                    }
                });
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


        $('#newItem').click(function () {
            $.ajax({
                url: '/exJobInfo/newPage',
                async: false,
                success: function (data, status, xhr) {
                    layer.open({
                        type: 1,
                        title: '新建',
                        area: ['600px', '380px'],
                        content: data
                    });
                }
            });
        });

        $('#delItems').click(function () {
            var checkStatus = table.checkStatus('dataTable'), data = checkStatus.data;
            layer.confirm('确认批量删除？', function (index) {
                $.ajax({
                    url: '/exJobInfo/deleteBatch',
                    data: {id: data.map(item => item.jobId)
            },
                async: false, traditional:true, success:
                function (data, status, xhr) {
                    if (data.success) {
                        layer.close(index);
                        layer.msg('删除成功', {
                            time: 1000
                        });
                        reloadTable(false, tablePageCurr);
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