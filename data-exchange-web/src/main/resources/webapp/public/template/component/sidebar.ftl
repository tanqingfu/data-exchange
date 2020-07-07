<div class="layui-side layui-bg-black">
    <div class="layui-side-scroll">
        <div class="layui-logo">
            <img src="../../static/img/logo.png" alt="">
        </div>
        <!-- 左侧导航区域（可配合layui已有的垂直导航） -->
        <ul class="layui-nav layui-nav-tree" id="menu-tree">
        ${menuHtml!''}
        </ul>
    </div>
</div>
<script>
    //console.log(${menuHtml})
    layui.use(['element', 'global'], function () {
        var $ = layui.$, element = layui.element;

        $(".layui-nav-tree .layui-nav-item").click(function () {
            $(this).slideDown(300);

        });

        $(function () {
            // 获取当前页面路径
            var pathname = window.location.pathname
            // a链接与当前页面路径匹配
            $('.layui-nav a').each(function () {
                if ($(this).attr('href') == pathname || ($(this).attr('href') != '/' && pathname.indexOf($(this).attr('href')) > -1)) {
                    // 父元素添加选中class
                    $(this).parent().addClass('layui-this')
                    if (!$(this).parent().hasClass('layui-nav-item')) {
                        // 父元素非li时往上找到li并添加展开class
                        $(this).parents('.layui-nav-item').addClass('layui-nav-itemed')
                    }
                }
            })
        })

    });

</script>
<style>
    #menu-tree .layui-icon {
        padding-right: 10px;
    }

</style>