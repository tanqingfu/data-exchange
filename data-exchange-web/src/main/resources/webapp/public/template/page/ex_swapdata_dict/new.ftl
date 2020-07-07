<#include "../../layout/dlgLayout.ftl">
<@layout>
<form class="layui-form" action="/exSwapdataDict/save" method="post" id="newItemForm">
    <div class="layui-form-item" style="width:100%">
        <label class="layui-form-label" style="width:37%"><span class="input-required">*</span>源库ID：</label>
        <div class="layui-input-block">
            <input type="text" name="sourcedbId" class="layui-input" lay-verify="required" style="width:60%">
        </div>
    </div>
    <div class="layui-form-item" style="width:100%">
        <label class="layui-form-label"  style="width:37%"><span class="input-required">*</span>目标库ID：</label>
        <div class="layui-input-block">
            <input type="text" name="destdbId" class="layui-input" lay-verify="required" style="width:60%">
        </div>
    </div>
    <div class="layui-form-item" style="width:100%">
        <label class="layui-form-label"  style="width:37%"><span class="input-required">*</span>用户名：</label>
        <div class="layui-input-block">
            <input type="text" name="userName" class="layui-input" lay-verify="required" style="width:60%">
        </div>
    </div>
    <div class="layui-form-item" style="width:100%">
        <label class="layui-form-label"  style="width:37%"><span class="input-required">*</span>源表名：</label>
        <div class="layui-input-block">
            <input type="text" name="sourceName" class="layui-input" lay-verify="required" style="width:60%">
        </div>
    </div>
    <div class="layui-form-item" style="width:100%">
        <label class="layui-form-label"  style="width:37%"><span class="input-required">*</span>目标表名：</label>
        <div class="layui-input-block">
            <input type="text" name="destName" class="layui-input" lay-verify="required" style="width:60%">
        </div>
    </div>
    <div class="layui-form-item" style="width:100%">
        <label class="layui-form-label"  style="width:37%"><span class="input-required">*</span>交换的时间24小时制：</label>
        <div class="layui-input-block">
            <input type="text" name="swaData" class="layui-input" lay-verify="required" style="width:60%">
        </div>
    </div>
    <div class="layui-form-item" style="width:100%">
        <label class="layui-form-label"  style="width:37%"><span class="input-required">*</span>交换数据 总量：</label>
        <div class="layui-input-block">
            <input type="text" name="swaGross" class="layui-input" lay-verify="required" style="width:60%">
        </div>
    </div>
    <div class="layui-form-item" style="width:100%">
        <div class="layui-input-block submit-btn-area">
            <button class="layui-btn" lay-submit="">保存</button>
            <button type="button" class="layui-btn layui-btn-primary" onclick="onClose();">取消</button>
        </div>
    </div>
</form>
<script>
    layui.use(['laydate', 'form', 'global', 'jquery.form.min'], function () {
        var laydate = layui.laydate, form = layui.form, $ = layui.$;
        laydate.render({
            elem: '#createTime_new',
            type: 'datetime'
        });
        $('#newItemForm').ajaxForm({
            success: function (data) {
                if (data.success) {
                    layer.closeAll();
                    layer.msg("新建成功。", {
                        time:1000
                    });
                    parent.reloadTable();
                }
            }
        });
        window.onClose = function () {
            layer.closeAll();
        };
        form.render();
    });
</script>
</@layout>