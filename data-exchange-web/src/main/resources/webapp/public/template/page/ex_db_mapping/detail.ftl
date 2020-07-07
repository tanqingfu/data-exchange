<#include "../../layout/dlgLayout.ftl">
<@layout>
    <style>
        .info-list {
            display: table;
            width: 100%;
        }

        .info {
            display: table-cell;
            border: 1px solid #e6e6e6;
            padding: 10px;
            width: 49%;
        }

        .offset {
            display: table-cell;
            width: 2%;
        }

        .info .layui-form-item {
            margin-top: 10px;
            margin-bottom: 0;
        }

        .info .layui-form-item:first-child {
            margin-top: 0;
        }
    </style>
    <form class="layui-form layui-row layout-form auto left" id="updateItemForm" style="width: 100%">
        <input type="hidden" name="id" value="${param.id}">
        <div class="layui-form-item">
            <label class="layui-form-label">交换节点名称：</label>
            <div class="layui-input-block">
                <span class="detail-item">${param.gatherDesc!''}</span>
            </div>
        </div>
        <div class="layui-col-md6">
            <div class="layui-form-item">
                <label class="layui-form-label">创建时间：</label>
                <div class="layui-input-block">
                    <span class="detail-item"><#if param.createTime??>${param.createTime?datetime}<#else></#if></span>
                </div>
            </div>
        </div>
        <div class="layui-col-md6">
            <div class="layui-form-item">
                <label class="layui-form-label">状态：</label>
                <div class="layui-input-block">
                    <span class="detail-item">${param.validFlag!''}</span>
                </div>
            </div>
        </div>
        <div class="info-list">
            <div class="info">
                <div class="layui-form-item">
                    <label class="layui-form-label">源数据库：</label>
                    <div class="layui-input-block">
                        <span class="detail-item">${param.sourceDbName!''}</span>
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">IP地址：</label>
                    <div class="layui-input-block">
                        <span class="detail-item">${param.sourceDbIp!''}</span>
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">端口：</label>
                    <div class="layui-input-block">
                        <span class="detail-item">${param.sourceDbPort!''}</span>
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">用户名：</label>
                    <div class="layui-input-block">
                        <span class="detail-item">${param.sourceDbUser!''}</span>
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">数据库描述：</label>
                    <div class="layui-input-block">
                        <span class="detail-item">${param.sourceDb!''}</span>
                    </div>
                </div>
            </div>
            <div class="offset"></div>
            <div class="info">
                <div class="layui-form-item">
                    <label class="layui-form-label">目标数据库：</label>
                    <div class="layui-input-block">
                        <span class="detail-item">${param.destDbName!''}</span>
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">IP地址：</label>
                    <div class="layui-input-block">
                        <span class="detail-item">${param.destDbIp!''}</span>
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">端口：</label>
                    <div class="layui-input-block">
                        <span class="detail-item">${param.destDbPort!''}</span>
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">用户名：</label>
                    <div class="layui-input-block">
                        <span class="detail-item">${param.destDbUser!''}</span>
                    </div>
                </div>
                <div class="layui-form-item">
                    <label class="layui-form-label">数据库描述：</label>
                    <div class="layui-input-block">
                        <span class="detail-item">${param.destDb!''}</span>
                    </div>
                </div>
            </div>
        </div>

    </form>
    <script>
        layui.use(['form', 'global'], function () {
            var form = layui.form, $ = layui.$;
            window.onClose = function () {
                layer.closeAll();
            };
            form.render();
        });
    </script>
</@layout>