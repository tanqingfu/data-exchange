<#macro layout cssPath=[]>
<!DOCTYPE html>
<html lang="zh">
    <head>
        <#include "../component/header.ftl">
        <style>
        .layui-layout-admin .layui-body {
            bottom: 0;
        }
    </style>
        <link rel="stylesheet" href="../../static/layui/css/layui.css">
        <link rel="stylesheet" href="../../static/module/css/easyui.css">
        <link rel="stylesheet" href="../../static/module/common.css">
        <#list cssPath as item>
        <link rel="stylesheet" href="${item}">
    </#list>
    <script src="../../static/layui/layui.js"></script>
    <script src="../../static/module/js/jquery.min.js"></script>
    <script src="../../static/module/js/jquery.easyui.min.js"></script>

    <script>
        layui.config({
            base: '/static/module/' //你存放新模块的目录，注意，不是layui的模块目录
        });
    </script>
    </head>
<!--    <#include "../component/sidebar.ftl">-->
    <body class="layui-layout-body">
        <div class="layui-layout layui-layout-admin" >
            <div class="layui-header">
                <div class="layui-nav layui-layout-left header" >
                    <h1>数据交换平台</h1>
                </div>
                <!-- 头部区域（可配合layui已有的水平导航） -->
                <#--            <ul class="layui-nav layui-layout-left">-->
                <#--                <li class="layui-nav-item"><a href="">控制台</a></li>-->
                <#--                <li class="layui-nav-item"><a href="">商品管理</a></li>-->
                <#--                <li class="layui-nav-item"><a href="">用户</a></li>-->
                <#--                <li class="layui-nav-item">-->
                    <#--                    <a href="javascript:;">其它系统</a>-->
                    <#--                    <dl class="layui-nav-child">-->
                        <#--                        <dd><a href="">邮件管理</a></dd>-->
                        <#--                        <dd><a href="">消息管理</a></dd>-->
                        <#--                        <dd><a href="">授权管理</a></dd>-->
                        <#--                    </dl>-->
                    <#--                </li>-->
                <#--            </ul>-->
                <ul class="layui-nav layui-layout-right">
                    <li class="layui-nav-item">
                        <a href="javascript:;">
                            <img src="${userInfo.imgUrl!''}" class="layui-nav-img">
                            <span>${userInfo.name!''}</span>
                        </a>
                        <dl class="layui-nav-child" style="top:65px;">
                            <dd><a href="javascript:;" onclick="logout()">退出</a></dd>
                        </dl>
                    </li>
                </ul>
            </div>

            <div class="layui-body" style="z-index:99999">
                <#--<div style="display: flex; flex-direction: column;min-height: calc(100vh - 60px);">-->
                <!-- 内容主体区域 -->
                <#nested>
                <#--<#include "../component/footer.ftl">-->
            </div>

            </div>
        </div>
    </body>
    <script>
    layui.use(['global'], function () {
        var $ = layui.$,element = layui.element;
        window.logout = function () {
            $.ajax({
                url: '/sso/logout',
                method: 'get',
                success: function (data) {
                    window.location.href = "/"
                }
            })
        }
    });
</script>
</html>
<!--</#macro>-->
<!--<#macro js>-->
<!--<#nested>-->
<!--</#macro>-->
<!--<#macro css>-->
<!--<#nested>-->
<!--</#macro>-->