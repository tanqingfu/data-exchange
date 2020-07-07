<#include "../../layout/mainLayout.ftl">
<link rel="stylesheet" href="../../../static/module/index1.css">
<link rel="stylesheet" href="../../../static/module/add.css">
<@layout>
<style>
    .layout-form {
        width: 80%;
    }
    .tag-span {
        width: 55px;
    }
</style>
<div class="content">
    <div class="layui-breadcrumb clearfix" lay-separator=">">
        <a href="/exJobTask/page">作业信息</a>
        <a><cite>作业详情</cite></a>
        <div class="right">
            <button type="button" class="layui-btn layui-btn-primary layui-btn-sm back">返回</button>
        </div>
    </div>
    <div class="content-table">
        <form class="layui-form layout-form" id="updateItemForm">

            <div class="layui-col-md6">
                <div class="layui-form-item">
                    <label class="layui-form-label">作业名：</label>
                    <div class="layui-input-block">
                        <span class="detail-item">${info.jobName!''}</span>
                    </div>
                </div>
            </div>
            <div class="layui-col-md6">
                <div class="layui-form-item">
                    <label class="layui-form-label">作业状态：</label>
                    <div class="layui-input-block">
                        <span class="detail-item">${info.jobState!''}</span>
                    </div>
                </div>
            </div>
            <div class="layui-col-md6">
                <div class="layui-form-item">
                    <label class="layui-form-label">作业服务器：</label>
                    <div class="layui-input-block">
                        <span class="detail-item">${info.ip!''}</span>
                    </div>
                </div>
            </div>
            <#if info.validFlag == "1">
                <div class="layui-col-md6" id="dealTime">
                    <div class="layui-form-item">
                        <label class="layui-form-label">增量作业开始时间：</label>
                        <div class="layui-input-block">
                            <span class="detail-item">${info.dealTime!''}</span>
                        </div>
                    </div>
                </div>
            </#if>
            <div class="layui-form-item">
                <label class="layui-form-label">作业描述：</label>
                <div class="layui-input-block">
                    <span class="detail-item">${info.jobDesc!''}</span>
                </div>
            </div>
            <table class="layui-table" style="width: 100%;margin-top: 30px;text-align: center; border-color:#ccc">
                <colgroup>

                    <col width="23%">
                    <col width="23%">
                    <col width="23%">
                    <col width="21%">
                    <col width="10%">
                    <col>
                </colgroup>
                <thead>
                    <tr>
                        <th style="text-align: center;">交换任务名称</th>
                        <th style="text-align: center;">源库表</th>
                        <th style="text-align: center;">目标库表</th>
                        <th style="text-align: center;">全量进度</th>
                        <th style="text-align: center;">操作</th>
                    </tr>
                </thead>
                <tbody style="text-align: center;">
                    <#list param as param>
                    <tr>
                        <td>${param.taskName!''}</td>
                        <td><div>${param.sourceDbDesc!''}</div><div><span class="layui-badge layui-bg-gray">${param.sourceDb!''} @ ${param.sourceTb!''}</span></div></td>
                        <td><div>${param.destDbDesc!''}</div><div><span class="layui-badge layui-bg-gray">${param.destDb!''} @ ${param.destTb!''}</span></div></td>
                        <td>
                            <div>
                                读: ${param.syncReadCurrent!''}/${param.syncReadTotal!''}
                            </div>
                            <div>
                                写: ${param.syncWriteCurrent!''}/${param.syncWriteTotal!''}
                            </div>
                        </td>
                        <td><a class="layui-btn layui-btn-xs layui-btn-primary" href="/exJobTask/page/detailPage?id=${param.taskId}">详情</a> </td>
                    </tr>
                    </#list>
                </tbody>
            </table>
            <div class="layui-collapse" lay-accordion>
                <div class="layui-colla-item">
                    <h2 class="layui-colla-title" style="background-color: rgb(241, 245, 248);">作业日志</h2>
                    <div class="layui-colla-content layui-show">
                        <div class="layui-card-body">
                            <#list jobLogs as jobLog>
                                <div style="margin-bottom: 10px;">
                                    <#if jobLog.type == 1>
                                        <span class="layui-badge layui-bg-yellow tag-span">TRACE</span>
                                    <#elseif jobLog.type == 2>
                                        <span class="layui-badge layui-bg-blue tag-span">DEBUG</span>
                                    <#elseif jobLog.type == 3>
                                        <span class="layui-badge layui-bg-green tag-span">INFO</span>
                                    <#elseif jobLog.type == 4>
                                        <span class="layui-badge layui-bg-orange tag-span">WARN</span>
                                    <#elseif jobLog.type == 5>
                                        <span class="layui-badge layui-bg-red tag-span">ERROR</span>
                                    <#else>
                                        <span class="layui-badge layui-bg-red tag-span">UNKNOWN</span>
                                    </#if>
                                    <span class="layui-badge-rim" style="height: 20px;">${jobLog.createTime?string('yyyy-MM-dd HH:mm:ss')}</span>
                                    <span style="<#if jobLog.type == 5>color:red;</#if>">${jobLog.msg!''}</span>
                                </div>
                            </#list>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>

</div>
<script>
    $('.back').click(function () {
        location.href = "/exJobInfo/jobPage"
    })
</script>
</@layout>