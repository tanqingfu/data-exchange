<#include "../../layout/dlgLayout.ftl">
<@layout>
    <form class="layui-form" id="updateItemForm">
        <input type="hidden" name="id" value="${param.id}">
        <div class="layui-form-item" style="width:100%">
            <label class="layui-form-label" style="width:50%">源同步表名：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.sourceTable!''}</span>
            </div>
        </div>
        <div class="layui-form-item"  style="width:100%">
            <label class="layui-form-label" style="width:50%">源表字段名：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.sourceSyncName!''}</span>
            </div>
        </div>
        <div class="layui-form-item" style="width:100%">
            <label class="layui-form-label" style="width:50%">函数-是否单独定义：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.sourceFunc!''}</span>
            </div>
        </div>
        <div class="layui-form-item" style="width:100%">
            <label class="layui-form-label" style="width:50%">目标同步表名：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.destTable!''}</span>
            </div>
        </div>
        <div class="layui-form-item" style="width:100%">
            <label class="layui-form-label" style="width:50%">目标字段名：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.destSyncName!''}</span>
            </div>
        </div>
        <div class="layui-form-item" style="width:100%">
            <label class="layui-form-label" style="width:50%">函数-是否单独定义：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.destFunc!''}</span>
            </div>
        </div>
        <div class="layui-form-item" style="width:100%">
            <label class="layui-form-label" style="width:50%">是否有效：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.validFlag!''}</span>
            </div>
        </div>
        <div class="layui-form-item" style="width:100%">
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
</@layout>