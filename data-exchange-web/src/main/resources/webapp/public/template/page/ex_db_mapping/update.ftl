<#include "../../layout/dlgLayout.ftl">
<@layout>
    <style>
        .layout-form .layui-form-label{
            width: 130px;
        }
        .layout-form .layui-input-block{
            margin-left: 135px;
        }
    </style>
<form class="layui-form layout-form full" action="/exDbMapping/update" method="post" id="updateItemForm">
    <input type="hidden" name="id" value="${param.id}">
    <div class="layui-form-item">
        <label class="layui-form-label">选择交换节点：</label>
        <div class="layui-input-block">
            <select name="gatherDesc" lay-filter="gatherDesc" id="gatherDesc">
            </select>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">源库：</label>
        <div class="layui-input-block">
            <select name="sourceDb" lay-filter="sourceDb" id="sourceDb">
            </select>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">目标库：</label>
        <div class="layui-input-block">
            <select name="destDb" lay-filter="destDb" id="destDb">
            </select>
        </div>
    </div>
    <div class="submit-btn-area">
        <button type="button" class="layui-btn layui-btn-normal" lay-filter="*" lay-submit="">保存
        </button>
        <button type="button" class="layui-btn layui-btn-primary "
                onclick="window.location.href='/exDbMapping/page'">取消
        </button>
    </div>

</form>
<script>
    //源库用户名
    layui.use(['form', 'formSelects'], function () {
        var form = layui.form, layer = layui.layer, formSelects = layui.formSelects;
        $('.layui-form').ready(function () {
            $.ajax({
                url: "/exGatherDict/getAllGatherDesc",
                type: 'GET'
            }).success(function (datas) {
                var option = "<option value='-1'>请选择交换节点</option>";//初始化option的选项
                var gatherId = '${param.gatherId!''}'
                for (var i = 0; i < datas.length; i++) {
                    option += "<option value='" + datas[i]['gatherId'] + "' " + ((gatherId == datas[i]['gatherId']) ? "selected" : "")
                            + ">" + datas[i]['gatherDesc'] + "</option>";//拼接option中的内容
                }
                $("#gatherDesc").html(option);//将option的拼接内容加入select中，注意选择器是select的ID
                form.render('select');//重点：重新渲染select
                if (gatherId) getAllDatabase()
            });
            form.on('select(gatherDesc)', function (data) {
                getAllDatabase()
            });
            // select下拉框选中触发事件
//            form.on('select(sourceDb)', function (data) {
//                var dbDesc = data.value;
//                $('.layui-form').ready(function () {
//                    $.ajax({
//                        url: "/exDbDict/getUserByDbDesc?dbDesc=" + dbDesc,
//                        type: 'GET'
//                    }).success(function (datas) {
////                                    var option = "<option value='-1'>请选择源库用户</option>";//初始化option的选项
//                        var option = "";//初始化option的选项
//                        for (var i = 0; i < datas.length; i++) {
//                            option += "<option value='" + datas[i]['value'] + "'>" + datas[i]['value'] + "</option>";//拼接option中的内容
//                            $("#sourceUser").html(option);//将option的拼接内容加入select中，注意选择器是select的ID
//                        }
////                                    form.render('select');//重点：重新渲染select
//                        formSelects.render();
//                        //监听select的选中与取消
//                        formSelects.on('sourceDb', function (id, vals, val, isAdd, isDisabled) {
//                            //id:           点击select的id
//                            //vals:         当前select已选中的值
//                            //val:          当前select点击的值
//                            //isAdd:        当前操作选中or取消
//                            //isDisabled:   当前选项是否是disabled
//                        }, true);
//                    })
//                });
//
//            });
        })

        //  点击返回库映射管理页面
        $('.back').click(function () {
            location.href = "/exDbMapping/page"
        })

        function getAllDatabase() {
            $.ajax({
                url: "/exDbDict/getAllDatabase",
                type: 'GET'
            }).success(function (datas) {
                var option = "<option value='-1'>请选择源库</option>";//初始化option的选项
                var sourceDbId = '${param.sourceDbId!''}'
                for (var i = 0; i < datas.length; i++) {
                    if (datas[i]['dbId'] != '-1') {
                        option += "<option value='" + datas[i]['dbId'] + "' " + ((parseInt(sourceDbId) == datas[i]['dbId']) ? "selected" : "")
                                + ">" + datas[i]['dbName'] + "</option>";//拼接option中的内容
                    }
                }
                var option1 = "<option value='-1'>请选择目标库</option>";//初始化option的选项
                var destDbId = '${param.destDbId!''}'
                for (var i = 0; i < datas.length; i++) {
                    if (datas[i]['dbId'] != '-1') {
                        option1 += "<option value='" + datas[i]['dbId'] + "' " + ((parseInt(destDbId) == datas[i]['dbId']) ? "selected" : "")
                                + ">" + datas[i]['dbName'] + "</option>";//拼接option中的内容
                    }
                }
                $("#destDb").html(option1);//将option的拼接内容加入select中，注意选择器是select的ID
                $("#sourceDb").html(option);//将option的拼接内容加入select中，注意选择器是select的ID
                form.render('select');//重点：重新渲染select
            })
        }

        form.on('submit(*)', function (data) {
            updateDb()
        });

        // 提交表单
        function updateDb() {

            $.ajax({
                url: "/exDbMapping/updateDbMapping",
                type: "POST",
                data: {
                    "gatherDesc": $("#gatherDesc").val(),
                    "sourceDb": $("#sourceDb").val(),
                    "destDb": $("#destDb").val(),
                    "id":'${param.id!''}'
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
</@layout>