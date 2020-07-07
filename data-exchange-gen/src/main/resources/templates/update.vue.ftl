<template>
    <el-dialog
        title="修改"
        :visible="show"
        :before-close="handleCancel"
        :close-on-click-modal="false"
    >
        <div v-loading="loadingGet" element-loading-background="transparent">
            <el-form :model="formData" :rules="rules" ref="form">
<#list table.fields as field>
<#if field.keyFlag>
<#else>
<#if field.propertyType == 'Date'>
<#if field.type=="date">
                <el-form-item label="${field.comment!field.propertyName}：" prop="${field.propertyName}" label-width="100px">
                    <el-date-picker
                        type="date"
                        placeholder="选择日期"
                        v-model="formData.${field.propertyName}"
                        style="width: 100%;"
                        value-format="yyyy-MM-dd"
                    ></el-date-picker>
                </el-form-item>
<#else>
                <el-form-item label="${field.comment!field.propertyName}：" prop="${field.propertyName}" label-width="100px">
                    <el-date-picker
                        type="datetime"
                        placeholder="选择时间"
                        v-model="formData.${field.propertyName}"
                        style="width: 100%;"
                        value-format="yyyy-MM-dd HH:mm:ss"
                    ></el-date-picker>
                </el-form-item>
</#if>
<#else>
                <el-form-item label="${field.comment!field.propertyName}：" prop="${field.propertyName}" label-width="100px">
                    <el-input v-model="formData.${field.propertyName}"></el-input>
                </el-form-item>
</#if>
</#if>
</#list>
            </el-form>
        </div>
        <div slot="footer" class="dialog-footer">
            <el-button @click="handleCancel" size="small">取消</el-button>
            <el-button type="primary" @click="handleSubmit" size="small" :loading="loadingSubmit">确定</el-button>
        </div>
    </el-dialog>
</template>

${'<script>'}
import ${table.name}_api from '../../api/${table.name}_api'

export default {
    created() {
        this.loadingGet = true
        ${table.name}_api.get(this.id).then(data => {
            this.loadingGet = false
            this.formData = data
        }).catch(err => {
            this.loadingGet = false
            this.$message.error(err.message)
        })
    },
    props: {
        show: Boolean,
        success: Function,
        cancel: Function,
        id: Number,
    },
    data() {
        return {
            loadingGet: false,
            loadingSubmit: false,
            formData: {},
            rules: {
<#list table.fields as field>
<#if field.keyFlag>
<#else>
                ${field.propertyName}: [
                    { required: true, message: '请输入${field.comment!field.propertyName}', trigger: 'blur' }
                ],
</#if>
</#list>
            },
        }
    },
    methods: {
        handleSubmit() {
            this.$refs.form.validate(valid => {
                if (valid) {
                    this.loadingSubmit = true
                    ${table.name}_api.update(this.formData).then(data => {
                        this.loadingSubmit = false
                        this.$message({
                            type: 'success',
                            message: '修改成功!'
                        })
                        this.success()
                    }).catch(err => {
                        this.loadingSubmit = false
                        this.$message.error(err.message)
                    })
                }
            })
        },
        handleCancel() {
            this.cancel()
        }
    }
}
${'</script>'}

<style scoped>
</style>
