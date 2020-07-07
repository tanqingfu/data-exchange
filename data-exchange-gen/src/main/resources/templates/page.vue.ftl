<#list table.fields as field>
    <#if field.keyFlag>
        <#assign keyPropertyName="${field.propertyName}"/>
    </#if>
</#list>
<template>
    <el-card class="box-card" shadow="never">
        <el-form :inline="true" :model="queryForm" class="demo-form-inline" size="small">
            <el-form-item label="主键">
                <el-select v-model="queryForm.${keyPropertyName}" placeholder="主键" clearable>
                    <el-option label="1" value="1"></el-option>
                    <el-option label="2" value="2"></el-option>
                    <el-option label="3" value="3"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="名称">
                <el-input v-model="queryForm.key" placeholder="主键" clearable></el-input>
            </el-form-item>
            <el-form-item label="创建日期">
                <el-date-picker
                    type="date"
                    placeholder="选择日期"
                    v-model="queryForm.createTime"
                    style="width: 100%;"
                    value-format="yyyy-MM-dd"
                ></el-date-picker>
            </el-form-item>
            <el-form-item label="创建时间">
                <el-date-picker
                    v-model="queryForm.createTimeRange"
                    type="datetimerange"
                    range-separator="至"
                    start-placeholder="开始时间"
                    end-placeholder="结束时间"
                    align="right"
                    value-format="yyyy-MM-dd HH:mm:ss"
                ></el-date-picker>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="onQuery">查询</el-button>
            </el-form-item>
        </el-form>
        <el-button type="primary" @click="onAdd" size="small">添加</el-button>
        <${table.name?replace('_', '-')}-add
            v-if="showAddDialog"
            :show="showAddDialog"
            :success="onAddSuccess"
            :cancel="onAddCancel"
        ></${table.name?replace('_', '-')}-add>
        <${table.name?replace('_', '-')}-update
            v-if="showUpdateDialog"
            :show="showUpdateDialog"
            :success="onUpdateSuccess"
            :cancel="onUpdateCancel"
            :id="updateId"
        ></${table.name?replace('_', '-')}-update>
        <${table.name?replace('_', '-')}-detail
            v-if="showDetailDialog"
            :show="showDetailDialog"
            :cancel="onDetailCancel"
            :id="detailId"
        ></${table.name?replace('_', '-')}-detail>
        <table-ex ref="tableEx" :queryCallback="queryCallback">
<#list table.fields as field>
            <el-table-column prop="${field.propertyName}" label="${field.comment!field.propertyName}"></el-table-column>
</#list>
            <el-table-column label="操作" width="120">
                <template slot-scope="scope">
                    <el-button type="text" size="small" @click="onDetail(scope.row.${keyPropertyName})">查看</el-button>
                    <el-button type="text" size="small" @click="onUpdate(scope.row.${keyPropertyName})">编辑</el-button>
                    <el-button type="text" size="small" @click="onDelete(scope.row.${keyPropertyName})">删除</el-button>
                </template>
            </el-table-column>
        </table-ex>
    </el-card>
</template>

${'<script>'}
import ${table.name}_api from '../../api/${table.name}_api'
import ${entity}Add from './${entity}Add.vue'
import ${entity}Update from './${entity}Update.vue'
import ${entity}Detail from './${entity}Detail.vue'
import TableEx from '../../components/TableEx.vue'

export default {
    components: {
        ${entity}Add,
        ${entity}Update,
        ${entity}Detail,
        TableEx,
    },
    data() {
        return {
            queryForm: {
                ${keyPropertyName}: '',
                key: '',
                createTime: '',
                createTimeRange: ''
            },
            showAddDialog: false,
            showUpdateDialog: false,
            showDetailDialog: false,
            updateId: null,
            detailId: null,
        }
    },
    methods: {
        queryCallback(param) {
            return ${table.name}_api.findPage({
                query: this.queryForm,
                ...param
            })
        },
        onQuery(type = 1) {
            this.$refs.tableEx.refresh(type)
        },
        onAdd() {
            this.showAddDialog = true
        },
        onAddSuccess() {
            this.showAddDialog = false
            this.onQuery()
        },
        onAddCancel() {
            this.showAddDialog = false
        },
        onUpdate(id) {
            this.updateId = id
            this.showUpdateDialog = true
        },
        onUpdateSuccess() {
            this.showUpdateDialog = false
            this.onQuery(2)
        },
        onUpdateCancel() {
            this.showUpdateDialog = false
        },
        onDetail(id) {
            this.detailId = id
            this.showDetailDialog = true
        },
        onDetailCancel() {
            this.showDetailDialog = false
        },
        onDelete(id) {
            this.$confirm('此操作将永久删除该记录, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                ${table.name}_api.delete(id).then(data => {
                    this.$message({
                        type: 'success',
                        message: '删除成功!'
                    })
                    this.onQuery(3)
                }).catch(err => {
                    this.$message.error(err.message)
                })
            }).catch(err => {
            })
        }
    }
}
${'</script>'}

<style scoped>
</style>

