<#include "../../layout/dlgLayout.ftl">
<@layout>
    <form class="layui-form" id="updateItemForm">
        <input type="hidden" name="id" value="${param.id}">
        <div class="layui-form-item">
            <label class="layui-form-label">库id：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.dbid!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">表id：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.tableid!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">字段个数：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.fieldNum!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">字段1：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.prmField1!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">字段2：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.prmField2!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">字段3：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.prmField3!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">字段4：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.prmField4!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">字段5：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.prmField5!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">字段6：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.prmField6!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">字段7：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.prmField7!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">字段8：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.prmField8!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">字段9：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.prmField9!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">字段10：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.prmField10!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
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