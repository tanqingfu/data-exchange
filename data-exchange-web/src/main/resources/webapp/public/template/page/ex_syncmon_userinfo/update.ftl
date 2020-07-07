<#include "../../layout/mainLayout.ftl">
<link rel="stylesheet" href="../../../static/module/index1.css">
<link rel="stylesheet" href="../../../static/module/add.css">
<@layout>
<div class="content">
    <div class="layui-breadcrumb clearfix" lay-separator=">">
        <a href="/exSyncmonUserinfo/page">数据源管理</a>
        <a><cite>编辑同步账号</cite></a>
        <div class="right">
            <button type="button" class="layui-btn layui-btn-primary layui-btn-sm back">返回</button>
        </div>
    </div>.
    <div class="add-from">
        <form class="layui-form layout-form" action="/exSyncmonUserinfo/update" method="post" id="updateItemForm">
            <input type="hidden" name="syncdbId" value="${param.syncdbId}">
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="input-required">*</span>同步账号：</label>
                <div class="layui-input-block">
                    <input type="text" name="syncdbUser" value="${param.syncdbUser!''}" class="layui-input"
                           lay-verify="required">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="input-required">*</span>同步密码：</label>
                <div class="layui-input-block">
                    <input type="password" name="syncdbPasswd" value="${param.syncdbPasswd!''}" class="layui-input"
                           lay-verify="required">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="input-required">*</span>数据库名称：</label>
                <div class="layui-input-block">
                    <input type="text" name="syncdbName" value="${param.syncdbName!''}" class="layui-input"
                           lay-verify="required">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="input-required">*</span>数据库类型：</label>
                <div class="layui-input-block">
                    <select name="syncdbType" lay-filter="syncdbType" id="syncdbType">
                        <option>ORACLE</option>
                        <option>MYSQL</option>
                        <option>SQLSERVER</option>
                        <option>DB2</option>
                    </select>
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="input-required">*</span>同步ip地址：</label>
                <div class="layui-input-block">
                    <input type="text" name="syncdbIp" value="${param.syncdbIp!''}" class="layui-input"
                           lay-verify="required">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="input-required">*</span>同步端口：</label>
                <div class="layui-input-block">
                    <input type="text" name="syncdbPort" value="${param.syncdbPort!''}" class="layui-input"
                           lay-verify="required">
                </div>
            </div>

            <div class="layui-form-item">
                <label class="layui-form-label"><span class="input-required">*</span>同步账号描述：</label>
                <div class="layui-input-block">
                    <input type="text" name="syncDesc" value="${param.syncDesc!''}" class="layui-input"
                           lay-verify="required">
                </div>
            </div>
            <div class="layui-form-item mt40">
                <div class="layui-input-block">
                    <button class="layui-btn" lay-submit="">保存</button>
                <button type="button" class="layui-btn layui-btn-primary close">取消</button>
                </div>
            </div>
        </form>
    </div>
</div>
<script>
    layui.use(['layer', 'laydate', 'form', 'global', 'jquery.form.min'], function () {
        var laydate = layui.laydate, form = layui.form, layer = layui.layer, $ = layui.$;
        laydate.render({
            elem: '#createTime_update',
            type: 'datetime',
        value: <#if param.createTime??>'${param.createTime?datetime}'<#else>''</#if>
        });
        $('#updateItemForm').ajaxForm({
            success: function (data) {
                layer.open({
                    content: '<div style="padding: 20px 100px;">' + data.data + '</div>'
                    , btn: '确定'
                    , btnAlign: 'c' //按钮居中
                    , shade: 0.5//不显示遮罩
                    , yes: function () {
                        layer.closeAll();
                        window.open('/exSyncmonUserinfo/page', "_self")
                    }
                });
            }
        });
        // 点击返回数据源管理页面
        $('.back,.close').click(function () {
            location.href = "/exSyncmonUserinfo/page"
        })
        $('#syncdbType').val('${param.syncdbType}')
        form.render();
    });
</script>
</@layout>