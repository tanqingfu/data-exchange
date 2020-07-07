package com.sdy.dataexchange.web.controller;

import com.sdy.common.model.Response;
import com.sdy.common.utils.EncodeUtil;
import com.sdy.dataexchange.biz.constants.RedisConstants;
import com.sdy.mvc.controller.BaseController;
import com.sdy.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.KeyPair;
import java.util.Base64;

/**
 * Created by zzq on 2019/1/10.
 */
@Slf4j
@Controller
public class IndexController extends BaseController {
    @Autowired
    private RedisService redisService;

    @GetMapping("/")
    public void index(HttpServletResponse response) throws Exception {
        response.sendRedirect("/exGatherDict/page");
    }

    @GetMapping("/exGatherDict")
    public String index1() throws Exception {
        return "/page/ex_gather_dict/page";
    }

    @GetMapping("/publicKey")
    @ResponseBody
    public Response publicKey(HttpServletRequest request) throws Exception {
        KeyPair keyPair = EncodeUtil.rsaGenKeyPair();
        String publicKeyStr = new String(Base64.getEncoder().encode(keyPair.getPublic().getEncoded()));
        redisService.set(RedisConstants.REDIS_PREFIX + "privateKey_" + getUserId(request), Base64.getEncoder().encode(keyPair.getPrivate().getEncoded()));
        return Response.success(publicKeyStr);
    }

}
