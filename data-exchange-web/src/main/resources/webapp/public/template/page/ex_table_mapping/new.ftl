<#include "../../layout/dlgLayout.ftl">
<@layout>
<form class="layui-form" action="/exTableMapping/save" method="post" id="newItemForm">
    <div class="layui-form-item">
        <label class="layui-form-label"><span class="input-required">*</span>交换任务名称：</label>
        <div class="layui-input-block">
            <input type="text" name="taskName" class="layui-input" lay-verify="required" style="width: 300px">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label"><span class="input-required">*</span>交换节点名称：</label>
        <div class="layui-input-block">
            <input type="text" name="gatherDesc" class="layui-input" lay-verify="required" style="width: 300px">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label"><span class="input-required">*</span>库源：</label>
        <div class="layui-input-block">
            <input type="text" name="sourceDb" class="layui-input" lay-verify="required">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label"><span class="input-required">*</span>源表名：</label>
        <div class="layui-input-block">
            <input type="text" name="destDb" class="layui-input" lay-verify="required">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label"><span class="input-required">*</span>目标库：</label>
        <div class="layui-input-block">
            <input type="text" name="destDb" class="layui-input" lay-verify="required">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label"><span class="input-required">*</span>目标表名：</label>
        <div class="layui-input-block">
            <input type="text" name="destTable" class="layui-input" lay-verify="required">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label"><span class="input-required">*</span>创建时间：</label>
        <div class="layui-input-block">
            <input type="text" name="createTime" id="createTime_new" class="layui-input" lay-verify="required" autocomplete="off">
        </div>
    </div>
    <div class="layui-form-item">
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