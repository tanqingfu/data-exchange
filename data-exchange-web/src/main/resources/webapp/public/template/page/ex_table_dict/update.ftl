<#include "../../layout/dlgLayout.ftl">
<@layout>
    <form class="layui-form" action="/exTableDict/update" method="post" id="updateItemForm">
        <input type="hidden" name="syncId" value="${param.syncId}">
        <div class="layui-form-item">
            <label class="layui-form-label"><span class="input-required">*</span>库描述：</label>
            <div class="layui-input-block">
                <input type="text" name="dbDesc" value="${param.dbDesc!''}" class="layui-input" lay-verify="required">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label"><span class="input-required">*</span>表名：</label>
            <div class="layui-input-block">
                <input type="text" name="dbTable" value="${param.dbTable!''}" class="layui-input" lay-verify="required">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label"><span class="input-required">*</span>创建时间：</label>
            <div class="layui-input-block">
                <input type="text" name="createTime" id="createTime_update" class="layui-input" lay-verify="required" autocomplete="off">
            </div>
        </div>
        <#--<div class="layui-form-item">-->
            <#--<label class="layui-form-label"><span class="input-required">*</span>修改时间：</label>-->
            <#--<div class="layui-input-block">-->
                <#--<input type="text" name="modifyTime" id="modifyTime_update" class="layui-input" autocomplete="off">-->
            <#--</div>-->
        <#--</div>-->
        <div class="layui-form-item">
            <label class="layui-form-label"><span class="input-required">*</span>有效标志：</label>
            <div class="layui-input-block">
                <input type="text" name="validFlag" value="${param.validFlag!''}" class="layui-input">
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