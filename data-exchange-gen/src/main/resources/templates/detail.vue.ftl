<template>
    <el-dialog
        title="查看"
        :visible="show"
        :before-close="handleCancel"
        :close-on-click-modal="false"
    >
        <div v-loading="loading" element-loading-background="transparent">
            <el-form :model="formData">
<#list table.fields as field>
<#if field.keyFlag>
<#else>
                <el-form-item label="${field.comment!field.propertyName}：" label-width="100px">{{formData.${field.propertyName}}}</el-form-item>
</#if>
</#list>
            </el-form>
        </div>
        <div slot="footer" class="dialog-footer">
            <el-button @click="handleCancel" size="small">关闭</el-button>
        </div>
    </el-dialog>
</template>

${'<script>'}
import ${table.name}_api from '../../api/${table.name}_api'

export default {
    created() {
        this.loading = true
        ${table.name}_api.get(this.id).then(data => {
            this.formData = data
            this.loading = false
        }).catch(err => {
            this.$message.error(err.message)
            this.loading = false
        })
    },
    props: {
        show: Boolean,
        cancel: Function,
        id: Number
    },
    data() {
        return {
            formData: {},
            loading: false
        }
    },
    methods: {
        handleCancel() {
            this.cancel()
        }
    }
}

${'</script>'}

<style scoped>
</style>
