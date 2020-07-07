<#include "../../layout/dlgLayout.ftl">
<@layout>
<form class="layui-form" action="/exTablePrimarykey/save" method="post" id="newItemForm">
    <div class="layui-form-item">
        <label class="layui-form-label"><span class="input-required">*</span>库id：</label>
        <div class="layui-input-block">
            <input type="text" name="dbid" class="layui-input" lay-verify="required">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label"><span class="input-required">*</span>表id：</label>
        <div class="layui-input-block">
            <input type="text" name="tableid" class="layui-input" lay-verify="required">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label"><span class="input-required">*</span>字段个数：</label>
        <div class="layui-input-block">
            <input type="text" name="fieldNum" class="layui-input" lay-verify="required">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label"><span class="input-required">*</span>字段1：</label>
        <div class="layui-input-block">
            <input type="text" name="prmField1" class="layui-input" lay-verify="required">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label"><span class="input-required">*</span>字段2：</label>
        <div class="layui-input-block">
            <input type="text" name="prmField2" class="layui-input" lay-verify="required">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label"><span class="input-required">*</span>字段3：</label>
        <div class="layui-input-block">
            <input type="text" name="prmField3" class="layui-input" lay-verify="required">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label"><span class="input-required">*</span>字段4：</label>
        <div class="layui-input-block">
            <input type="text" name="prmField4" class="layui-input" lay-verify="required">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label"><span class="input-required">*</span>字段5：</label>
        <div class="layui-input-block">
            <input type="text" name="prmField5" class="layui-input" lay-verify="required">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label"><span class="input-required">*</span>字段6：</label>
        <div class="layui-input-block">
            <input type="text" name="prmField6" class="layui-input" lay-verify="required">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label"><span class="input-required">*</span>字段7：</label>
        <div class="layui-input-block">
            <input type="text" name="prmField7" class="layui-input" lay-verify="required">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label"><span class="input-required">*</span>字段8：</label>
        <div class="layui-input-block">
            <input type="text" name="prmField8" class="layui-input" lay-verify="required">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label"><span class="input-required">*</span>字段9：</label>
        <div class="layui-input-block">
            <input type="text" name="prmField9" class="layui-input" lay-verify="required">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label"><span class="input-required">*</span>字段10：</label>
        <div class="layui-input-block">
            <input type="text" name="prmField10" class="layui-input" lay-verify="required">
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
            elem: '#createTime_new',
            type: 'datetime'
        });
        $('#newItemForm').ajaxForm({
            success: function (data) {
                if (data.success) {
                    layer.closeAll();
                    layer.msg("新建成功。", {
                        time:1000
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