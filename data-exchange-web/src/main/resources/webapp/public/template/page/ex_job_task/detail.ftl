<#include "../../layout/mainLayout.ftl">
<link rel="stylesheet" href="../../../static/module/index1.css">
<link rel="stylesheet" href="../../../static/module/add.css">
<@layout>
    <style>
        .job {
            width: 80%;
            margin: 0 auto;
        }

        .job-info {
            background-color: #f6f9fc;
            padding: 10px 20px;
        }

        .job-name {
            font-size: 16px;
            line-height: 20px;
            margin-bottom: 20px;
        }

        .job-info dd {
            display: inline-block;
            vertical-align: top;
            margin-left: 30px;
        }

        .job-info dd:first-child {
            margin-left: 0;
        }

        .job-info dd .title {
            color: #666;
        }

        .headline{
            line-height: 38px;
            font-size: 15px;
            padding: 0 10px;
        }

        .data-info .layout-form {
            width: 100%;
            padding: 10px;
        }

        .data-info .layui-form-item{
            border-bottom: 1px solid #eee;
        }

        .data-info .layui-form-label {
            width: 150px;
            color: #999;
        }

        .data-info .layui-input-block {
            margin-left: 150px;
            line-height: 20px;
            padding: 9px 0;
        }
    </style>
    <div class="content">
        <div class="layui-breadcrumb clearfix" lay-separator=">">
            <a href="/exJobTask/page">交换任务信息</a>
            <a><cite>交换任务详情</cite></a>
            <div class="right">
                <button type="button" class="layui-btn layui-btn-primary layui-btn-sm back">返回</button>
            </div>
        </div>
        <div class="content-table">
            <div class="job">
                <div class="job-info">
                    <div class="job-name">${info.jobName!''}</div>
                    <dl>
                        <dd>
                            <div class="title">状态</div>
                            <div>${info.validFlag!''}</div>
                        </dd>
                        <dd>
                            <div class="title">转换名称</div>
                            <div>${info.jobtaskName!''}</div>
                        </dd>
                        <dd>
                            <div class="title">所属交换节点</div>
                            <div>${info.gatherDesc!''}</div>
                        </dd>
                    </dl>
                </div>
                <div class="data-info mt20">
                    <div class="layui-row layui-col-space20">
                        <div class="layui-col-md6">
                            <div class="headline">
                                源数据信息
                            </div>
                            <div class="layout-form left">
                                <div class="layui-form-item">
                                    <label class="layui-form-label">源数据库</label>
                                    <div class="layui-input-block">
                                        <span class="detail-item">${info.sourceDbName!''}</span>
                                    </div>
                                </div>
                                <div class="layui-form-item">
                                    <label class="layui-form-label">源数据库IP：</label>
                                    <div class="layui-input-block">
                                        <span class="detail-item">${info.sourceDbIp!''}</span>
                                    </div>
                                </div>
                                <div class="layui-form-item">
                                    <label class="layui-form-label">源数据库端口：</label>
                                    <div class="layui-input-block">
                                        <span class="detail-item">${info.sourceDbPort!''}</span>
                                    </div>
                                </div>
                                <div class="layui-form-item">
                                    <label class="layui-form-label">源数据库用户名：</label>
                                    <div class="layui-input-block">
                                        <span class="detail-item">${info.sourceDbUser!''}</span>
                                    </div>
                                </div>
                                <div class="layui-form-item">
                                    <label class="layui-form-label">源数据表：</label>
                                    <div class="layui-input-block">
                                        <span class="detail-item">${info.sourceTb!''}</span>
                                    </div>
                                </div>
                                <div class="layui-form-item">
                                    <label class="layui-form-label">源数据库描述：</label>
                                    <div class="layui-input-block">
                                        <span class="detail-item">${info.sourceDb!''}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="layui-col-md6">
                            <div class="headline">
                                目标数据信息
                            </div>
                            <div class="layout-form left">
                                <div class="layui-form-item">
                                    <label class="layui-form-label">目标数据库：</label>
                                    <div class="layui-input-block">
                                        <span class="detail-item">${info.destDbName!''}</span>
                                    </div>
                                </div>
                                <div class="layui-form-item">
                                    <label class="layui-form-label">目标数据库IP：</label>
                                    <div class="layui-input-block">
                                        <span class="detail-item">${info.destDbIp!''}</span>
                                    </div>
                                </div>
                                <div class="layui-form-item">
                                    <label class="layui-form-label">目标数据库端口：</label>
                                    <div class="layui-input-block">
                                        <span class="detail-item">${info.destDbPort!''}</span>
                                    </div>
                                </div>
                                <div class="layui-form-item">
                                    <label class="layui-form-label">目标数据库用户名：</label>
                                    <div class="layui-input-block">
                                        <span class="detail-item">${info.destDbUser!''}</span>
                                    </div>
                                </div>
                                <div class="layui-form-item">
                                    <label class="layui-form-label">目标数据表：</label>
                                    <div class="layui-input-block">
                                        <span class="detail-item">${info.destTb!''}</span>
                                    </div>
                                </div>
                                <div class="layui-form-item">
                                    <label class="layui-form-label">目标数据库描述：</label>
                                    <div class="layui-input-block">
                                        <span class="detail-item">${info.destDb!''}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="headline">
                        交换的数据字段
                    </div>
                    <table class="layui-table" lay-skin="line">
                        <colgroup>
                            <col width="20%">
                            <col width="30%">
                            <col width="20%">
                            <col width="30%">
                        </colgroup>
                        <thead>
                        <tr>
                            <th>源字段</th>
                            <th>源字段类型</th>
                            <th>目标字段</th>
                            <th>目标字段类型</th>
                        </tr>
                        </thead>
                        <tbody>
                        <#list param as param>
                            <tr>
                                <td>${param.sourceField!''}</td>
                                <td>${param.sourceFieldType!''}</td>
                                <td>${param.destField!''}</td>
                                <td>${param.destFieldType!''}</td>
                            </tr>
                        </#list>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

    </div>


    <script>
        $('.back').click(function () {
            location.href = "/exJobTask/page"
        })
    </script>
</@layout>