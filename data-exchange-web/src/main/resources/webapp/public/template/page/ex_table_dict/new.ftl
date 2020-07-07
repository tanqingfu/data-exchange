<#include "../../layout/dlgLayout.ftl">
<@layout>
<form class="layui-form" action="/exTableDict/save" method="post" id="newItemForm">
    <div class="layui-form-item">
        <label class="layui-form-label"><span class="input-required">*</span>库id：</label>
        <div class="layui-input-block">
            <input type="text" name="dbId" class="layui-input" lay-verify="required">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label"><span class="input-required">*</span>表名：</label>
        <div class="layui-input-block">
            <input type="text" name="dbTable" class="layui-input" lay-verify="required">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label"><span class="input-required">*</span>创建时间：</label>
        <div class="layui-input-block">
            <input type="text" name="createTime" id="createTime_new" class="layui-input" lay-verify="required" autocomplete="off">
        </div>
    </div>
    <#--<div class="layui-form-item">-->
        <#--<label class="layui-form-label"><span class="input-required">*</span>修改时间：</label>-->
        <#--<div class="layui-input-block">-->
            <#--<input type="text" name="modifyTime" id="modifyTime_new" class="layui-input" lay-verify="required" autocomplete="off">-->
        <#--</div>-->
    <#--</div>-->
    <div class="layui-form-item">
        <label class="layui-form-label"><span class="input-required">*</span>有效标志：</label>
        <div class="layui-input-block">
            <input type="text" name="validFlag" class="layui-input" lay-verify="required">
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