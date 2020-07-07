<#include "../../layout/dlgLayout.ftl">
<@layout>
    <form class="layui-form" action="/exTableMapping/update" method="post" id="updateItemForm">
        <input type="hidden" name="id" value="${param.id}">
        <div class="layui-form-item" style="width:100%">
            <label class="layui-form-label" style="width:40%"><span class="input-required">*</span>交换任务名称：</label>
            <div class="layui-input-block">
                <input type="text" name="taskName" value="${param.taskName!''}" class="layui-input" lay-verify="required" style="width:200px">
            </div>
        </div>
        <div class="layui-form-item" style="width:100%">
            <label class="layui-form-label" style="width:40%"><span class="input-required">*</span>交换节点描述：</label>
            <div class="layui-input-block">
                <input type="text" name="gatherDesc" value="${param.gatherDesc!''}" class="layui-input" lay-verify="required" style="width:200px">
            </div>
        </div>
        <div class="layui-form-item" style="width:100%">
            <label class="layui-form-label" style="width:40%"><span class="input-required">*</span>源库描述：</label>
            <div class="layui-input-block">
                <input type="text" name="sourceTable" value="${param.sourceDb!''}" class="layui-input" lay-verify="required" style="width:200px">
            </div>
        </div>
        <div class="layui-form-item" style="width:100%">
            <label class="layui-form-label" style="width:40%"><span class="input-required">*</span>库源表名：</label>
            <div class="layui-input-block">
                <input type="text" name="sourceTable" value="${param.sourceTable!''}" class="layui-input" lay-verify="required" style="width:200px">
            </div>
        </div>
        <div class="layui-form-item" style="width:100%">
            <label class="layui-form-label" style="width:40%"><span class="input-required">*</span>目标库描述：</label>
            <div class="layui-input-block">
                <input type="text" name="sourceTable" value="${param.destDb!''}" class="layui-input" lay-verify="required" style="width:200px">
            </div>
        </div>
        <div class="layui-form-item" style="width:100%">
            <label class="layui-form-label" style="width:40%"><span class="input-required">*</span>目标表名：</label>
            <div class="layui-input-block">
                <input type="text" name="destTable" value="${param.destTable!''}" class="layui-input" lay-verify="required" style="width:200px">
            </div>
        </div>
        <#--<div class="layui-form-item" style="width:100%">-->
            <#--<label class="layui-form-label" style="width:40%"><span class="input-required">*</span>修改时间：</label>-->
            <#--<div class="layui-input-block">-->
                <#--<input type="text" name="modifyTime" id="modifyTime_update" class="layui-input" autocomplete="off" style="width:200px">-->
            <#--</div>-->
        <#--</div>-->
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
            laydate.render({
                elem: '#modifyTime_update',
                type: 'datetime',
                value: <#if param.modifyTime??>'${param.modifyTime?datetime}'<#else>''</#if>
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