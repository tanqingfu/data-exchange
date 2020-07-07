<#include "../../layout/dlgLayout.ftl">

<@layout>
    <form class="layui-form" id="updateItemForm">
        <input type="hidden" name="gatherId" value="${param.gatherId}">
        <div class="layui-form-item" style=" clear: both;width: 100%;">
            <label class="layui-form-label" style="width:50%;">交换节点描述：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.gatherDesc!''}</span>
            </div>
        </div>
        <div class="layui-form-item" style=" clear: both;width: 100%;">
            <label class="layui-form-label" style="width:50%;">服务器IP：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.serviceIp!''}</span>
            </div>
        </div>
        <div class="layui-form-item" style=" clear: both;width: 100%;">
            <label class="layui-form-label" style="width:50%;">服务器描述：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.serviceDesc!''}</span>
            </div>
        </div>

        <div class="layui-form-item" style=" clear: both;width: 100%;">
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