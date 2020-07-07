<#include "../layout/loginLayout.ftl">
<@layout>
    <div class="layui-card" style="width: 400px;margin:auto;margin-top:100px;">
        <div class="layui-card-header">
            登录
        </div>
        <div class="layui-card-body">
            <form class="layui-form" action="/login/confirm" method="post" id="loginForm">
                <div class="layui-form-item">
                    <label class="layui-form-label"><span class="input-required">*</span>用户：</label>
                    <div class="layui-input-block">
                        <input type="text" name="username" class="layui-input" lay-verify="required">
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label"><span class="input-required">*</span>密码：</label>
                    <div class="layui-input-block">
                        <input type="password" id="password" class="layui-input" lay-verify="required">
                    </div>
                </div>
                <div class="layui-form-item">
                    <div class="layui-input-block">
                        <button class="layui-btn" lay-submit="">登录</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</@layout>

<@js>
    <script>
        layui.use(['global', 'sha256.min', 'jquery.form.min'], function () {
            var $ = layui.$;
            <#--window.login = function() {-->
            <#--    $.ajax({-->
            <#--        url: "/login/confirm",-->
            <#--        data: {-->
            <#--            username: $("input[name='username']").val(),-->
            <#--            password: $("input[name='password']").val()-->
            <#--        },-->
            <#--        method: 'post',-->
            <#--        success: function (data) {-->
            <#--            if (data.success) {-->
            <#--                window.location.href = '${targetUrl!'/'}'-->
            <#--            } else {-->
            <#--                layer.msg(data.message, {-->
            <#--                    time:2000-->
            <#--                });-->
            <#--            }-->
            <#--        }-->
            <#--    })-->
            <#--}-->
            $('#loginForm').ajaxForm({
                data: {
                    password: function () {
                        var loginSeed = null;
                        $.ajax({
                            url :'/login/token',
                            data:{},
                            async : false,
                            success:function (data) {
                                if (data.success) {
                                    loginSeed = data.data;
                                }
                            }
                        });
                        var password = sha256(sha256($('#password').val()) + loginSeed);
                        $(this).clearForm();
                        return password;
                    }
                },
                success: function (data) {
                    if (data.success) {
                        window.location.href = '${targetUrl!'/'}'
                    } else {
                        // changeCode();
                        layer.msg(data.message, {
                            time:2000
                        });
                    }
                }
            });
        });
    </script>
</@js>