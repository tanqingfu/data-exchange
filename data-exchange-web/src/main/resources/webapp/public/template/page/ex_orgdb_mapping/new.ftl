<#include "../../layout/dlgLayout.ftl">
<@layout>
<form class="layui-form" action="/exOrgdbMapping/save" method="post" id="newItemForm">
    <div class="layui-form-item">
        <label class="layui-form-label"><span class="input-required">*</span>机构id：</label>
        <div class="layui-input-block">
            <input type="text" name="orgId" class="layui-input" lay-verify="required">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label"><span class="input-required">*</span>机构与库表描述信息：</label>
        <div class="layui-input-block">
            <input type="text" name="orgdbDesc" class="layui-input" lay-verify="required">
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