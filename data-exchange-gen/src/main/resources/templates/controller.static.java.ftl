package ${package.Controller};

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ${package.Service}.${table.serviceName};
import com.sdy.common.utils.DateUtil;
import com.sdy.common.utils.StringUtil;
import ${package.Entity}.${entity};
import com.sdy.common.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
<#if restControllerStyle>
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
<#else>
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;
</#if>
<#if superControllerClassPackage??>
import ${superControllerClassPackage};
</#if>
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

<#list table.fields as field>
    <#if field.keyFlag>
        <#assign keyPropertyName="${field.propertyName}"/>
        <#assign keyPropertyType="${field.propertyType}"/>
    </#if>
</#list>
/**
 * <p>
 * <#if table.comment??>${table.comment}<#else> </#if> 前端控制器
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Slf4j
<#if restControllerStyle>
@RestController
<#else>
@Controller
</#if>
@RequestMapping("<#if package.ModuleName??>/${package.ModuleName}</#if>/<#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if>")
<#if kotlin>
class ${table.controllerName}<#if superControllerClass??> : ${superControllerClass}()</#if>
<#else>
<#if superControllerClass??>
public class ${table.controllerName} extends ${superControllerClass} {
<#else>
public class ${table.controllerName} {
</#if>
</#if>
    @Autowired
    private ${table.serviceName} ${table.entityPath}Service;

    /**
     * 页面
     */
    @GetMapping("/page")
    public String page() {
        return "/page/${table.name}/page";
    }

    /**
     * 新建页面
     */
    @GetMapping("/newPage")
    public String newPage() {
        return "/page/${table.name}/new";
    }
    
    /**
     * 详情页面
     */
    @GetMapping("/detailPage")
    public String detailPage(Model model, Integer id) {
        model.addAttribute("param", ${table.entityPath}Service.getById(id));
        return "/page/${table.name}/detail";
    }
    
    /**
     * 修改页面
     */
    @GetMapping("/updatePage")
    public String updatePage(Model model, Integer id) {
        model.addAttribute("param", ${table.entityPath}Service.getById(id));
        return "/page/${table.name}/update";
    }

    /**
     * 分页数据
     * @param request
     * @param page [current, size]
     * @return
     */
    @GetMapping("/pageData")
    @ResponseBody
    public Response pageData(HttpServletRequest request, Page<${entity}> page) {
        QueryWrapper<${entity}> wrapper = new QueryWrapper<>();

        String ${keyPropertyName} = request.getParameter("${keyPropertyName}");
        wrapper.eq(StringUtil.isNotBlank(${keyPropertyName}), "${keyPropertyName}", ${keyPropertyName});

        String key = request.getParameter("key");
        wrapper.eq(StringUtil.isNotBlank(key), "${keyPropertyName}", key);

        String createTime = request.getParameter("createTime");
        if (StringUtil.isNotBlank(createTime)) {
            wrapper.between("create_time",
                DateUtil.getDate(createTime + " 00:00:00", DateUtil.DATETIME_FORMAT),
                DateUtil.getDate(createTime + " 23:59:59", DateUtil.DATETIME_FORMAT));
        }

        String createTimeRange = request.getParameter("createTimeRange");
        if (StringUtil.isNotBlank(createTimeRange)) {
            String[] timeRangeStrs = createTimeRange.split(" - ");
            wrapper = wrapper.between("create_time",
                    DateUtil.getDate(timeRangeStrs[0], DateUtil.DATETIME_FORMAT),
                    DateUtil.getDate(timeRangeStrs[1], DateUtil.DATETIME_FORMAT));
        }

        wrapper.orderByDesc("${keyPropertyName}");

        IPage<${entity}> result = ${table.entityPath}Service.page(page, wrapper);
        return Response.success(result);
    }

    /**
     * 保存数据
     * @param
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public Response save(${entity} ${table.entityPath}) {
        return Response.success(${table.entityPath}Service.save(${table.entityPath}));
    }

    /**
     * 更新数据
     * @param
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    public Response update(${entity} ${table.entityPath}) {
        return Response.success(${table.entityPath}Service.updateById(${table.entityPath}));
    }

    /**
     * 删除数据
     * @param id 主键
     * @return
     */
    @GetMapping("/delete")
    @ResponseBody
    public Response delete(${keyPropertyType} id) {
        return Response.success(${table.entityPath}Service.removeById(id));
    }

    /**
     * 删除数据
     * @param id 主键
     * @return
     */
    @GetMapping("/deleteBatch")
    @ResponseBody
    public Response deleteBatch(${keyPropertyType}[] id) {
        if (id != null) {
            ${table.entityPath}Service.removeByIds(Arrays.asList(id));
        }
        return Response.success();
    }
}
