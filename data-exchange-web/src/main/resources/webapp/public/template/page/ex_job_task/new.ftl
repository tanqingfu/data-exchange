<#include "../../layout/mainLayout.ftl">
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

        .choice-list{
            width: 700px;
        }
        .choice-list .list{
            margin-left: 0;
        }
        .choice-list .list:first-child{
            margin-right: 3%;
        }
        .choice-list .layui-form-label{
            width: 52px;
        }
        .choice-list .layui-input-block{
            margin-left: 52px;
        }
        .choice-list .empty{
            display: block;
        }
    </style>
    <div class="layui-breadcrumb clearfix" lay-separator=">">
        <a href="/exTableMapping/taskPage">转换任务配置</a>
        <a><cite>新增转换任务</cite></a>
        <div class="right">
            <button type="button" class="layui-btn layui-btn-primary layui-btn-sm back">返回</button>
        </div>
    </div>
<#--    <form class="layui-form">-->
    <div class="add-from layui-form addFrom1">
        <div class="layout-form">
            <div class="layui-form-item">
                <label class="layui-form-label"><span class="input-required">*</span>转换任务名称：</label>
                <div class="layui-input-block">
                    <input type="text" id="taskName" name="taskName" lay-verify="required" autocomplete="off"
                           placeholder="请输入交换任务名称" class="layui-input">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">选择交换节点：</label>
                <div class="layui-input-block">
                    <select name="gatherDesc" lay-filter="gatherDesc" id="gatherDesc">
                    </select>
                </div>
            </div>
        </div>
        <div class="choice-list mt20">
            <div class="list">
                <div class="line">
                    <div class="title">源数据库</div>
                    <ul class="sourceDbList">

                    </ul>
                    <div class="empty mt40">请先选择交换节点</div>
                </div>
                <div class="layout-form left full">
                    <div class="layui-form-item mt20">
                        <label class="layui-form-label">源表</label>
                        <div class="layui-input-block">
                            <select name="sourceTables" lay-filter="sourceTables" id="sourceTables">
                            </select>
                        </div>
                    </div>
                </div>
            </div>
            <div class="list">
                <div class="line">
                    <div class="title">目标数据库</div>
                    <ul class="destDbList">

                    </ul>
                    <div class="empty mt40">请先选择源数据库</div>
                </div>
                <div class="layout-form left full">
                    <div class="layui-form-item mt20">
                        <label class="layui-form-label">目标表</label>
                        <div class="layui-input-block">
                            <select name="destTables" lay-filter="destTables" id="destTables">
                            </select>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="layui-form-item next2">
            <div class="layui-input-block submit-btn-area">
                <button type="button" lay-submit="" class="layui-btn next layui-btn-disabled" disabled> 下一步</button>
                <button type="button" class="layui-btn layui-btn-primary"
                        onclick="window.location.href='/exJobTask/page'">取消
                </button>
            </div>
        </div>

    </div>
<#--    </form>-->
    <div class="add-from layui-form addFrom2" style="display:none;">
        <div class="show-table clearfix">
            <div class="right">
                <span>目标库表:</span>
                <span id="destDb3" class="name"></span>
                <span id="destTb3" class="name"></span>
            </div>
            <div style="float: left">
                <span>源库表:</span>
                <span id="sourceDb2" class="name"></span>
                <span id="sourceTb2" class="name"></span>
            </div>
        </div>
        <div style="width:100%;">
            <table lay-even class="layui-table" lay-data="{url:'', id:'test3'}" lay-filter="fieldTable"
                   id="fieldTable">
            </table>
        </div>
        <div class="mt40">
            <button type="button" class="layui-btn layui-btn-primary prve"
                    style="color: #999;border-color: #999">
                上一步
            </button>
            <button type="button" class="layui-btn saveTask layui-btn-normal"
                    style="width: 80px;border-radius: 5px;background-color: #3986F4">保存
            </button>
        </div>
    </div>


