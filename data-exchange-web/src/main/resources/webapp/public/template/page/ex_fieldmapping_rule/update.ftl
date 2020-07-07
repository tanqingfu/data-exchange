<#include "../../layout/dlgLayout.ftl">
<@layout>
    <form class="layui-form" action="/exFieldmappingRule/update" method="post" id="updateItemForm">
        <input type="hidden" name="id" value="${param.id}">
        <div class="layui-form-item">
            <label class="layui-form-label"><span class="input-required">*</span>源数据库类型：</label>
            <div class="layui-input-block">
                <input type="text" name="sourceDbtype" value="${param.sourceDbtype!''}" class="layui-input" lay-verify="required">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label"><span class="input-required">*</span>目标数据库类型：</label>
            <div class="layui-input-block">
                <input type="text" name="destDbtype" value="${param.destDbtype!''}" class="layui-input" lay-verify="required">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label"><span class="input-required">*</span>字段源类型：</label>
            <div class="layui-input-block">
                <input type="text" name="sourceFieldtype" value="${param.sourceFieldtype!''}" class="layui-input" lay-verify="required">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label"><span class="input-required">*</span>字段目标类型：</label>
            <div class="layui-input-block">
                <input type="text" name="destFieldtype" value="${param.destFieldtype!''}" class="layui-input" lay-verify="required">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label"><span class="input-required">*</span>规则：</label>
            <div class="layui-input-block">
                <input type="text" name="rule" value="${param.rule!''}" class="layui-input">
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