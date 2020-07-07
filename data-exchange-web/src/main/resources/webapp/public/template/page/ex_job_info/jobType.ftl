<#include "../../layout/dlgLayout.ftl">
<@layout>
<style>
    .layout-form {
        margin-left: 0;
    }

    .createTime {
        display: none;
    }
</style>
<form class="layui-form layout-form" action="/exJobInfo/start" method="post" id="newItemForm">

    <input type="hidden" name="jobId" class="jobId">
    <div class="layui-form-item">
        <label class="layui-form-label">选择迁移方式：</label>
        <div class="layui-input-block">
            <select name="flag" xm-select-skin="default" id="flag" lay-filter="flag">
                <option value="2" selected>全量迁移</option>
                <option value="1">增量迁移</option>
            </select>
        </div>
    </div>
    <div class="layui-form-item createTime">
        <label class="layui-form-label">选择增量开始时间：</label>
        <div class="layui-input-block">
            <input type="text" class="layui-input" id="createTime_new" placeholder="" autocomplete="off"
                   name="dealTime">
        </div>
    </div>

    <div class="layui-input-block submit-btn-area">
        <button class="layui-btn" lay-submit="">保存</button>
        <button type="button" class="layui-btn layui-btn-primary" onclick="onClose();">取消</button>
    </div>
</form>
<script>


    layui.use(['laydate', 'form', 'global', 'jquery.form.min', 'formSelects'], function () {

        var laydate = layui.laydate, form = layui.form, $ = layui.$, formSelects = layui.formSelects;
        laydate.render({
            elem: '#createTime_new',
            type: 'datetime'
        });
        window.onClose = function () {
            layer.closeAll();
            window.flagClose()
        };
        form.render();
//        reloadTable();

//        form.on('submit(*)', function(data){
//            reloadTable();
//            return false;
//        });

        $('#newItemForm').ajaxForm({
            success: function (data) {
                if (data.success) {
                    layer.closeAll();
                    layer.msg("作业启动成功", {
                        time: 1000
                    });
                    window.flagClose(1)
                }
            }
        });
        $('.jobId').val($('.jobStart').attr('data'))
        form.on('select(flag)', function (data) {
            if (data.value == '1') {
                $('.createTime').show()
            } else {
                $('.createTime').hide()
                $('#createTime_new').val('')
            }
        });
    });
</script>
</@layout>