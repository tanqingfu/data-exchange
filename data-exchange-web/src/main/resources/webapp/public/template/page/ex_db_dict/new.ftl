<#include "../../layout/mainLayout.ftl">
<link rel="stylesheet" href="../../static/module/index1.css">
<link rel="stylesheet" href="../../static/module/add.css">
<@layout>
<div style="padding: 20px;" class="content">
    <div class="layui-breadcrumb clearfix" lay-separator=">">
        <a href="">数据源管理</a>
        <a><cite>新增数据源</cite></a>
        <div class="right">
            <button type="button" class="layui-btn layui-btn-primary layui-btn-sm back">返回</button>
        </div>
    </div>

    <div class="add-from">
        <form class="layui-form layout-form" id="newItemForm">
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="input-required">*</span>数据库名称：</label>
                <div class="layui-input-block">
                    <input type="text" name="dbName" id="dbName" class="layui-input" lay-verify="required">
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
                <label class="layui-form-label"> <span class="input-required">*</span>IP地址：</label>
                <div class="layui-input-block">
                    <input type="text" name="dbIp" id="dbIp" class="layui-input" lay-verify="required">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="input-required">*</span>端口：</label>
                <div class="layui-input-block">
                    <input type="text" name="dbPort" id="dbPort" class="layui-input" lay-verify="required">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="input-required">*</span>用户名：</label>
                <div class="layui-input-block">
                    <input type="text" name="dbUser" id="dbUser" class="layui-input" lay-verify="required">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="input-required">*</span>密码：</label>
                <div class="layui-input-block">
                    <input type="password" name="dbPasswd" id="dbPasswd" class="layui-input" lay-verify="required">
                </div>
            </div>
            <div class="layui-form-item" id="userInfoDiv">
                <label class="layui-form-label"><span> </span>同步账号：</label>
                <div class="layui-input-block">
                    <select name="userInfo" lay-filter="userInfo" id="userInfo">
                    </select>
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="input-required">*</span>数据库所属地：</label>
                <div class="layui-input-block">
                    <input type="hidden" id="deptIdHid" class="layui-input" name="organizationId" readonly/>
                    <input type="text" id="deptId" class="layui-input" readonly/>
                    <div id="menuContent" class="menuContent">
                        <ul id="tree" class="ztree selectztree"></ul>
                    </div>
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="input-required">*</span>数据库所属科室：</label>
                <div class="layui-input-block">
                    <select name="office" lay-filter="office" id="office">
                    </select>
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="input-required">*</span>数据库描述：</label>
                <div class="layui-input-block">
                <#--                    <input type="text" name="dbDesc" id="dbDesc" class="layui-input" lay-verify="required">-->
                    <textarea name="dbDesc" id="dbDesc" placeholder="请输入描述" class="layui-textarea" lay-verify="required"></textarea>
                </div>
            </div>
            <div class="layui-form-item">
                <div class="layui-input-block submit-btn-area">
                    <button type="button" class="layui-btn" lay-submit="" lay-filter="*">保存</button>
                    <button type="button" class="layui-btn layui-btn-primary"
                            onclick="window.location.href='/exDbDict/page'">取消
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
//                $('#dbPort').blur(function () {
            $.ajax({
                url: "/exSyncmonUserinfo/getAllUserInfo",
                type: 'GET',
//                        data: {
//                            "dbIp": $("#dbIp").val(),
//                            "dbPort": $("#dbPort").val()
//                        }
            })
                    .done(function (datas) {
                        var option = "<option value='-1'>请选择同步账号</option>";//初始化option的选项
                        for (var i = 0; i < datas.data.length; i++) {
                            option += "<option value='" + datas.data[i]['syncId'] + "'>" + datas.data[i]['syncdbUser'] + "</option>";//拼接option中的内容
                            $("#userInfo").html(option);//将option的拼接内容加入select中，注意选择器是select的ID
                        }
                        form.render('select');//重点：重新渲染select
                    })
                    .success(function () {
                    });
