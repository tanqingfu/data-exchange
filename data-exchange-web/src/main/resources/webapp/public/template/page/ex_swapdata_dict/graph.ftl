<#include "../../layout/mainLayout.ftl">
<@layout cssPath=['/static/module/index1.css'] jsPath=["/static/echarts-4.2.1/echarts.min.js"]>
    <style>
        .layui-form-select{
            width: 260px;
        }
    </style>
    <div style="padding: 20px;" class="content">
<#--        <h3>交换量统计</h3>-->
        <div class="content-table">
                <div class="content-input layui-fluid layui-col-space10 layui-form">
                    <div class="line">
                        <span class="label">选择作业</span>
                        <select name="jobInfoId" lay-verify="required" lay-filter="jobinfofilter">
                            <#list jobInfoList as jobInfo>
                                <option value="${jobInfo.jobId}">${jobInfo.jobName}</option>
                            </#list>
                        </select>
                    </div>
                </div>
            <div style="height: 600px;overflow:auto;clear: both;">
                <div id="chartMain" style="width: 100%;height:600px;float:left;padding:10px;"></div>
            </div>
        </div>
    </div>
    <style>
        .layui-form-select .layui-edge {
            right: 15px;
        }
        .layui-form-select dl {
            margin-left: 0;
        }
    </style>
</@layout>
<@js>
    <script type="text/javascript">
        layui.use(['layer', 'global','form'], function () {
            var layer = layui.layer, $ = layui.$, form = layui.form;
            form.on('select(jobinfofilter)', function(data){
                onselect(data.value)
            });
            form.render('select');
            <#if jobInfoList?size gt 0>
                onselect(${jobInfoList[0].jobId});
            </#if>
            function onselect(jobId) {
                $.ajax({
                    type: 'get',
                    url: '/exSwapdataDict/graph/jobInfo',
                    data: {jobInfoId: jobId},
                    async: true,
                    success: function (data, status, xhr) {
                        if (data.success) {
                            // 基于准备好的dom，初始化echarts实例
                            var myChart = echarts.init(document.getElementById('chartMain'), 'light');
                            // 指定图表的配置项和数据
                            var option = {
                                title: {
                                    text: data.data.jobName
                                },
                                tooltip: {
                                    trigger: 'axis'
                                },
                                legend: {
                                    data: data.data.taskList.map(item => item.jobtaskName)
                                },
                                grid: {
                                    left: '5%',
                                    right: '7%',
                                    bottom: '7%',
                                    containLabel: true
                                },
                                toolbox: {
                                    feature: {
                                        dataZoom: {
                                            yAxisIndex: 'none'
                                        },
                                        restore: {},
                                        saveAsImage: {}
                                    }
                                },
                                xAxis: {
                                    type: 'category',
                                    boundaryGap: false,
                                    data: data.data.taskHourList
                                },
                                yAxis: {
                                    type: 'value'
                                },
                                series: data.data.taskList.map(item => {
                                    return {
                                        name: item.jobtaskName,
                                        type: 'line',
                                        data: item.taskStats.map(item2 => item2.swaGross)
                                    }
                                }),
                                dataZoom: [
                                    {
                                        show: true,
                                        realtime: true,
                                        start: 0,
                                        end: 100
                                    },
                                    {
                                        type: 'inside',
                                        realtime: true,
                                        start: 0,
                                        end: 100
                                    }
                                ]
                            };

                            // 使用刚指定的配置项和数据显示图表。
                            myChart.setOption(option);
                        }
                    }
                });
            }
        });
    </script>
</@js>