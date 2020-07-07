<#include "../layout/mainLayout.ftl">
<@layout cssPath=["../../static/module/css/temp.css","../../static/module/css/icon.css"]>

<#--<link rel="stylesheet" href="../../static/module/css/icon.css">-->
<div class="content" style="display: none">
    <div class="easyui-layout">
        <div region="north" border="false" class="rtitle">
            <label>交换节点查询：</label>
            <input class="easyui-searchbox"
                   data-options="prompt:'请输入交换节点名称',searcher:doSearch, position: 'left'"
                   style="width:300px"></input>
            <!--<label style="margin-left: 20px;">数据库2查询：</label>-->
            <!--<input class="easyui-searchbox"-->
            <!--data-options="prompt:'Please Input Value',searcher:doSearch, position: 'right'"-->
            <!--style="width:300px"></input>-->
            <a href="javascript:;" class="easyui-linkbutton" onclick="relate()"
               style="margin-left: 20px;margin-right: 20px;">字段关联</a>
            <a href="javascript:;" class="easyui-linkbutton" onclick="addRelate()">新增同步表</a>
            <a href="javascript:;" class="easyui-linkbutton" onclick="addGather()">新增交换节点</a>
        </div>
        <div region="south" split="true" border="false" class="footer">
        <#--<p>&nbsp;&nbsp;&nbsp;&nbsp;同步信息展示台</p>-->
            <div id="footerTabs" class="easyui-tabs" style="width:100%;height:100%"></div>
        </div>
        <div region="center" border="false">
            <div class="left">
                <div class="easyui-layout">
                    <div region="west" title="数据库列表1" split="true" border="false"
                         style="width:150px;background:#EAFDFF;">
                        <ul id="left-channels" data-position="left"></ul>
                    </div>
                    <div region="center" border="false">
                        <table id="left-table" height="100%"
                               idField="field"
                               iconCls="icon-save"
                               data-options="url:''"
                               toolbar="#leftTableBtns">
                            <thead>
                            <tr>
                                <th field="ck" checkbox="true" width="5%"></th>
                                <th field="field" width="55%">字段名</th>
                                <th field="fieldType" width="40%">字段类型</th>
                            </tr>
                            </thead>
                        </table>
                        <div id="leftTableBtns">
                            <div class="channel-user"></div>
                            <div class="channel-city"></div>
                            <!--<a href="#" class="easyui-linkbutton"-->
                            <!--data-options="iconCls:'icon-add',plain:true,position:'left'"-->
                            <!--onclick="tableBtnClick(this,'add')">新增</a>-->
                            <!--<a href="#" class="easyui-linkbutton"-->
                            <!--data-options="iconCls:'icon-cut',plain:true,position:'left'"-->
                            <!--onclick="tableBtnClick(this,'cut')">删除</a>-->
                            <!--<a href="#" class="easyui-linkbutton"-->
                            <!--data-options="iconCls:'icon-save',plain:true,position:'left'"-->
                            <!--onclick="tableBtnClick(this,'save')">保存</a>-->
                        </div>
                    </div>
                </div>
            </div>
            <div class="right">
                <div class="easyui-layout">
                    <div region="west" title="数据库列表2" split="true" border="false"
                         style="width:150px;background:#EAFDFF;">
                        <ul id="right-channels" data-position="right"></ul>
                    </div>
                    <div region="center" border="false">
                        <table id="right-table" height="100%"
                               idField="field"
                               iconCls="icon-save"
                               data-options="url:''"
                               toolbar="#rightTableBtns">
                            <thead>
                            <tr>
                                <th field="ck" checkbox="true" width="5%"></th>
                                <th field="field" width="55%">字段名</th>
                                <th field="fieldType" width="40%">字段类型</th>
                            </tr>
                            </thead>
                        </table>
                        <div id="rightTableBtns">
                            <div class="channel-user"></div>
                            <div class="channel-city"></div>
                            <!--<a href="#" class="easyui-linkbutton"-->
                            <!--data-options="iconCls:'icon-add',plain:true,position:'left'"-->
                            <!--onclick="tableBtnClick(this,'add')">新增</a>-->
                            <!--<a href="#" class="easyui-linkbutton"-->
                            <!--data-options="iconCls:'icon-cut',plain:true,position:'left'"-->
                            <!--onclick="tableBtnClick(this,'cut')">删除</a>-->
                            <!--<a href="#" class="easyui-linkbutton"-->
                            <!--data-options="iconCls:'icon-save',plain:true,position:'left'"-->
                            <!--onclick="tableBtnClick(this,'save')">保存</a>-->
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div id="win-addTabs" class="easyui-dialog" title="新增同步表" style="width:400px;height:200px;"
             data-options="resizable:true,modal:true,buttons:[{
				text:'确定',
				handler:saveRelate
			},{
				text:'取消',
				handler:closeRelate
			}]" closed="true">
            <div>
                <label>源数据库表：</label>
                <input id="leftNoRelate-Tabs" class="easyui-combobox" name="dept"
                       data-options="valueField:'value',textField:'text'">
            </div>
            <div>
                <label>目标数据库表：</label>
                <input id="rightNoRelate-Tabs" class="easyui-combobox" name="dept"
                       data-options="valueField:'value',textField:'text'">
            </div>
        </div>
        <div id="win-addGather" class="easyui-dialog" title="新增交换节点" style="width:420px;height:420px;"
             data-options="resizable:true,modal:true,buttons:[{
				text:'确定',
				handler:saveGather
			},{
				text:'取消',
				handler:closeGather
			}]" closed="true">
            <div>
                <label>交换节点名称：</label>
                <input type="text" class="easyui-textbox" id="newGatherName">
            </div>
            <div>
                <label>源库：</label>
                <input id="leftGather-db" class="easyui-combobox"
                       data-options="valueField:'value',textField:'db_desc',required:true">
            </div>
            <div>
                <label>用户名：</label>
                <input id="leftGather-dbUser" class="easyui-combobox"
                       data-options="valueField:'value',textField:'text',required:true">
            </div>
            <div>
                <label>源库所属地：</label>
                <input id="leftGather-city">
            </div>
            <div>
                <label>源库所属科室：</label>
                <input id="leftGather-office" class="easyui-combobox"
                       data-options="valueField:'value',textField:'text',required:true">
            </div>
            <div>
                <label>目标库：</label>
                <input id="rightGather-db" class="easyui-combobox"
                       data-options="valueField:'value',textField:'db_desc',required:true">
            </div>
            <div>
                <label>目标库所属地：</label>
                <input id="rightGather-city">
            </div>
            <div>
                <label>目标库所属科室：</label>
                <input id="rightGather-office" class="easyui-combobox"
                       data-options="valueField:'value',textField:'text',required:true">
            </div>
        </div>
    </div>
