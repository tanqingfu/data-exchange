<#include "../../layout/mainLayout.ftl">
<link rel="stylesheet" href="../../static/module/index1.css">
<link rel="stylesheet" href="../../static/module/add.css">
<@layout>
<div class="content">
    <div class="layui-breadcrumb clearfix" lay-separator=">">
        <a href="/exDbMapping/page">数据交换</a>
        <a><cite>数据源映射配置</cite></a>
        <div class="right">
            <button type="button" class="layui-btn layui-btn-primary layui-btn-sm back">返回</button>
        </div>
    </div>
    <div class="add-from">
        <form class="layui-form layout-form">
            <div class="layui-form-item">
                <label class="layui-form-label">选择交换节点：</label>
                <div class="layui-input-block">
                    <select name="gatherDesc" lay-filter="gatherDesc" id="gatherDesc">
                    </select>
                </div>
            </div>
        </form>
        <div class="choice-list mt20">
            <div class="list">
                <div class="line">
                    <div class="title">源数据库</div>
                    <ul class="sourceDbList">

                    </ul>
                    <div class="empty mt40">暂无选项</div>
                </div>
            </div>
            <div class="list">
                <div class="line">
                    <div class="title">目标数据库</div>
                    <ul class="destDbList">

                    </ul>
                    <div class="empty mt40">暂无选项</div>
                </div>
            </div>
        </div>
        <div class="submit-btn-area">
            <button type="button" class="layui-btn layui-btn-normal" lay-filter="*" lay-submit="">保存
            </button>
            <button type="button" class="layui-btn layui-btn-primary "
                    onclick="window.location.href='/exDbMapping/page'">取消
            </button>
        </div>

    </div>
</div>
</@layout>

<script>
    //源库用户名
    layui.use(['form', 'layer', 'formSelects'], function () {
        var form = layui.form, layer = layui.layer, formSelects = layui.formSelects;

        // 获取交换节点数据
        $('.layui-form').ready(function () {
            $.ajax({
                url: "/exGatherDict/getAllGatherDesc",
                type: 'GET'
            }).success(function (datas) {
                var option = "<option value='-1'>请选择交换节点</option>";//初始化option的选项
                for (var i = 0; i < datas.length; i++) {
                    option += "<option value='" + datas[i]['gatherId'] + "'>" + datas[i]['gatherDesc'] + "</option>";//拼接option中的内容
                }
                $("#gatherDesc").html(option);//将option的拼接内容加入select中，注意选择器是select的ID
                form.render('select');//重点：重新渲染select
            });
        });
        //监听交换节点下拉框
        form.on('select(gatherDesc)', function (data) {
            $.ajax({
                url: "/exDbDict/getAllDatabase",
                type: 'GET'
            }).success(function (datas) {
                var list1 = '', list2 = '';
                // 处理渲染源数据库、目标数据库
                for (var i = 0; i < datas.length; i++) {
                    if (datas[i]['syncdbId'] != '-1') {
                        list1 += '<li data="' + datas[i].dbId + '">' +
                                '<div class="name">' + datas[i].db + '</div>' +
                                '<div class="info">IP：<span class="ip">' + datas[i].dbIp + '</span> port：<span class="port">' + datas[i].dbPort+ '</span> user：<span class="user">' + datas[i].dbUser  + '</span></div>' +
                                '<i class="layui-icon layui-icon-ok ok"></i>' +
                                '</li>'
                    }
                    list2 += '<li data="' + datas[i].dbId + '">' +
                            '<div class="name">' + datas[i].db + '</div>' +
                            '<div class="info">IP：<span class="ip">' + datas[i].dbIp + '</span> port：<span class="port">' + datas[i].dbPort + '</span></div>' + '</span> user：<span class="user">' + datas[i].dbUser + '</span></div>' +
                            '<i class="layui-icon layui-icon-ok ok"></i>' +
                            '</li>'
                }
                $('.sourceDbList,.destDbList').html('')
                if (list1) {
                    $('.sourceDbList').append(list1).next('.empty').hide()
                } else {
                    $('.sourceDbList').next('.empty').show()
                }
                if (list2) {
                    $('.destDbList').append(list2).next('.empty').hide()
                } else {
                    $('.destDbList').next('.empty').show()
                }
            });
        });
        // 选择源数据库、目标数据库
        $(document).on('click', '.sourceDbList li,.destDbList li', function () {
            $(this).addClass('active').siblings().removeClass()
        })
        // 点击返回库映射管理页面
        $('.back, .close').click(function () {
            location.href = "/exDbMapping/page"
        })

        form.on('submit(*)', function (data) {
            addDb()
        });

        // 提交表单
        function addDb() {
            var sourceDb = $('.sourceDbList .active'), destDb = $('.destDbList .active');
            if (!sourceDb[0]) {
                layer.alert('请选择源数据库！');
                return
            }
            if (!destDb[0]) {
                layer.alert('请选择目标数据库！');
                return
            }
            $.ajax({
                url: "/exDbMapping/addDbMapping",
                type: "POST",
                data: {
                    "gatherDesc": $("#gatherDesc").val(),
                    "sourceDb": sourceDb.attr('data'),
                    "destDb": destDb.attr('data')
                },
                success: function (data) {
                    layer.open({
                        content: '<div style="padding: 20px 100px;">' + data.data + '</div>'
                        , btn: '关闭全部'
                        , btnAlign: 'c' //按钮居中
                        , shade: 0.5 //不显示遮罩
                        , yes: function () {
                            window.open('/exDbMapping/page', "_self")
                            layer.closeAll();
                        }
                    });
                }
            })
        }
    });

</script>
