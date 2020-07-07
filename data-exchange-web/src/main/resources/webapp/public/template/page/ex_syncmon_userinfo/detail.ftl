<#include "../../layout/dlgLayout.ftl">
<@layout>
    <form class="layui-form layout-form auto left layui-row" id="updateItemForm">
        <input type="hidden" name="id" value="${param.syncdbId}">
        <div class="layui-col-md6">
            <div class="layui-form-item">
                <label class="layui-form-label">同步账号：</label>
                <div class="layui-input-block">
                    <span class="detail-item">${param.syncdbUser!''}</span>
                </div>
            </div>
        </div>
        <div class="layui-col-md6">
            <div class="layui-form-item">
                <label class="layui-form-label">数据库名称：</label>
                <div class="layui-input-block">
                    <span class="detail-item">${param.syncdbName!''}</span>
                </div>
            </div>
        </div>
        <div class="layui-col-md6">
            <div class="layui-form-item">
                <label class="layui-form-label">数据库类型：</label>
                <div class="layui-input-block">
                    <span class="detail-item">${param.syncdbType!''}</span>
                </div>
            </div>
        </div>
        <div class="layui-col-md6">

            <div class="layui-form-item">
                <label class="layui-form-label">IP地址：</label>
                <div class="layui-input-block">
                    <span class="detail-item">${param.syncdbIp!''}</span>
                </div>
            </div>
        </div>
        <div class="layui-col-md6">

            <div class="layui-form-item">
                <label class="layui-form-label">端口：</label>
                <div class="layui-input-block">
                    <span class="detail-item">${param.syncdbPort!''}</span>
                </div>
            </div>
        </div>
        <div class="layui-col-md6">

            <div class="layui-form-item">
                <label class="layui-form-label">创建时间：</label>
                <div class="layui-input-block">
                    <span class="detail-item"><#if param.createTime??>${param.createTime?datetime}<#else></#if></span>
                </div>
            </div>
        </div>
        <div class="layui-col-md6">

            <div class="layui-form-item">
                <label class="layui-form-label">修改时间：</label>
                <div class="layui-input-block">
                    <span class="detail-item"><#if param.modifyTime??>${param.modifyTime?datetime}<#else></#if></span>
                </div>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">同步账号描述：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.syncDesc!''}</span>
            </div>
        </div>

<#--        <div class="layui-form-item mt40">-->
<#--            <div class="layui-input-block submit-btn-area">-->
<#--                <button type="button" class="layui-btn layui-btn-primary" onclick="onClose();">关闭</button>-->
<#--            </div>-->
<#--        </div>-->
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