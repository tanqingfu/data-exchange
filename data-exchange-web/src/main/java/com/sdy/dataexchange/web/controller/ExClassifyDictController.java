package com.sdy.dataexchange.web.controller;

//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.sdy.dataexchange.biz.service.ExClassifyDictService;
//import com.sdy.common.utils.DateUtil;
//import com.sdy.common.utils.StringUtil;
//import com.sdy.dataexchange.biz.model.ExClassifyDict;
//import com.sdy.common.model.Response;

import com.sdy.mvc.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wyy
 * @since 2019-09-11
 */
@Slf4j
@Controller
//@ResponseBody
@RequestMapping("/exClassifyDict")
public class ExClassifyDictController extends BaseController {
//    @Autowired
//    private ExClassifyDictService exClassifyDictService;

    /**
     * 页面
     */
    @GetMapping("/page")
    public String page() {
        return "/page/sourceDb";
    }


    @GetMapping("/jobData")
    public String page2() {
        return "/page/collectTask";
    }

}