</div>

</@layout>
<@js>
<script>
    $(function () {
        var leftHasRelate,
                leftNoRelate,
                rightHasRelate,
                rightNoRelate;
        $('#left-channels,#right-channels').tree({
            url: '',
            method: "GET",
            formatter: function (node) {
                return node.text;
            },
            onSelect: function (node) {
                // 获取字段并重新渲染
                var name = node.text,
                        table = '#' + $(this).attr('data-position') + '-table',
                        btns = '#' + $(this).attr('data-position') + 'TableBtns',
                        parent = $(this).tree('getParent', node.target),
                        id = parent ? parent.db_id : '';
                var user = parent ? parent.user ? parent.user : '' : node.user ? node.user : '';
                var officeName = parent ? parent.officeName ? parent.officeName : '' : node.officeName ? node.officeName : '';
                if (user) {
                    $(btns + ' .channel-user').text('用户：' + user)
                    $(btns + ' .channel-city').text('所属单位：' + officeName)
                } else {
                    $(btns + ' .channel-user').text('')
                    $(btns + ' .channel-city').text('')
                }
                if (!parent) return false;
                if ($(this).attr('data-position') == 'left') {
                    $.get('/ShowInfoController/showInfo?tableNames=' + name + '&db_id=' + id, function (result) {
                        if (result.leftFields) {
                            $('#left-table').datagrid('uncheckAll');
                            $('#left-table').datagrid('loadData', result.leftFields);
                        }
                        if (result.rightTree) {
                            result.rightTree.forEach(function (cur, index) {
                                if (cur.children && cur.children.length > 0) {
                                    cur.children.forEach(function (item, i) {
                                        if (item.checked || item.selected) item.id = '1'
                                    })
                                }
                            })
                            $('#right-channels').tree('loadData', result.rightTree);
                            setTimeout(function () {
                                $('#right-channels').tree('select', $('#right-channels').tree('find', 1).target)
                            }, 0)
                        }
                    })
                } else {
                    var leftTable = $('#left-channels').tree('getSelected') || $('#left-channels').tree('getChecked'),
                            leftId = $('#left-channels').tree('getParent', leftTable.target).db_id;
                    $(table).datagrid('uncheckAll');
                    $(table).datagrid('options').url = '/GetSyncFields/getSyncFieldsByTableName?tableNames=' + name + '&db_id=' + id + '&leftTableNames=' + leftTable.text + '&leftDb_id=' + leftId;
                    $(table).datagrid('load');
                }
            },
            onLoadSuccess: function (node, data) {
                // 初始全部收起
//                $(this).tree('collapseAll')
//                $('#tree').tree('find', 'tree_resource')

            }
        });
        $('#left-table,#right-table').datagrid({
            method: "GET",
            onSelect: function (indexidrow) {
            },
            onLoadSuccess: function () {
                // 取消选中
//                $(this).datagrid('uncheckAll');
                // 选中第一行
                var rows = $(this).datagrid('getRows');

                if (rows.length) {
                    for (var i = 0; i < rows.length; i++) {
                        if (rows[i].checked) $(this).datagrid('selectRow', i);
                    }
//                    $(this).datagrid('selectRow', 0);
                }
            }
        });
        $('#leftGather-city').combotree({
            url: '/GetAllOrganizationsController/getAllOrganzitions',
            method: 'get',
            required: true,
            onSelect: function (node) {
                $('#leftGather-city').combotree('setValue', node.value)
                $.get("/GetAllOfficeController/getAllOffice", {
                    id: node.id
                }, function (result) {
                    $('#leftGather-office').combobox('clear').combobox('loadData', result)
                })
            },
            onLoadSuccess: function (node, data) {
                // 初始选中id为01的选项
                $(this).tree('collapseAll')
//                $('#leftGather-city').combotree('setValue', 01)
            }
        });
        $('#rightGather-city').combotree({
            url: '/GetAllOrganizationsController/getAllOrganzitions',
            method: 'get',
            required: true,
            onSelect: function (node) {
                $('#rightGather-city').combotree('setValue', node.value)
                $.get("/GetAllOfficeController/getAllOffice", {
                    id: node.id
                }, function (result) {
                    $('#rightGather-office').combobox('clear').combobox('loadData', result)
                })
            },
            onLoadSuccess: function (node, data) {
                // 初始选中id为01的选项
                $(this).tree('collapseAll')
//                $('#rightGather-city').combotree('setValue', 01)
            }
        });
        $('#footerTabs').tabs({
            border: false,
            onSelect: function (title) {
            }
        });
        $('#leftGather-db').combobox({
            onSelect: function (node) {
//                $('#leftGather-dbUser').combobox('loadData', node.user)
                $('#leftGather-dbUser').combobox('options').url = '/GetUserByDbDescController/getUserByDbDesc?dbDesc=' + node.value;
                $('#leftGather-dbUser').combobox('reload');
            }
        });
        $('#leftGather-dbUser').combobox({
            url: '',
            method: "GET"
        })
        $(window).resize(function () {
            $('.easyui-layout').layout('resize')
        })
        $('.content').show()
        $('.easyui-layout').layout('resize')
    })

    function doSearch(value) { // 搜索框事件
        $.get("/GetDbAndTables/getDbAndTb?gather_desc=" + value, function (result) {
            if (result.message) $.messager.alert("提示", result.message);
            leftHasRelate = result.data[0].sourceDoneDbAndTable,
                    leftNoRelate = result.data[0].sourceUndoDbAndTable,
                    rightHasRelate = result.data[1].destDoneDbAndTable,
                    rightNoRelate = result.data[1].destUndoDbAndTable;
            $('#left-channels').tree('loadData', leftHasRelate)
            $('#right-channels').tree('loadData', rightHasRelate)
            $('#left-table,#right-table').datagrid('loadData', {rows: []});
        })
    }

    function relate() {
        var leftTab = $('#left-table').datagrid('getChecked'), rightTab = $('#right-table').datagrid('getChecked'),
                data, leftTabs = [], leftTypes = [], rightTabs = [], rightTypes = [];
        var leftTabList = $('#left-channels').tree('getSelected'),
                leftDb = $('#left-channels').tree('getParent', leftTabList.target);
        var rightTabList = $('#right-channels').tree('getSelected'),
                rightDb = $('#right-channels').tree('getParent', rightTabList.target);
        for (var i = 0; i < leftTab.length; i++) {
            leftTabs.push(leftTab[i].field)
            leftTypes.push(leftTab[i].fieldType)
        }
        for (var j = 0; j < rightTab.length; j++) {
            rightTabs.push(rightTab[j].field)
            rightTypes.push(rightTab[j].fieldType)
        }
        data = {
            left: {
                tab: leftTabList.text, // 表
                db: leftDb.db_id, // 库
                fields: leftTabs.join(','), // 字段list
                fieldType: leftTypes.join(',')
                //fieldtypes:leftTab.COLUMN_TYPE

            },
            right: {
                tab: rightTabList.text,
                db: rightDb.db_id,
                fields: rightTabs.join(','),
                fieldType: rightTypes.join(',')
                //fieldtypes:rightTab.COLUMN_TYPE
            }
        }
        $.post("/InsertInfo/handle", data, function (result) {
            if (result.status == 200) {
                $.messager.confirm('提示', '字段关联成功，是否立即同步?', function (r) {
                    if (r) {
                        $.post("/InsertFlag/insertFlag", data, function (result1) {
                            $.messager.alert("提示", result1.message);
                            insertFlag()
                            if (insertFlagTime) clearInterval(insertFlagTime)
                            insertFlagTime = setInterval(insertFlag, 3600000)
                        });
                    } else {
                    }
                });
            } else {
                $.messager.alert("提示", result.message);
            }
        })
    }

    $.messager.defaults.ok = '确定'
    $.messager.defaults.cancel = '取消'
    var insertFlagTime;

    function insertFlag(type) {
        $.get("/ShowDataController/showData", function (result) {
            var content = '', selected = $('#footerTabs').tabs('getSelected')
            if (type == 'refresh') {
                content = ''
            } else {
                content = selected ? selected[0].innerHTML : ''
            }
            result.forEach(function (list) {
                content = '<div>' + '时间:' + (list.swaData || '') + ' ' + '用户名:' + ' ' +
                        list.userName + ' ' +
                        '源库名:' + list.sourceDbName + '-' +
                        '源同步表名:' + list.sourceName + ' ===>' +
                        '目标库名:' + list.destDbName + '-' +
                        '目标同步表名:' + list.destName + ' 已同步 --- ' +
                        list.swaGross + '条</div>' + content;
            })

            if (selected) {
                $('#footerTabs').tabs('update', {
                    tab: selected,
                    options: {
                        content: content
                    }
                })
            } else {
                $('#footerTabs').tabs('add', {
                    title: '同步信息展示台',
                    content: content,
                    tools: [{
                        iconCls: 'icon-mini-refresh',
                        handler: function () {
                            insertFlag('refresh')
                        }
                    }],
                    closable: false
                });
            }
        })
    }

    function addRelate() {
        var leftDb = $('#left-channels').tree('getSelected'), rightDb = $('#right-channels').tree('getSelected')
        if (leftDb && !leftDb.children) leftDb = $('#left-channels').tree('getParent', leftDb.target)
        if (rightDb && !rightDb.children) rightDb = $('#right-channels').tree('getParent', rightDb.target)
        if (leftDb) {
            if (leftNoRelate && leftNoRelate.length > 0) {
                leftNoRelate.forEach(function (obj, index) {
                    if (obj.text === leftDb.text) {
                        if (obj.children && obj.children.length > 0) {
                            $('#leftNoRelate-Tabs').combobox('loadData', obj.children)
                            getRight()
                        } else {
                            $.messager.alert("提示", "当前原库无可新增关联表");
                        }
                    }
                })
            } else {
                $.messager.alert("提示", "当前原库无可新增关联表");
            }
        } else {
            $.messager.alert("提示", "请选择需要关联的原库");
        }

        function getRight() {
            if (rightDb) {
                if (rightNoRelate && rightNoRelate.length > 0) {
                    rightNoRelate.forEach(function (obj, index) {
                        if (obj.text === rightDb.text) {
                            if (obj.children && obj.children.length > 0) {
                                $('#rightNoRelate-Tabs').combobox('loadData', rightNoRelate[index].children)
                                $('#win-addTabs').dialog("open")
                            } else {
                                $.messager.alert("提示", "当前目标库无可新增关联表");
                            }
                        }
                    })
                } else {
                    $.messager.alert("提示", "当前目标库无可新增关联表");
                }
            } else {
                $.messager.alert("提示", "请选择需要关联的目标库");
            }
        }
    }

    function saveRelate() {
        var leftDb = $('#left-channels').tree('getSelected'), rightDb = $('#right-channels').tree('getSelected'),
                leftValue = $('#leftNoRelate-Tabs').combobox('getValue'),
                rightValue = $('#rightNoRelate-Tabs').combobox('getValue'),
                leftList = $('#leftNoRelate-Tabs').combobox('getData'),
                rightList = $('#rightNoRelate-Tabs').combobox('getData');
        if (leftDb && !leftDb.children) leftDb = $('#left-channels').tree('getParent', leftDb.target);
        if (rightDb && !rightDb.children) rightDb = $('#right-channels').tree('getParent', rightDb.target);
        if (!leftValue || !rightValue) return;
        postRelate(leftDb, rightDb, leftValue, rightValue, function () {
            $('#left-channels').tree('append', {
                parent: leftDb.target,
                data: getData(leftList, leftValue)
            })
            $('#right-channels').tree('append', {
                parent: rightDb.target,
                data: getData(rightList, rightValue)
            })
            deleteData(leftNoRelate, leftDb, leftList, leftValue)
            deleteData(rightNoRelate, rightDb, rightList, rightValue)
            $('#leftNoRelate-Tabs,#rightNoRelate-Tabs').combobox('clear')
            closeRelate()
        })

        function getData(list, value) {
            for (var i = 0; i < list.length; i++) {
                if (list[i].text === value) {
                    return list[i]
                    break
                }
            }
        }

        function deleteData(list1, db, list2, listValue) {
            var del = false, off = false
            for (var i = 0; i < list1.length; i++) {
                if (list1[i].text === db.text) {
                    var children = list1[i].children
                    for (var z = 0; z < list2.length; z++) {
                        if (list2[z].text === listValue) {
                            children.splice(z, 1)
                            del = true
                            break
                        }
                    }
                }
                if (del) {
                    break
                }
            }
        }
    }

    function postRelate(leftDb, rightDb, leftValue, rightValue, callback) {
        $.post('/AddTableController/addTable', {
            source_db_id: leftDb.db_id,
            source_tableName: leftValue,
            dest_db_id: rightDb.db_id,
            dest_tableName: rightValue
        }, function (result) {
            if (callback && typeof(callback) == 'function') callback();
            $.messager.alert("提示", "新增同步表成功!");
        })
    }

    function closeRelate(callback) {
        $('#win-addTabs').dialog("close")
        if (callback && typeof(callback) == 'function') callback()
    }

    function addGather() {
        $.get("/GetAllDatabaseController/getAllDatabase", function (result) {
            $('#leftGather-db').combobox('loadData', result[0])
            $('#rightGather-db').combobox('loadData', result[1])
        })
        $('#win-addGather').dialog("open")
    }

    function saveGather() {
        var leftValue = $('#leftGather-db').combobox('getValue'),
                rightValue = $('#rightGather-db').combobox('getValue'),
                gatherName = $('#newGatherName').textbox('getValue'),
                leftOfficeName = $('#leftGather-office').combobox('getValue'),
                rightOfficeName = $('#rightGather-office').combobox('getValue'),
                userName = $('#leftGather-dbUser').combobox('getValue')
        if (!leftValue || !rightValue) return;
        $.post('/AddGatherController/addGather', {
            'gather_desc': gatherName,
            'source_desc': leftValue,
            'dest_desc': rightValue,
            'left_id': leftOfficeName,
            'right_id': rightOfficeName,
            'sourceUser': userName
        }, function (result) {
            $.messager.alert("提示", result.message);
            $('#leftGather-db,#rightGather-db,#leftGather-office,#rightGather-office').combobox('clear')
            $('#newGatherName').textbox('clear')
            $('#leftGather-city,#rightGather-city').combotree('reload')
            closeGather()
        })
    }

    function closeGather() {
        $('#win-addGather').dialog("close")
    }
</script>
</@js>