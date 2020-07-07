<#include "../../layout/dlgLayout.ftl">
<@layout>
    <form class="layui-form" action="/exJobInfo/update" method="post" id="updateItemForm">
        <input type="hidden" name="jobId" value="${param.jobId}">
        <div class="layui-form-item">
            <label class="layui-form-label"><span class="input-required">*</span>作业名：</label>
            <div class="layui-input-block">
                <input type="text" name="jobName" value="${param.jobName!''}" class="layui-input" lay-verify="required">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label"><span class="input-required">*</span>作业描述：</label>
            <div class="layui-input-block">
                <input type="text" name="jobDesc" value="${param.jobDesc!''}" class="layui-input" lay-verify="required">
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
                        parent.flagReload();
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