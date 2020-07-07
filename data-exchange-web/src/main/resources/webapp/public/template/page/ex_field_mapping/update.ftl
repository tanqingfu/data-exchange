<#include "../../layout/dlgLayout.ftl">
<@layout>
    <form class="layui-form" action="/exFieldMapping/update" method="post" id="updateItemForm">
        <input type="hidden" name="id" value="${param.id}">
        <div class="layui-form-item" style="width:100%">
            <label class="layui-form-label"><span class="input-required">*</span>源同步表名：</label>
            <div class="layui-input-block">
                <input type="text" name="sourceTable" value="${param.sourceTable!''}" class="layui-input" lay-verify="required">
            </div>
        </div>
        <div class="layui-form-item" style="width:100%">
            <label class="layui-form-label"><span class="input-required">*</span>源表字段名：</label>
            <div class="layui-input-block">
                <input type="text" name="sourceSyncname" value="${param.sourceSyncName!''}" class="layui-input" lay-verify="required">
            </div>
        </div>
        <div class="layui-form-item" style="width:100%">
            <label class="layui-form-label"><span class="input-required">*</span>函数-是否单独定义：</label>
            <div class="layui-input-block">
                <input type="text" name="sourceFunc" value="${param.sourceFunc!''}" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item" style="width:100%">
            <label class="layui-form-label"><span class="input-required">*</span>目标同步表名：</label>
            <div class="layui-input-block">
                <input type="text" name="destTable" value="${param.destTable!''}" class="layui-input" lay-verify="required">
            </div>
        </div>
        <div class="layui-form-item" style="width:100%">
            <label class="layui-form-label"><span class="input-required">*</span>目标表字段名：</label>
            <div class="layui-input-block">
                <input type="text" name="destSyncname" value="${param.destSyncName!''}" class="layui-input" lay-verify="required">
            </div>
        </div>
        <div class="layui-form-item" style="width:100%">
            <label class="layui-form-label"><span class="input-required">*</span>函数-是否单独定义：</label>
            <div class="layui-input-block">
                <input type="text" name="destFunc" value="${param.destFunc!''}" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item" style="width:100%">
            <label class="layui-form-label"><span class="input-required">*</span>是否有效：</label>
            <div class="layui-input-block">
                <input type="text" name="validFlag" value="${param.validFlag!''}" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item" style="width:100%">
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