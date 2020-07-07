<#include "../../layout/dlgLayout.ftl">
<@layout>
    <form class="layui-form" action="/exFieldDict/update" method="post" id="updateItemForm">
        <input type="hidden" name="syncSeqno" value="${param.syncSeqno}">
        <div class="layui-form-item">
            <label class="layui-form-label"><span class="input-required">*</span>同步表名：</label>
            <div class="layui-input-block">
                <input type="text" name="dbTable" value="${param.dbTable!''}" class="layui-input" lay-verify="required">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label"><span class="input-required">*</span>字段：</label>
            <div class="layui-input-block">
                <input type="text" name="syncField" value="${param.syncField!''}" class="layui-input" lay-verify="required">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label"><span class="input-required">*</span>字段类型：</label>
            <div class="layui-input-block">
                <input type="text" name="syncType" value="${param.syncType!''}" class="layui-input" lay-verify="required">
            </div>
        </div>
        <div class="layui-form-item">
            <div class="layui-input-block submit-btn-area">
                <button class="layui-btn" lay-submit="">保存</button>
                <button type="button" class="layui-btn layui-btn-primary" onclick="onClose();">取消</button>
            </div>
        </div>
    </form>
    <script>
        layui.use(['laydate', 'form', 'global', 'jquery.form.min'], function () {
            var laydate = layui.laydate, form = layui.form, $ = layui.$;
            laydate.render({
                elem: '#createTime_update',
                type: 'datetime',
                value: <#if param.createTime??>'${param.createTime?datetime}'<#else>''</#if>
            });
            $('#updateItemForm').ajaxForm({
                success: function (data) {
                    if (data.success) {
                        layer.closeAll();
                        layer.msg("更新成功。", {
                            time: 1000
                        });
                        parent.reloadTable();
                    }
                }
            });
            window.onClose = function () {
                layer.closeAll();
            };
            form.render();
        });
    </script>
</@layout>