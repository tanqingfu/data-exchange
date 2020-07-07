<#include "../../layout/mainLayout.ftl">
<link href="/static/module/common.css" rel="stylesheet">
<link href="/static/module/index1.css" rel="stylesheet">

<@layout>
<div style="padding: 20px;" class="content">
    <div style=" height: 20px;clear: both">
        <h4 style="float: left">
            <span><a href="/jump/gatherDict" style="color:#999">交换节点信息></a>
        </span>编辑</h4>
        <button type="button" class="layui-btn layui-btn-primary layui-btn-sm back" style="float: right">返回</button>
    </div>
    <div class="content-table" style="overflow:auto;height:100%">
        <form class="layui-form" action="/exGatherDict/update" method="post" id="updateItemForm"
              style="margin-top:60px">
            <input type="hidden" name="gatherId" value="${param.gatherId}">

            <div class="layui-form-item" style="clear: both; width: 100%;">
                <label class="layui-form-label" style="width:40%;"><span class="input-required">*</span>交换节点名称：</label>
                <div class="layui-input-block">
                    <input type="text" name="gatherDesc" value="${param.gatherDesc!''}" class="layui-input"
                           lay-verify="required" style="width: 30%;">
                </div>
            </div>
            <div class="layui-form-item" style="clear: both; width: 100%;">
                <label class="layui-form-label" style="width:40%;"><span class="input-required">*</span>服务器Ip：</label>
                <div class="layui-input-block">
                    <input type="text" name="serviceIp" value="${param.serviceIp!''}" class="layui-input"
                           lay-verify="required" style="width: 30%;">
                </div>
            </div>
            <div class="layui-form-item" style="clear: both; width: 100%;">
                <label class="layui-form-label" style="width:40%;"><span class="input-required">*</span>ssh端口：</label>
                <div class="layui-input-block">
                    <input type="text" name="sshPort" value="${param.sshPort!''}" class="layui-input"
                           lay-verify="required" style="width: 30%;">
                </div>
            </div>
            <div class="layui-form-item" style="clear: both; width: 100%;">
                <label class="layui-form-label" style="width:40%;"><span class="input-required">*</span>ssh用户名：</label>
                <div class="layui-input-block">
                    <input type="text" name="sshUser" id="sshUser" value="${param.sshUser!''}" class="layui-input"
                           lay-verify="required" style="width: 30%;">
                </div>
            </div>
            <div class="layui-form-item" style="clear: both; width: 100%;">
                <label class="layui-form-label" style="width:40%;"><span class="input-required">*</span>服务器密码：</label>
                <div class="layui-input-block">
                    <input type="password" name="sshPassword" value="${param.sshPassword!''}" class="layui-input"
                           lay-verify="required" style="width: 30%;">
                </div>
            </div>
            <div class="layui-form-item" style="clear: both; width: 100%;">
                <label class="layui-form-label" style="width:40%;"><span class="input-required">*</span>服务器描述：</label>
                <div class="layui-input-block">
                    <input type="text" name="serviceDesc" id="serviceDesc" value="${param.serviceDesc!''}"
                           class="layui-input" lay-verify="required" style="width: 30%;">
                </div>
            </div>
            <div class="layui-form-item" style="clear: both; width: 100%;">
                <div class="layui-input-block submit-btn-area">
                    <button class="layui-btn " lay-submit="" onclick="onSuccess();">保存</button>
                    <button type="button" class="layui-btn layui-btn-primary" onclick="onClose();">取消</button>
                </div>
            </div>
        </form>
    </div>
</div>
<script>
    layui.use(['laydate', 'form', 'global', 'jquery.form.min'], function () {
        var laydate = layui.laydate, form = layui.form, $ = layui.$;
        laydate.render({
            elem: '#modifyTime_update',
            type: 'datetime',
        value: <#if param.modifyTime??>'${param.modifyTime?datetime}'<#else>''</#if>
        });
        $('#updateItemForm').ajaxForm({
            success: function (data) {
                if (data.success) {
                    layer.closeAll();
                    layer.msg("更新成功。", {
                        time: 1000
                    });
                    parent.reloadTable();

                }
            }
        });
        // 点击返回任务信息页面
        $('.back').click(function () {
            location.href = "/jump/gatherDict"
        })
        window.onClose = function () {
            layer.closeAll();
            location.href = "/jump/gatherDict"
        };
        window.onSuccess = function () {
            setTimeout("location.href = '/jump/gatherDict'", 1000);
        };
        form.render();
    });
</script>
</@layout>