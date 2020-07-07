<#include "../../layout/dlgLayout.ftl">
<@layout>
    <form class="layui-form layout-form auto left" id="updateItemForm" style="width: 100%">
       <input type="hidden" name="id" value="${param.dbId}">

        <div class="layui-col-md6">
            <div class="layui-form-item">
                <label class="layui-form-label">数据库名称：</label>
                <div class="layui-input-block">
                    <span class="detail-item">${param.dbName!''}</span>
                </div>
            </div>
        </div>
        <div class="layui-col-md6">
            <div class="layui-form-item">
                <label class="layui-form-label">数据库类型：</label>
                <div class="layui-input-block">
                    <span class="detail-item">${param.dbType!''}</span>
                </div>
            </div>
        </div>
        <div class="layui-col-md6">
            <div class="layui-form-item">
                <label class="layui-form-label">IP地址：</label>
                <div class="layui-input-block">
                    <span class="detail-item">${param.dbIp!''}</span>
                </div>
            </div>
        </div>
        <div class="layui-col-md6">
            <div class="layui-form-item">
                <label class="layui-form-label">端口：</label>
                <div class="layui-input-block">
                    <span class="detail-item" id="port">${param.dbPort!''}</span>
                </div>
            </div>
        </div>
        <div class="layui-col-md6">
            <div class="layui-form-item">
                <label class="layui-form-label">用户名：</label>
                <div class="layui-input-block">
                    <span class="detail-item">${param.dbUser!''}</span>
                </div>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">同步账号：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.syncdbUser!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">数据库所属单位：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.orgdbDesc!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">数据库描述：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.dbDesc!''}</span>
            </div>
        </div>
<#--        <div class="layui-form-item ">-->
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