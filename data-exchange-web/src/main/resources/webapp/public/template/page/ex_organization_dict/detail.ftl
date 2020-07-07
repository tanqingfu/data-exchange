<#include "../../layout/dlgLayout.ftl">
<@layout>
    <form class="layui-form" id="updateItemForm">
        <input type="hidden" name="id" value="${param.id}">
        <div class="layui-form-item">
            <label class="layui-form-label">组织机构代码：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.orgCode!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">组织机构名称：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.orgName!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">机构所属等级：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.orgLevel!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">组织机构上级id：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.orgParentid!''}</span>
            </div>
        </div>
        <div class="layui-form-item">
            <div class="layui-input-block ">
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