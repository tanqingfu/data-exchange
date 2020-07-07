<#macro layout>
<!DOCTYPE html>
<html lang="zh">
<head>
    <#include "../component/header.ftl">
    <style>
        .layui-layout-admin .layui-body {
            bottom: 0;
        }
    </style>
</head>
<body style="background-color: #f0f0f0">
    <div id="wrapper">
        <div style="display: flex; flex-direction: column;min-height: calc(100vh);">
            <!-- 内容主体区域 -->
            <div style="padding: 15px; flex: auto;">
                <#nested>
            </div>
            <#include "../component/footer.ftl">
        </div>
    </div>
    <script src="../../static/layui/layui.js"></script>
    <script>
        layui.config({
            base: '/static/module/' //你存放新模块的目录，注意，不是layui的模块目录
        });
        layui.use(['global'], function () {
            var $ = layui.$;
        });

    </script>
</body>
</html>
</#macro>
<#macro js>
    <#nested>
</#macro>
<#macro css>
    <#nested>
</#macro>