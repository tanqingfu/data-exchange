package ${package.Entity};

<#list table.importPackages as pkg>
<#if pkg!="com.baomidou.mybatisplus.annotation.IdType">
import ${pkg};
</#if>
</#list>
<#assign idAs=1>
<#list table.fields as field>
<#if field.name == 'id'>
<#assign idAs=2>
</#if>
</#list>
<#if idAs==1>
import com.baomidou.mybatisplus.annotation.TableId;
</#if>
<#assign jfAssign=1>
<#list table.fields as field>
<#if field.propertyType == 'Date' && jfAssign==1>
import com.fasterxml.jackson.annotation.JsonFormat;
<#assign jfAssign=2>
</#if>
</#list>
<#if entityLombokModel>
<#if cfg.seq>
import com.baomidou.mybatisplus.annotation.KeySequence;
</#if>
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
</#if>

/**
 * <p>
 * <#if table.comment??>${table.comment}<#else> </#if>
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
<#if entityLombokModel>
@Data
<#if superEntityClass??>
@EqualsAndHashCode(callSuper = true)
<#else>
@EqualsAndHashCode(callSuper = false)
</#if>
@Accessors(chain = true)
</#if>
<#if table.convert>
@TableName("${table.name}")
</#if>
<#if cfg.seq>
@KeySequence("SEQ_${table.name}")
</#if>
<#if superEntityClass??>
public class ${entity} extends ${superEntityClass}<#if activeRecord><${entity}></#if> {
<#elseif activeRecord>
public class ${entity} extends Model<${entity}> {
<#else>
public class ${entity} implements Serializable {
</#if>
    private static final long serialVersionUID = 1L;
<#-- ----------  BEGIN 字段循环遍历  ---------->
<#list table.fields as field>
<#if field.keyFlag>
<#assign keyPropertyName="${field.propertyName}"/>
</#if>

<#if field.comment!?length gt 0>
    /**
     * ${field.comment}
     */
</#if>
<#if field.keyFlag>
<#-- 主键 -->
<#if field.keyIdentityFlag>
    @TableId
    <#--@TableId(type = IdType.AUTO)-->
<#elseif idType??>
    @TableId(type = IdType.${idType})
<#elseif field.convert>
    @TableId("${field.name}")
<#else>
    @TableId
</#if>
<#-- 普通字段 -->
<#elseif field.fill??>
<#-- -----   存在字段填充设置   ----->
<#if field.convert>
    @TableField(value = "${field.name}", fill = FieldFill.${field.fill})
<#else>
    @TableField(fill = FieldFill.${field.fill})
</#if>
<#elseif field.convert>
    @TableField("${field.name}")
</#if>
<#-- 乐观锁注解 -->
<#if (versionFieldName!"") == field.name>
    @Version
</#if>
<#-- 逻辑删除注解 -->
<#if (logicDeleteFieldName!"") == field.name>
    @TableLogic
</#if>
<#if field.propertyType == 'Date'>
    <#assign dttype="DATETIME_FORMAT">
    <#if field.type=="date">
        <#assign dttype="DATE_FORMAT">
    </#if>
    @JsonFormat(pattern = ${dttype})
</#if>
    private ${field.propertyType} ${field.propertyName};
</#list>
<#------------  END 字段循环遍历  ---------->
<#if !entityLombokModel>
<#list table.fields as field>
<#if field.propertyType == "boolean">
    <#assign getprefix="is"/>
<#else>
    <#assign getprefix="get"/>
</#if>
    public ${field.propertyType} ${getprefix}${field.capitalName}() {
        return ${field.propertyName};
    }

<#if entityBuilderModel>
    public ${entity} set${field.capitalName}(${field.propertyType} ${field.propertyName}) {
<#else>
    public void set${field.capitalName}(${field.propertyType} ${field.propertyName}) {
</#if>
        this.${field.propertyName} = ${field.propertyName};
<#if entityBuilderModel>
        return this;
</#if>
    }
</#list>
</#if>

<#if entityColumnConstant>
<#list table.fields as field>
    public static final String ${field.name?upper_case} = "${field.name}";

</#list>
</#if>
<#if activeRecord>
    @Override
    protected Serializable pkVal() {
<#if keyPropertyName??>
        return this.${keyPropertyName};
<#else>
        return null;
</#if>
    }

</#if>
<#if !entityLombokModel>
    @Override
    public String toString() {
        return "${entity}{" +
<#list table.fields as field>
<#if field_index==0>
        "${field.propertyName}=" + ${field.propertyName} +
<#else>
        ", ${field.propertyName}=" + ${field.propertyName} +
</#if>
</#list>
        "}";
    }
</#if>
}
