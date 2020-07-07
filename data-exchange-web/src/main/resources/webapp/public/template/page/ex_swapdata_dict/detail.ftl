<#include "../../layout/dlgLayout.ftl">
<@layout>
    <form class="layui-form" id="updateItemForm">
        <input type="hidden" name="id" value="${param.id}">
        <div class="layui-form-item" style="width:100%">
            <label class="layui-form-label">源库ID：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.sourcedbId!''}</span>
            </div>
        </div>
        <div class="layui-form-item" style="width:100%">
            <label class="layui-form-label">目标库ID：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.destdbId!''}</span>
            </div>
        </div>
        <div class="layui-form-item" style="width:100%">
            <label class="layui-form-label">用户名：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.userName!''}</span>
            </div>
        </div>
        <div class="layui-form-item" style="width:100%">
            <label class="layui-form-label">源表名：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.sourceName!''}</span>
            </div>
        </div>
        <div class="layui-form-item" style="width:100%">
            <label class="layui-form-label">目标表名：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.destName!''}</span>
            </div>
        </div>
        <div class="layui-form-item" style="width:100%">
            <label class="layui-form-label">交换的时间24小时制：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.swaData!''}</span>
            </div>
        </div>
        <div class="layui-form-item" style="width:100%">
            <label class="layui-form-label">交换数据 总量：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.swaGross!''}</span>
            </div>
        </div>
        <div class="layui-form-item" style="width:100%">
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