<#include "../../layout/dlgLayout.ftl">
<@layout>
    <form class="layui-form" id="updateItemForm">
        <input type="hidden" name="syncSeqno" value="${param.syncSeqno}">
        <div class="layui-form-item">
            <label class="layui-form-label">同步表名：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.dbTable!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">字段：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.syncField!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">字段类型：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.syncType!''}</span>
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