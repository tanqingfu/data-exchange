<#list table.fields as field>
    <#if field.keyFlag>
        <#assign keyPropertyName="${field.propertyName}"/>
    </#if>
</#list>
${'<#include "../../layout/dlgLayout.ftl">'}
${'<@layout>'}
<form class="layui-form" action="/${table.entityPath}/save" method="post" id="newItemForm">
<#list table.fields as field>
<#if field.keyFlag>
<#else>
    <div class="layui-form-item">
        <label class="layui-form-label"><span class="input-required">*</span>${field.comment!field.propertyName}：</label>
        <div class="layui-input-block">
<#if field.propertyType == 'Date'>
            <input type="text" name="${field.propertyName}" id="${field.propertyName}_new" class="layui-input" lay-verify="required" autocomplete="off">
<#else>
            <input type="text" name="${field.propertyName}" class="layui-input" lay-verify="required">
</#if>
        </div>
    </div>
</#if>
</#list>
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
${'</@layout>'}