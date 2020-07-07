<#list table.fields as field>
    <#if field.keyFlag>
        <#assign keyPropertyName="${field.propertyName}"/>
    </#if>
</#list>
${'<#include "../../layout/dlgLayout.ftl">'}
${'<@layout>'}
    <form class="layui-form" id="updateItemForm">
        ${'<input type="hidden" name="id" value="$\{param.id}">'}
<#list table.fields as field>
<#if field.keyFlag>
<#else>
        <div class="layui-form-item">
            <label class="layui-form-label">${field.comment!field.propertyName}：</label>
            <div class="layui-input-block">
<#if field.propertyType == 'Date'>
                <span class="detail-item">${'<#'}if param.${field.propertyName}??>${'$\{'}param.${field.propertyName}?datetime}${'<#'}else>${'</#'}if></span>
<#else>
                <span class="detail-item">${'$\{'}param.${field.propertyName}!''${'}'}</span>
</#if>
            </div>
        </div>
</#if>
</#list>
        <div class="layui-form-item">
            <div class="layui-input-block submit-btn-area">
                <button type="button" class="layui-btn layui-btn-primary" onclick="onClose();">关闭</button>
            </div>
        </div>
    </form>
    <script>
        layui.use(['form', 'global'], function () {
            var form = layui.form, $ = layui.$;
            window.onClose = function () {
                layer.closeAll();
            };
            form.render();
        });
    </script>
${'</@layout>'}