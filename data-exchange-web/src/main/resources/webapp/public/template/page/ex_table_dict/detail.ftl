<#include "../../layout/dlgLayout.ftl">
<@layout>
    <form class="layui-form" id="updateItemForm">
        <input type="hidden" name="syncId" value="${param.syncId}">
        <div class="layui-form-item">
            <label class="layui-form-label">库描述：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.dbDesc!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">表名：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.dbTable!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">创建时间：</label>
            <div class="layui-input-block">
                <span class="detail-item"><#if param.createTime??>${param.createTime?datetime}<#else></#if></span>
            </div>
        </div>
        <#--<div class="layui-form-item">-->
            <#--<label class="layui-form-label">修改时间：</label>-->
            <#--<div class="layui-input-block">-->
                <#--<span class="detail-item"><#if param.modifyTime??>${param.modifyTime?datetime}<#else></#if></span>-->
            <#--</div>-->
        <#--</div>-->
        <div class="layui-form-item">
            <label class="layui-form-label">有效标志：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.validFlag!''}</span>
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