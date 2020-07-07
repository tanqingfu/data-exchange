package com.sdy.dataexchange.web.controller;

import com.sdy.mvc.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author tqf
 * @Description 测试页面
 * @Version 1.0
 * @since 2020-07-07 16:19
 */
@Controller
@RequestMapping("/Test")
public class TestController extends BaseController {

    @RequestMapping(value = "/page",method =  RequestMethod.GET)
    public  String test(HttpServletRequest request, HttpServletResponse response) throws IOException{
        request.setAttribute("age",52);
        return  "/page/test/test";
    }
}
