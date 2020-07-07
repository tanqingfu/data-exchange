<#macro layout cssPath=[] jsPath=[]>
<!DOCTYPE html>
<html lang="zh">
<head>
    <title>交换平台</title>
    <#include "../component/header.ftl">
    <style>
        .layui-layout-admin .layui-body {
            bottom: 0;
        }
    </style>
    <link rel="stylesheet" href="../../static/module/common.css">
    <link rel="stylesheet" href="../../static/module/formSelects-v4.css">
    <#list cssPath as item>
        <link rel="stylesheet" href="${item}">
    </#list>
    <#list jsPath as item>
        <script src="${item}"></script>
    </#list>
    <script src="../../static/layui/layui.js"></script>
    <script src="../../static/module/js/jquery.min.js"></script>

    <script>
        layui.config({
            base: '/static/module/' //你存放新模块的目录，注意，不是layui的模块目录
        }).extend({
            formSelects: 'formSelects-v4'
        });
    </script>
</head>
    <#include "../component/sidebar.ftl">
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
    <div class="layui-header">
        <div class="layui-nav layui-layout-left header">
            <h1>数据交换平台</h1>
        </div>
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

    <div class="layui-body">
        <#nested>
    </div>

</div>
</div>
</body>
<script>
    layui.use(['global', 'element','table'], function () {
        var $ = layui.$, element = layui.element, table = layui.table;
        window.logout = function () {
            $.ajax({
                url: '/sso/logout',
                method: 'get',
                success: function (data) {
                    window.location.href = "/"
                }
            })
        }
        //缓存当前操作的是哪个表格的哪个tr的哪个td
        $(document).off('mousedown','.layui-table-grid-down').on('mousedown','.layui-table-grid-down',function (event) {
            //直接记录td的jquery对象
            table._tableTrCurrr = $(this).closest('td');
        });

        //给弹出的详情里面的按钮添加监听级联的触发原始table的按钮的点击事件
        $(document).off('click','.layui-table-tips-main [lay-event]').on('click','.layui-table-tips-main [lay-event]',function (event) {
            var elem = $(this);
            var tableTrCurrr =  table._tableTrCurrr;
            if(!tableTrCurrr){
                return;
            }
            var layerIndex = elem.closest('.layui-table-tips').attr('times');
            layer.close(layerIndex);
            table._tableTrCurrr.find('[lay-event="' + elem.attr('lay-event') + '"]').first().click();
        });
    });

</script>
</html>
</#macro>
<#macro js>
    <#nested>
</#macro>
<#macro css>
    <#nested>
</#macro>