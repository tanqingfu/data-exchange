<#include "../../layout/dlgLayout.ftl">
<@layout>
    <form class="layui-form" id="updateItemForm">
        <input type="hidden" name="id" value="${param.id}">
        <div class="layui-form-item">
            <label class="layui-form-label">id串定义：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.idStr!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">id序号：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.idSeqno!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">id描述信息：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.idDesc!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <div class="layui-input-block ">
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