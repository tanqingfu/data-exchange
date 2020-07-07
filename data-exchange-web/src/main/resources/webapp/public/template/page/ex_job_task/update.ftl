<#include "../../layout/mainLayout.ftl">
<#--<link rel="stylesheet" href="../../static/module/common.css">-->
<link rel="stylesheet" href="../../static/module/index1.css">
<link rel="stylesheet" href="../../static/module/add.css">
<@layout>
<div class="content">
    <style>
        .layui-table-box {
            overflow: visible;
        }

        .layui-table-body {
            overflow: visible;
        }
    </style>
    <div class="layui-breadcrumb clearfix" lay-separator=">">
        <a href="/exTableMapping/taskPage">数据表配置</a>
        <a><cite>编辑数据表</cite></a>
        <div class="right">
            <button type="button" class="layui-btn layui-btn-primary layui-btn-sm back">返回</button>
        </div>
    </div>
    <div class="add-from addFrom1 layui-form">
        <div class="layout-form">
            <div class="layui-form-item">
                <label class="layui-form-label">所属作业：</label>
                <div class="layui-input-block" id="gatherDesc" name="gatherDesc">${param.jobName!''}</div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="input-required">*</span>转换任务名称：</label>
                <div class="layui-input-block">
                    <input type="text" name="taskName" id="taskName" lay-verify="required" autocomplete="off" value="${param.taskName!''}" class="layui-input">
                </div>
            </div>

            <div class="layui-form-item">
                <label class="layui-form-label">交换节点：</label>
                <div class="layui-input-block" id="gatherDesc" name="gatherDesc">${param.gatherDesc!''}</div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">源数据库：</label>
                <div>${param.sourceDbName!''}</div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">源数据库IP：</label>
                <div>${param.sourceDbIp!''}</div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">源数据库端口：</label>
                <div>${param.sourceDbPort!''}</div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">源数据库用户名：</label>
                <div>${param.sourceUser!''}</div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">源数据表：</label>
                <div>${param.sourceTable!''}</div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">目标数据库：</label>
                <div>${param.destDbName!''}</div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">目标数据库IP：</label>
                <div>${param.destDbIp!''}</div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">目标数据库端口：</label>
                <div>${param.destDbPort!''}</div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">目标数据库用户名：</label>
                <div>${param.destUser!''}</div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">目标数据表：</label>
                <div>${param.destTable!''}</div>
            </div>
            <div class="layui-form-item mt40">
                <div class="layui-input-block">
                    <button type="button" lay-submit="" class="layui-btn next">下一步</button>
                    <button type="button" class="layui-btn layui-btn-primary" onclick="window.location.href='/exJobTask/page'">取消</button>
                </div>
            </div>
        </div>
    </div>

    <div class="add-from addFrom2  layui-form" style="display:none;">
        <div class="show-table clearfix">
            <div class="right">
                <span>目标库表:</span>
                <span id="destDb3" name="destDb3" class="name">${param.destDbName!''}(IP:${param.destDbIp!''};PORT:${param.destDbPort!''};用户名:${param.destUser!''})</span>
                <span id="destTb3" name="destTb3" class="name">${param.destTable!''}</span>
            </div>
            <div>
                <span>源库表:</span>
                <span id="sourceDb2" name="sourceDb2" class="name">${param.sourceDbName!''}(IP:${param.sourceDbIp!''};PORT:${param.sourceDbPort!''};用户名:${param.sourceUser!''})</span>
                <span id="sourceTb2" name="sourceTb2" class="name">${param.sourceTable!''}</span>
            </div>
        </div>
        <div style="width:100%;">
            <table lay-even class="layui-table" lay-data="{url:'', id:'test3'}" lay-filter="fieldTable"
                   id="fieldTable">
            </table>
        </div>
        <div class="mt40">
            <button type="button" class="layui-btn layui-btn-primary prve" style="color: #999;border-color: #999">
                上一步
            </button>
            <button type="button" class="layui-btn saveTask layui-btn-normal"
                    style="width: 80px;border-radius: 5px;background-color: #3986F4">保存
            </button>
        </div>
    </div>
    </form>
</div>
<script type="text/html" id="selectDemo">
    <select name="destFields" lay-filter="destFields" id="destFields">
    </select>