//                })
        });
    });

    //省市区
    layui.use(['form', 'jquery.ztree.core'], function () {
        var form = layui.form, layer = layui.layer;
        // 监听
        $(document).ready(function () {
            // select下拉框选中触发事件
            form.on('select(userInfo)', function (data) {
                $('.layui-form').ready(function () {
                    layui.use('form', function () {
                        var form = layui.form;
                    });
                });
            });

        });
        form.on('submit(*)', function(data){
            addDb()
        });

        var setting = {
            async: {
                enable: true,
                type: 'get',
                url: "/exOrganizationDict/getAllOrganzitions",
                autoParam: ["id=parentId"],
                dataFilter: ajaxDataFilter
            },
            data: {
                key: {
                    isParent: "expand",
                    name: "text"
                },
                simpleData: {
                    enable: true
                }
            },
            callback: {
                onClick: zTreeOnClick
            }
        };

        function ajaxDataFilter(treeId, parentNode, responseData) {
            if (responseData.success) {
                return responseData.data;
            }
            return [];
        }

        function zTreeOnClick(e, treeId, treeNode) {
            var zTree = $.fn.zTree.getZTreeObj("tree"),
                    nodes = zTree.getSelectedNodes(),
                    v = "";
            nodes.sort(function compare(a, b) {
                return a.id - b.id;
            });
            for (var i = 0, l = nodes.length; i < l; i++) {
                v += nodes[i].text + ",";
            }
            if (v.length > 0) v = v.substring(0, v.length - 1);
            $("#deptId").attr("value", v);
            $("#deptIdHid").attr("value", treeNode.id);
            hideMenu();


            var id = treeNode.id;
            $('.layui-form').ready(function () {
                layui.use('form', function () {
                    var form = layui.form;
                    $.ajax({
                        url: "/exOrganizationDict/getAllOffice?id=" + id,
                        type: 'GET'
                    })
                            .done(function (datas) {
                                var option = "<option value='-1'>请选择源库所属科室</option>";//初始化option的选项
                                for (var i = 0; i < datas.length; i++) {
                                    option += "<option value='" + datas[i]['value'] + "'>" + datas[i]['text'] + "</option>";//拼接option中的内容
                                    $("#office").html(option);//将option的拼接内容加入select中，注意选择器是select的ID
                                }
                                form.render('select');//重点：重新渲染select
                            })
                            .success(function () {
                            });
                });
            });
        }

        $('#deptId').click(function () {
            showMenu();
        });

        function showMenu() {
            $("#menuContent").slideDown("fast");
            $("body").bind("mousedown", onBodyDown);
        }

        function hideMenu() {
            $("#menuContent").fadeOut("fast");
            $("body").unbind("mousedown", onBodyDown);
        }

        function onBodyDown(event) {
            if (!(event.target.id == "menuBtn" || event.target.id == "menuContent" || $(event.target).parents("#menuContent").length > 0)) {
                hideMenu();
            }
        }

        $.fn.zTree.init($("#tree"), setting);
    });
    //源科室
    layui.use('form', function () {
        var form = layui.form, layer = layui.layer;
        // 监听
        $(document).ready(function () {
            // select下拉框选中触发事件
            form.on('select(deptIdHid)', function (data) {

            });

        });

        $('.back').click(function () {
            location.href = "/exDbDict/page"
        })
    });

    function addDb() {
        $.ajax({
            url: "/exDbDict/addDb",
            type: "POST",
            data: {
                "dbDesc": $("#dbDesc").val(),
                "dbIp": $("#dbIp").val(),
                "dbPort": $("#dbPort").val(),
                "dbUser": $("#dbUser").val(),
                "dbPasswd": $("#dbPasswd").val(),
                "dbName": $("#dbName").val()
                , "dbType": $("#dbType").val()
                //, "sourceOrDest": $("#sourceOrDest").val()
                , "userInfo": $("#userInfo").val()
                , "deptIdHid": $("#deptIdHid").val()
                , "office": $("#office").val()
            },
            success: function (data) {
                _layer.open({
                    content: '<div style="padding: 20px 100px;">' + data.data + '</div>'
                    , btn: '确定'
                    , btnAlign: 'c' //按钮居中
                    , shade: 0.5//不显示遮罩
                    , yes: function () {
                        _layer.closeAll();
                        window.open('/exDbDict/page', "_self")
                    }
                });
            }
        })
    }


    var _$ = null, _layer = null
    layui.use('layer', function () {
        _$ = layui.jquery, _layer = layui.layer;
    })
</script>
</@layout>