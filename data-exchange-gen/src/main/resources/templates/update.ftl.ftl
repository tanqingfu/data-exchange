<#list table.fields as field>
    <#if field.keyFlag>
        <#assign keyPropertyName="${field.propertyName}"/>
    </#if>
</#list>
${'<#include "../../layout/dlgLayout.ftl">'}
${'<@layout>'}
    <form class="layui-form" action="/${table.entityPath}/update" method="post" id="updateItemForm">
        ${'<input type="hidden" name="id" value="$\{param.id}">'}
<#list table.fields as field>
<#if field.keyFlag>
<#else>
        <div class="layui-form-item">
            <label class="layui-form-label"><span class="input-required">*</span>${field.comment!field.propertyName}：</label>
            <div class="layui-input-block">
<#if field.propertyType == 'Date'>
                <input type="text" name="${field.propertyName}" id="${field.propertyName}_update" class="layui-input" lay-verify="required" autocomplete="off">
<#else>
                <input type="text" name="${field.propertyName}" value="${'$\{'}param.${field.propertyName}!''${'}'}" class="layui-input" lay-verify="required">
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
                elem: '#createTime_update',
                type: 'datetime',
                ${'value: <#if param.createTime??>\'$\{param.createTime?datetime}\'<#else>\'\'</#if>'}
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
            window.onClose = function () {
                layer.closeAll();
            };
            form.render();
        });
    </script>
${'</@layout>'}