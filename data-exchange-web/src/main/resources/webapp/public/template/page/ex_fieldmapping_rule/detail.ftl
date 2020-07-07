<#include "../../layout/dlgLayout.ftl">
<@layout>
    <form class="layui-form" id="updateItemForm">
        <input type="hidden" name="id" value="${param.id}">
        <div class="layui-form-item">
            <label class="layui-form-label">源数据库类型：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.sourceDbtype!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">目标数据库类型：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.destDbtype!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">字段源类型：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.sourceFieldtype!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">字段目标类型：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.destFieldtype!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">规则：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.rule!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <div class="layui-input-block">
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
</@layout>