<#include "../../layout/mainLayout.ftl">
<link rel="stylesheet" href="../../../static/module/index1.css">
<link rel="stylesheet" href="../../../static/module/add.css">
<@layout>
<div style="padding: 20px;" class="content">
    <div class="layui-breadcrumb clearfix" lay-separator=">">
        <a href="/">交换节点管理</a>
        <a><cite>新增交换节点</cite></a>
        <div class="right">
            <button type="button" class="layui-btn layui-btn-primary layui-btn-sm back">返回</button>
        </div>
    </div>
    <div class="add-from">
        <form class="layui-form layout-form" id="newItemForm">
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="input-required">*</span>交换节点名称：</label>
                <div class="layui-input-block">
                    <input type="text" name="gatherDesc1" id="gatherDesc1" lay-verify="gatherDesc1 required" autocomplete="off"
                           placeholder="" class="layui-input">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">服务器IP：</label>
                <div class="layui-input-block">
                    <input type="text" name="serviceIp" id="serviceIp" lay-verify="serviceIp" autocomplete="off"
                           placeholder="MYSQL节点必填" class="layui-input">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">ssh端口：</label>
                <div class="layui-input-block">
                    <input type="text" name="sshPort" id="sshPort" lay-verify="sshPort" autocomplete="off"
                           placeholder="MYSQL节点必填" class="layui-input">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">ssh用户：</label>
                <div class="layui-input-block">
                    <input type="text" name="sshUser" id="sshUser" lay-verify="sshUser" autocomplete="off"
                           placeholder="MYSQL节点必填" class="layui-input">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">服务器密码：</label>
                <div class="layui-input-block">
                    <input type="password" name="sshPassword" id="sshPassword" lay-verify="sshPassword" autocomplete="off"
                           placeholder="MYSQL节点必填" class="layui-input">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label"> <span class="input-required">*</span>服务器描述：</label>
                <div class="layui-input-block">
                    <input type="text" name="serviceDesc" id="serviceDesc" lay-verify="serviceDesc required" autocomplete="off"
                           placeholder="" class="layui-input">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">交换节点路径：</label>
                <div class="layui-input-block">
                    <input type="text" name="gatherPath" id="gatherPath"  lay-verify="gatherPath" autocomplete="off"
                           placeholder="MYSQL节点必填" class="layui-input">
                </div>
            </div>


            <div class="layui-form-item">
                <div class="layui-input-block submit-btn-area">
                    <button type="button" class="layui-btn" lay-submit="" lay-filter="*">保存</button>
                    <button type="button" class="layui-btn layui-btn-primary"
                            onclick="window.location.href='/'">取消
                    </button>
                </div>
            </div>

        </form>
    </div>


</div>

</@layout>
<script>
    layui.use(['layer','form'], function () {
        var $ = layui.jquery, layer = layui.layer, form = layui.form;
        form.on('submit(*)', function(data){
            addGatherDict()
        });
        $('.back').click(function () {
            location.href = "/"
        })
        function addGatherDict(callback) {

                $.ajax({
                    url: "/exGatherDict/addGather",
                    type: "POST",
                    data: {
                        "gatherDesc": $("#gatherDesc1").val(),
                        "serviceIp": $("#serviceIp").val(),
                        "serviceDesc": $("#serviceDesc").val(),
                        "gatherPath":$("#gatherPath").val(),
                        "sshPassword": $("#sshPassword").val(),
                        "sshPort": $("#sshPort").val(),
                        "sshUser":$("#sshUser").val()
                    },
                    success: function (data) {
                        layer.open({
                            content: '<div style="padding: 20px 100px;">' + data.data + '</div>'
                            , btn: '关闭全部'
                            , btnAlign: 'c' //按钮居中
                            , shade: 0.5 //不显示遮罩
                            , yes: function () {
                                layer.closeAll();
                                window.open('/exGatherDict/page',"_self")
                            }
                        });
//                        layer.open({
//                            content: '<div style="padding: 20px 100px;">' + data.data + '</div>'
//                            , btn: '关闭全部'
//                            , btnAlign: 'c' //按钮居中
//                            , shade: 0.3 //不显示遮罩
//                            , yes: function () {
//                                layer.closeAll();
//                                window.open('/', "_self")
//                            }
//                        });
                    }
                })

        }
    })
</script>

