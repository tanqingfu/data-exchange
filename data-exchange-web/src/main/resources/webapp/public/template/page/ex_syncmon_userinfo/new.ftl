<#include "../../layout/mainLayout.ftl">
<link rel="stylesheet" href="../../static/module/index1.css">
<link rel="stylesheet" href="../../static/module/add.css">
<@layout>
<div style="padding: 20px;" class="content">
    <div class="layui-breadcrumb clearfix" lay-separator=">">
        <a href="/exSyncmonUserinfo/page">数据源管理</a>
        <a><cite>新增同步管理账号</cite></a>
        <div class="right">
            <button type="button" class="layui-btn layui-btn-primary layui-btn-sm back">返回</button>
        </div>
    </div>
    <div class="add-from">
        <form class="layui-form layout-form" id="newItemForm" >
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="input-required">*</span>同步账号(数据库用户名)：</label>
                <div class="layui-input-block">
                    <input type="text" name="userName" id="userName" class="layui-input" lay-verify="required">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="input-required">*</span>同步密码：</label>
                <div class="layui-input-block">
                    <input type="password" name="passwd" id="passwd" class="layui-input" lay-verify="required">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="input-required">*</span>交换节点名称：</label>
                <div class="layui-input-block">
                    <select name="gatherDesc" lay-filter="gatherDesc" lay-verify="required" id="gatherDesc">
                    </select>
                </div>
            </div>

            <div class="layui-form-item">
                <label class="layui-form-label"> <span class="input-required">*</span>数据库名称：</label>
                <div class="layui-input-block">
                    <input type="text" name="dbName" id="dbName" class="layui-input" lay-verify="required">
                </div>
            </div>

            <div class="layui-form-item">
                <label class="layui-form-label"><span class="input-required">*</span>同步IP地址：</label>
                <div class="layui-input-block">
                    <input type="text" name="ip" id="ip" class="layui-input" lay-verify="required">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="input-required">*</span>同步端口：</label>
                <div class="layui-input-block">
                    <input type="text" name="port" id="port" class="layui-input" lay-verify="required">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="input-required">*</span>数据库类型：</label>
                <div class="layui-input-block">
                    <select name="dbType" lay-filter="dbType" id="dbType">
                        <option>ORACLE</option>
                        <option>MYSQL</option>
                        <option>SQLSERVER</option>
                        <option>DB2</option>
                    </select>
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="input-required">*</span>同步账号描述：</label>
                <div class="layui-input-block">
                    <input type="text" name="dbDesc" id="dbDesc" class="layui-input" lay-verify="required">
                </div>
            </div>
            <div class="layui-form-item mt40">
                <div class="layui-input-block">
                    <button class="layui-btn" type="button" lay-filter="*" lay-submit="">保存</button>
                    <button type="button" class="layui-btn layui-btn-primary close">取消
                    </button>
                </div>
            </div>
        </form>

    </div>
</div>
<script>
    $('.layui-form').ready(function () {
        layui.use('form', function () {
            var form = layui.form;
            $.ajax({
                url: "/exGatherDict/getAllGatherDesc",
                type: 'GET'
            })
                    .done(function (datas) {
                        var option = "<option value=''>请选择交换节点</option>";//初始化option的选项
                        for (var i = 0; i < datas.length; i++) {
                            option += "<option value='" + datas[i]['gatherId'] + "'>" + datas[i]['gatherDesc'] + "</option>";//拼接option中的内容
                        }
                        $("#gatherDesc").html(option);//将option的拼接内容加入select中，注意选择器是select的ID

                        form.render('select');//重点：重新渲染select
                    })
            form.on('submit(*)', function(data){
                addUserInfo()
            });
        });

        $('.back,.close').click(function () {
            location.href = "/exSyncmonUserinfo/page"
        })
    });

    function addUserInfo() {
        $.ajax({
            url: "/exSyncmonUserinfo/addUserInfo",
            type: "POST",
            data: {
                "gatherId": $("#gatherDesc").val(),
                "dbDesc": $("#dbDesc").val(),
                "dbName": $("#dbName").val(),
                "userName":$("#userName").val(),
                "passwd": $("#passwd").val(),
                "ip": $("#ip").val(),
                "port": $("#port").val(),
                "dbType": $("#dbType").val()
            },
            success: function (data) {
                layer.open({
                    content: '<div style="padding: 20px 100px;">' + data.data + '</div>'
                    , btn: '关闭全部'
                    , btnAlign: 'c' //按钮居中
                    , shade: 0.5 //不显示遮罩
                    , yes: function () {
                        layer.closeAll();
                        window.open('/exSyncmonUserinfo/page',"_self")
                    }
                });
            }
        })
    }
</script>
</@layout>
