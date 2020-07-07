<#include "../../layout/dlgLayout.ftl">
<@layout>
    <style>
        .layout-form{
            margin-left: 0;
        }
        .layui-layer-page .layui-layer-content{
            overflow: visible;
            overflow-y: visible;
        }
    </style>
    <form class="layui-form layout-form" action="/exJobInfo/save" method="post" id="newItemForm">
        <div class="layui-form-item">
            <label class="layui-form-label"><span class="input-required">*</span>作业名称：</label>
            <div class="layui-input-block">
                <input type="text" name="jobName" class="layui-input" lay-verify="required">
            </div>
        </div>
        <div class="layui-form-item taskName">
            <label class="layui-form-label">选择任务：</label>
            <div class="layui-input-block">
                <select name="taskName" xm-select="example2_1" xm-select-skin="default" id="taskName">
                </select>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label"><span class="input-required">*</span>作业描述：</label>
            <div class="layui-input-block">
                <input type="text" name="jobDesc" class="layui-input" lay-verify="required">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">作业服务器：</label>
            <div class="layui-input-block">
                <select name="ip" lay-filter="ip" id="ip">
                </select>
            </div>
        </div>
        <div class="layui-input-block submit-btn-area">
            <button class="layui-btn" lay-submit="">保存</button>
            <button type="button" class="layui-btn layui-btn-primary" onclick="onClose();">取消</button>
        </div>
    </form>
    <script>


        layui.use(['laydate', 'form', 'global', 'jquery.form.min', 'formSelects'], function () {

            var laydate = layui.laydate, form = layui.form, $ = layui.$, formSelects = layui.formSelects;
            laydate.render({
                elem: '#createTime_new',
                type: 'datetime'
            });
            $('#newItemForm').ajaxForm({
                success: function (data) {
                    if (data.success) {
                        layer.closeAll();
                        layer.msg("新建成功。", {
                            time: 1000
                        });
                        parent.reloadTable();
                    }
                }
            });

            window.onClose = function () {
                layer.closeAll();

            };
            form.render();
            $('.layui-form').ready(function () {
                layui.use('form', function () {
                    var form = layui.form;
                    $.ajax({
                        url: '/exJobInfo/getAllTasks',
                        type: 'get',
                        success: function (data) {
                            // var option3 = "<option value='-1'>请选择任务</option>";//初始化option的选项
                            var option3 = ''
                            var sourceDbName = data;
                            for (var p = 0; p < data.length; p++) {
                                option3 += "<option value='" + data[p]["taskName"] + "'>" + data[p]["taskName"] + "</option>";//拼接option中的内容
                                $("#taskName").html(option3);//将option的拼接内容加入select中，注意选择器是select的ID
                            }
                            // form.render('select');//重点：重新渲染select
                            formSelects.render();
                        }
                    })


                    $.ajax({
                        url: '/exJobInfo/getAllIp',
                        type: 'get',
                        success: function (data) {
                            // var option3 = "<option value='-1'>请选择任务</option>";//初始化option的选项
                            var option = "<option value='-1'>请选择</option>", list = data.data
                            for (var p = 0; p < list.length; p++) {
                                option += "<option value='" + list[p]["ip"] +"'>" + list[p]["ip"] + "</option>";//拼接option中的内容
                                $("#ip").html(option);//将option的拼接内容加入select中，注意选择器是select的ID
                            }
                             form.render('select');//重点：重新渲染select

                        }
                    })
                });
            });
        });
    </script>
</@layout>