</div>
<script>

    var sourceFields = null, destFieldName = null, form = null, selectList = [], loadShade = null;
    layui.use('form', function () {
        form = layui.form
        var layer = layui.layer, $ = layui.jquery

        form.render();
        // 监听交换节点select
        form.on('select(gatherDesc)', function (data) {
            var gatherDesc = data.value;
            $.ajax({
                url: "/exDbDict/getSourceDbsAndDestDbs?gatherId=" + gatherDesc,
                type: 'GET'
            }).done(function () {
                layer.close(loadShade);
            }).success(function (datas) {
                var list1 = '', tip = data.value!=-1?'暂无数据': '请先选择交换节点';
                // 处理渲染源数据库、目标数据库
                for (var i = 0; i < datas["sourceDb"].length; i++) {
                    list1 += '<li data="' + datas["sourceDb"][i].sourceDbId + '">' +
                            '<div class="name">' + datas["sourceDb"][i].sourceDbName + '</div>' +
                            '<div class="info">IP：<span class="ip">' + datas["sourceDb"][i].sourceIp + '</span> port：<span class="port">' + datas["sourceDb"][i].sourcePort+'</span> user：<span class="user">'+ datas["sourceDb"][i].sourceUser + '</span></div>' +
                            '</li>'
                }
                $('.sourceDbList').html('')
                if (list1) {
                    $('.sourceDbList').append(list1).next('.empty').hide()
                } else {
                    $('.sourceDbList').next('.empty').html(tip).show()
                    $('.destDbList').html('').next('.empty').html('请先选择源数据库').show()
                    $("#sourceTables").html("<option value='-1'>请选择源表</option>");
                    $("#destTables").html("<option value='-1'>请选择目标表</option>");
                    $('.next').addClass('layui-btn-disabled').attr('disabled', true);
                    form.render('select');//重点：重新渲染select
                }
            });
        });
        // 监听源库select
        form.on('select(sourceDb)', function (data) {
            $.ajax({
                url: "/exTableDict/getSourceTablesAndFields",
                data: {"sourceDbId": $("#sourceDb").val()},
                type: "GET"
            })
                    .success(function (datas){
                        $("#sourceDb2").html($("#sourceDb option:selected").html());
                        var option2 = "<option value='-1'>请选择源表</option>";//初始化option的选项
                        for (var p = 0; p < datas.data[0]["sourceTables"].length; p++) {
                            option2 += "<option value='" + datas.data[0]["sourceTables"][p]['text'] + "'>" + datas.data[0]["sourceTables"][p]['text'] + "</option>";//拼接option中的内容
                            $("#sourceTables").html(option2);//将option的拼接内容加入select中，注意选择器是select的ID
                        }
                        var option4 = "<option value='-1'>请选择目标库</option>";//初始化option的选项
                        for (var p = 0; p < datas.data[0]["destDbInfo"].length; p++) {
                            option4 += "<option value='" + datas.data[0]["destDbInfo"][p]['destDbId'] + "'>" + datas.data[0]["destDbInfo"][p]['destDb'] + "</option>";//拼接option中的内容
                            $("#destDb").html(option4);//将option的拼接内容加入select中，注意选择器是select的ID
                        }
                        form.render('select');//重点：重新渲染select
                    })
        })
        // 监听源表select
        form.on('select(sourceTables)', function (data) {
            var tb = data.value, destName = $("#destTables").val();
            if (tb != -1 && destName != -1) {
                $('.next').removeClass('layui-btn-disabled').attr('disabled', false);
                form.render('select');
            }
            $("#sourceTb2").html(tb);
        });
        // 监听目标库select
        form.on('select(destDb)', function (data) {
            $.ajax({
                url: "/exTableDict/getDestTablesAndFields",
                data: {"destDbId": $("#destDb").val()},
                type: "GET"
            })
                    .success(function (datas){
                        $("#destDb3").html($("#destDb option:selected").html());
                        var option2 = "<option value='-1'>请选择目标表</option>";//初始化option的选项
                        for (var p = 0; p < datas.data[0]["destTables"].length; p++) {
                            option2 += "<option value='" + datas.data[0]["destTables"][p]['text'] + "'>" + datas.data[0]["destTables"][p]['text'] + "</option>";//拼接option中的内容
                            $("#destTables").html(option2);//将option的拼接内容加入select中，注意选择器是select的ID
                        }
                        form.render('select');//重点：重新渲染select
                    })
        });
        // 监听目标表select
        form.on('select(destTables)', function (data) {
            $.ajax({
                url: "/exJobTask/getRowseq",
                data: {"destDbId": $(".destDbList li.active").attr('data'),"destTableName":$("#destTables").val()},
                type: "GET"
            }).success(function(result){
                if(result.success){
                    var tb = data.value, sourceName = $("#sourceTables").val();
                    if (tb != -1 && sourceName != -1) {
                        $('.next').removeClass('layui-btn-disabled').attr('disabled', false);
                        form.render('select');
                    }
                    $("#destTb3").html(tb);
                }
            }).error(function (error) {
                $('.next').addClass('layui-btn-disabled').attr('disabled', true);
                form.render('select');
            })

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

        // 初始化交换节点
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
                    var sourceTableName = $("#sourceTables").val();
                    var destTableName = $("#destTables").val();
                    var sourceDbId = $(".sourceDbList li.active").attr('data');
                    var destDbId= $(".destDbList li.active").attr('data');
                    $.get('/exFieldDict/getAllSyncFields', {
                        "sourceTableName": sourceTableName,
                        "destTableName": destTableName,
                        "sourceDbId": sourceDbId,
                        "destDbId": destDbId
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
//                        sourceFields = result.data[0].sourceFields;
//                        destFieldName = result.data[0].destFields;
                        var mateFields = result.data[0].mateFields
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
                                {field: 'field1Type', title: '目标字段类型', minWidth: 200}
                            ]]
                        });
                        selectList = []
                        if (mateFields.length > 0) {
                            for (var i = 0; i < mateFields.length; i++) {
                                for (var j = 0; j < sourceFields.length; j++) {
                                    if (sourceFields[j].field == mateFields[i].sourceField) {
                                        $('td[data-field="field1Type"]').eq(j).find('div').html(mateFields[i].destFieldType).attr('data-old', mateFields[i].destField)
                                        selectList.push({
                                            value: mateFields[i].destField,
                                            index: j,
                                            type: mateFields[i].destFieldType
                                        });
                                        sourceFields[j].field1 = mateFields[i].destField
                                        sourceFields[j].field1Type = mateFields[i].destFieldType
                                        destFieldClick(mateFields[i].destField, j)
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
            selectList = []
        });
        // 点击返回任务页面
        $('.back').click(function () {
            location.href = "/exJobTask/page"
        })
        // 点击保存提交任务
        $('.saveTask').click(function () {
            saveTask()
        })
        // 选择源数据库
        $(document).on('click', '.sourceDbList li', function () {
            if($(this).attr('data') == $('.sourceDbList .active').attr('data')) return
            $(this).addClass('active').siblings().removeClass()
            var currentDb = $(this)
            $.ajax({
                url: "/exTableDict/getSourceTablesAndFields",
                data: {"sourceDbId": currentDb.attr('data'),"gatherId":$("#gatherDesc").val()},
                type: "GET"
            }).success(function (datas){
                $("#sourceDb2").html(currentDb.find('.name').html());
                var list2 = '', tip = '暂无数据';
                // 初始化源表option的选项
                var option2 = "<option value='-1'>请选择源表</option>";
                for (var p = 0; p < datas.data[0]["sourceTables"].length; p++) {
                    option2 += "<option value='" + datas.data[0]["sourceTables"][p]['text'] + "'>" + datas.data[0]["sourceTables"][p]['text'] + "</option>";//拼接option中的内容
                    $("#sourceTables").html(option2);//将option的拼接内容加入select中，注意选择器是select的ID
                }
                $("#destTables").html("<option value='-1'>请选择目标表</option>");
                $('.next').addClass('layui-btn-disabled').attr('disabled', true);
                // 处理目标数据库
                for (var i = 0; i < datas.data[0]["destDbInfo"].length; i++) {
                    list2 += '<li data="' + datas.data[0]["destDbInfo"][i].destDbId + '">' +
                            '<div class="name">' + datas.data[0]["destDbInfo"][i].destDbName + '</div>' +
                            '<div class="info">IP：<span class="ip">' + datas.data[0]["destDbInfo"][i].destDbIp + '</span> port：<span class="port">' + datas.data[0]["destDbInfo"][i].destDbPort+ '</span> user：<span class="user">' + datas.data[0]["destDbInfo"][i].destDbUser + '</span></div>' +
                            '</li>'
                }
                $('.destDbList').html('')
                if (list2) {
                    $('.destDbList').append(list2).next('.empty').hide()
                } else {
                    $('.destDbList').next('.empty').html(tip).show()
                }
                form.render('select');//重点：重新渲染select
            })
        })
        // 选择目标数据库
        $(document).on('click', '.destDbList li', function () {
            if($(this).attr('data') == $('.destDbList .active').attr('data')) return
            $(this).addClass('active').siblings().removeClass()
            var currentDb = $(this)
            $.ajax({
                url: "/exTableDict/getDestTablesAndFields",
                data: {"destDbId": currentDb.attr('data'),"gatherId":$("#gatherDesc").val()},
                type: "GET"
            })
                    .success(function (datas){
                        $("#destDb3").html(currentDb.find('.name').html());
                        var option2 = "<option value='-1'>请选择目标表</option>";//初始化option的选项
                        for (var p = 0; p < datas.data[0]["destTables"].length; p++) {
                            option2 += "<option value='" + datas.data[0]["destTables"][p]['text'] + "'>" + datas.data[0]["destTables"][p]['text'] + "</option>";//拼接option中的内容
                            $("#destTables").html(option2);//将option的拼接内容加入select中，注意选择器是select的ID
                        }
                        $('.next').addClass('layui-btn-disabled').attr('disabled', true);
                        form.render('select');//重点：重新渲染select
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
    // 提交任务
    function saveTask(){
        $.ajax({
            url: "/exJobTask/saveTask",
            type: "POST",
            contentType: 'application/json',
            data: JSON.stringify({
                "taskName": $("#taskName").val(),
                "sourceDbId":$(".sourceDbList li.active").attr('data'),
                "destDbId": $(".destDbList li.active").attr('data'),
                "sourceTableName": $("#sourceTables").val(),
                "destTableName": $("#destTables").val(),
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
                        layer.confirm('是否继续添加任务', {title: '提示'}, function (index) {
                            window.location.reload()
                            layer.close(index);
                        }, function (index) {
                            window.open('/exJobTask/page', "_self")
                            layer.close(index);
                        });
                    }
                });
            }
        })
    }

</script>
</@layout>