</script>
<script>
    var sourceFields = null, destFieldName = null, form = null, selectList = [], loadShade = null;
    layui.use('form', function () {
        form = layui.form
        var layer = layui.layer, $ = layui.jquery
        // 监听交换节点select
        form.on('select(gatherDesc)', function (data) {
            var gatherDesc = data.value;
            loadShade = layer.load(2, {
                shade: 0.3
            });
            $.ajax({
                url: "/exDbDict/getDbByDesc?gatherDesc=" + gatherDesc,
                type: 'GET'
            })
                    .done(function () {
                        layer.close(loadShade);
                    })
                    .success(function (datas1) {
                        var option2 = "<option value='-1'>请选择源表</option>";//初始化option的选项
                        var sourceDbName1 = datas1[0].sourceDb;
                        $("#sourceDb").html(sourceDbName1);
                        for (var p = 0; p < datas1.data[0]["sourceTables"].length; p++) {
                            option2 += "<option value='" + datas1.data[0]["sourceTables"][p]['text'] + "'>" + datas1.data[0]["sourceTables"][p]['text'] + "</option>";//拼接option中的内容
                            $("#sourceTables").html(option2);//将option的拼接内容加入select中，注意选择器是select的ID
                        }
                        var option3 = "<option value='-1'>请选择目标表</option>";//初始化option的选项
                        var destDbName = datas1.data[1].destDb;
                        $("#destDb").html(destDbName);
                        for (var p = 0; p < datas1.data[1]["destTables"].length; p++) {
                            option3 += "<option value='" + datas1.data[1]["destTables"][p]['text'] + "'>" + datas1.data[1]["destTables"][p]['text'] + "</option>";//拼接option中的内容
                            $("#destTables").html(option3);//将option的拼接内容加入select中，注意选择器是select的ID
                        }
                        form.render('select');//重点：重新渲染select
                    });
        });
        // 监听目标库select
        form.on('select(sourceTables)', function (data) {
            var tb = data.value, sourceName = $("#sourceTb2").html();
            if (data.value != -1 && sourceName) {
                $('.next').removeClass('layui-btn-disabled').attr('disabled', false);
                form.render('select');
            }

            $("#destTb3").html(tb);


        });
        // 监听源库select
        form.on('select(destTables)', function (data) {
            var tb = data.value, destName = $("#destTb3").html();

            if (data.value != -1 && destName) {
                $('.next').removeClass('layui-btn-disabled').attr('disabled', false);
                form.render('select');
            }
            $("#sourceTb2").html(tb);
//            $.ajax({
//                url: "/GetPrimaryKeyController/getDestPrimaryKey",
//                type: "GET",
//                data: {"destTableName": $("#destTables").val(), "gatherDesc": $("#gatherDesc").val()}
//            })
//                    .success(function (datas) {
//                        var option = "<option value='-1'>请选择表主键</option>";//初始化option的选项
//                        for (var i = 0; i < datas.length; i++) {
//                            option += "<option value='" + datas[i]['COLUMN_NAME'] + "'>" + datas[i]['COLUMN_NAME'] + "</option>";//拼接option中的内容
//                            $("#destPrimaryKey").html(option);//将option的拼接内容加入select中，注意选择器是select的ID
//                        }
//                        form.render('select');//重点：重新渲染select
//                    })
        });
        // 监听表格内select
        form.on('select', function (data) {
            if ($(data.elem).hasClass('destFields')) {
                var index = Number($(data.elem).parents('tr').attr('data-index')),
                        type = $(data.elem).find(":selected").attr('data-type') || '',
                        td = $(data.elem).parents('td').next().find('div')
                // 将选择值放入list中，后续方法获取已选择值list进行对比
                // 优化：1.已选择不再插入 2.已有值删除
                var oldTd = selectList.find(function (item) {
                    return item.value == td.attr('data-old')
                })
                if (data.value < 0 || oldTd) {
                    selectList.forEach(function (item, i) {
                        if (item.index == index) {
                            selectList.splice(i, 1)
                        }
                    })
                    sourceFields[index].field1 = ''
                }
                if (!(data.value < 0)) {
                    selectList.push({
                        value: data.value,
                        index: index,
                        type: type
                    });
                    // sourceFields：表格数据
                    sourceFields[index].field1 = data.value
                }
                sourceFields[index].field1Type = type
                td.html(type).attr('data-old', data.value)
                destFieldClick(data.value, index)
            }
        });
        // 下一步点击切换显示并渲染表格
        $('.next').on('click', function () {
            if($('#taskName').val()){
                $('.addFrom1').css('display', 'none')
                $('.addFrom2').css('display', 'block')
                loadShade = layer.load(2, {
                    shade: 0.3
                });
                layui.use('table', function () {
                    var table = layui.table;
                    $.get('/exFieldDict/getAllSyncFields', {
                        "sourceTableName": '${param.sourceTable!''}',
                        "destTableName": '${param.destTable!''}',
                        "sourceDbId": '${param.sourceDbId!''}',
                        "destDbId": '${param.destDbId!''}'
                    }).done(function (result) {
                        sourceFields = [], destFieldName = []
                        for(var i = 0; i<result.data[0].sourceFields.length; i++){
                            if(result.data[0].sourceFields[i].field != 'ROWSEQ'){
                                sourceFields.push(result.data[0].sourceFields[i])
                            }
                        }
                        for(var j = 0; j<result.data[0].destFields.length; j++){
                            if(result.data[0].destFields[j].field != 'ROWSEQ'){
                                destFieldName.push(result.data[0].destFields[j])
                            }
                        }
                        var doneFields = result.data[0].doneFields
                        table.render({
                            elem: '#fieldTable',
                            data: sourceFields,
                            limit: 10000,
                            skin: 'line',
                            done: function (res, curr, count) {
                                var tableElem = this.elem.next('.layui-table-view');
                                count || tableElem.find('.layui-table-header').css('overflow', 'auto');
                                layui.each(tableElem.find('select'), function (index, item) {
                                    var elem = $(item);
                                    elem.val(elem.data('value')).parents('div.layui-table-cell').css({'overflow':'visible','height':'auto'});
                                });
                                form.render();
                                layer.close(loadShade);
                            },
                            cols: [[ //表头
                                {field: 'field', title: '源字段', minWidth: 200},
                                {field: 'fieldType', title: '源字段类型', minWidth: 200},
                                //                                {field: '', title: '目标字段', width: 500, edit: 'select'},
                                {
                                    field: 'field1',
                                    title: '目标字段',
                                    align: 'center',
                                    minWidth: 280,
                                    "templet": function (d) {
                                        // 模板的实现方式也是多种多样，这里简单返回固定的
                                        return '<select class="destFields" name="destFields" lay-filter="destFields" onClick="destFieldClick(this)" lay-verify="required" data-value="' + d.destField + '" >\n' + '</select>';
                                    }
                                },
                                {field: 'field1Type', title: '目标字段类型', minWidth: 200},
                            ]]
                        });
                        // 初始化目标字段select
                        selectList = []
                        if (doneFields.length > 0) {
                            for (var i = 0; i < doneFields.length; i++) {
                                for (var j = 0; j < sourceFields.length; j++) {
                                    if (sourceFields[j].field == doneFields[i].sourceField) {
                                        $('td[data-field="field1Type"]').eq(j).find('div').html(doneFields[i].destType).attr('data-old', doneFields[i].destField)
                                        selectList.push({
                                            value: doneFields[i].destField,
                                            index: j,
                                            type: doneFields[i].destType
                                        });
                                        sourceFields[j].field1 = doneFields[i].destField
                                        sourceFields[j].field1Type = doneFields[i].destType
                                        destFieldClick(doneFields[i].destField, j)
                                    }
                                }
                            }
                        } else {
                            destFieldClick()
                        }
                    })
                });
            }
        });
        // 上一步点击切换显示
        $('.prve').on('click', function () {
            $('.addFrom1').css('display', 'block')
            $('.addFrom2').css('display', 'none')
        });
        // 点击返回任务页面
        $('.back').click(function () {
            location.href = "/exJobTask/page"
        })
        // 点击保存提交任务
        $('.saveTask').click(function () {
            $.ajax({
                url: "/exJobTask/updateTask",
                type: "POST",
                contentType: 'application/json',

                data: JSON.stringify({
                    "taskName": $("#taskName").val(),
                    "sourceDbId": '${param.sourceDbId!''}',
                    "destDbId": '${param.destDbId!''}',
                    "sourceTableName": $("#sourceTb2").html(),
                    "destTableName": $("#destTb3").html(),
//                    "sourcePrimaryKey": $("#sourcePrimaryKey").val(),
//                    "destPrimaryKey": $("#destPrimaryKey").val(),
                    "tableInfos": sourceFields

                }),
                success: function (data) {
                    layer.open({
                        content: '<div style="padding: 20px 100px;">' + data.data + '</div>'
                        , btn: '确定'
                        , btnAlign: 'c' //按钮居中
                        , shade: 0.5 //不显示遮罩
                        , yes: function () {
                            layer.closeAll();
                            window.open('/exJobTask/page', "_self")
                        }
                    });
                }
            })
        })
    });

    /// 表格内每选择一次重新渲染select
    function destFieldClick(value, index) {
        if ($(".destFields").length > 0) {
            for (var i = 0; i < $(".destFields").length; i++) {
                var _this = $($(".destFields")[i]), option2 = null;
                for (var p = 0; p < destFieldName.length; p++) {
                    var li = selectList.find(function (item) {
                        return item.value == destFieldName[p]['field']
                    })
                    if (!option2) {
                        if (li) {
                            option2 = "<option value='-1' data-value=' '>请选择</option>"
                        } else {
                            option2 = "<option selected value='-1' data-value=' '>请选择</option>"
                        }
                    }
                    if (li) {
                        if (li.index == i) {
                            //将option的拼接内容加入select中，注意选择器是select的ID
                            option2 += "<option selected value='" + destFieldName[p]['field'] + "' data-type='" + destFieldName[p]['fieldType'] + "'>" + destFieldName[p]['field'] + "</option>";//拼接option中的内容
                        }
                    } else {
                        //将option的拼接内容加入select中，注意选择器是select的ID
                        option2 += "<option value='" + destFieldName[p]['field'] + "' data-type='" + destFieldName[p]['fieldType'] + "'>" + destFieldName[p]['field'] + "</option>";//拼接option中的内容
                    }
                }
                _this.html(option2);
            }
            form.render('select');
        }
    }

</script>
</@layout>