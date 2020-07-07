package com.sdy.dataexchange.web.interceptor;

import com.sdy.auth.api.AuthApi;
import com.sdy.auth.api.model.MenuDto;
import com.sdy.auth.api.model.UserDto;
import com.sdy.auth.api.model.UserInfo;
import com.sdy.auth.client.service.SsoService;
import com.sdy.common.model.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class BizInterceptor extends HandlerInterceptorAdapter {
    @Value("${res.app.code}")
    private String appCode;
    @Autowired
    private SsoService ssoService;
    @Autowired
    private AuthApi authApi;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        request.setCharacterEncoding("UTF-8");
        if (modelAndView != null && (response.getContentType() == null || !(response.getContentType().contains("json") || response.getContentType().contains("JSON")))) {
            String uri = request.getRequestURI().replace(request.getContextPath(), "");
            UserInfo userInfo = ssoService.getUserInfo(request);
            if (userInfo != null) {
                MenuDto appMenu = authApi.menu(ssoService.getUserInfo(request).getUserId(), appCode).getData().getChildren().get(0);//authService.findMenu(userInfo.getUserId(), resAppId).getChildren().get(0);
                String html = renderMenuHtml(appMenu, uri);
                modelAndView.addObject("menuHtml", html);
                List<MenuDto> resStack = getResStack(appMenu, uri);
                String breadcrumb = "";
                for (MenuDto sysResource : resStack) {
                    breadcrumb = "<a><cite>".concat(sysResource.getName()).concat("</cite></a>").concat(breadcrumb);
                }
                modelAndView.addObject("breadcrumb", breadcrumb);
                modelAndView.addObject("userInfo", getUserInfo(request));
            }
        }
        if(modelAndView != null) {
            String viewName = modelAndView.getViewName();
//            log.info("{} to location :{}", StringUtil.startsWith(viewName, "redirect:")?"redirect":"forward", viewName);
        }
    }

    private List<MenuDto> getResStack(MenuDto res, String currentUrl) throws BizException {
        List<MenuDto> l = res.toList();
        List<MenuDto> curr = l.stream().filter(item -> currentUrl.equals(item.getUrl())).collect(Collectors.toList());
        List<MenuDto> result = new ArrayList<>();
        if (curr.size() > 0) {
            Map<Integer, MenuDto> resMap = new HashMap<>();
            l.forEach(item -> resMap.put(item.getId(), item));
            MenuDto tmp = curr.get(0);
            while (tmp != null) {
                result.add(tmp);
                tmp = resMap.get(tmp.getParentId());
            }
        }
        return result;
    }

    /**
     * 后端渲染菜单，防止前端闪烁
     */
    private String renderMenuHtml(MenuDto data, String currentUrl) {
        return data.getChildren().stream().map(item -> renderMenuHtmlRecurse(item, currentUrl)).collect(Collectors.joining());
    }

    private String renderMenuHtmlRecurse(MenuDto data, String currentUrl) {
        String clazz = (currentUrl != null && currentUrl.equals(data.getUrl())) ? "layui-this" : "";
        String html = "<li class=\"layui-nav-item " + clazz + "\">";
        String icon = data.getIcon() == null ? "" : data.getIcon();
        if (data.getType().equals(2)) {
            if (data.getChildren() != null && data.getChildren().size() > 0) {
                html = html.concat("<a href=\"javascript:;\"><i class=\"layui-icon ").concat(icon).concat("\"></i>").concat(data.getName()).concat("</a>");
                html = html.concat("<dl class=\"layui-nav-child\">");
                for (MenuDto child : data.getChildren()) {
                    html = html.concat("<dd>").concat("<a href=\"").concat(child.getUrl()).concat("\">").concat(child.getName()).concat("</a>").concat("</dd>");
//                    html = html.concat("<dd>").concat(renderMenuHtmlRecurse(child, currentUrl)).concat("</dd>");
                }
                html = html.concat("</dl>");
            } else {
                html = html.concat("<a href=\"").concat(data.getUrl()).concat("\"><i class=\"layui-icon ").concat(icon).concat("\"></i>").concat(data.getName()).concat("</a>");
            }
        }
        html = html.concat("</li>");
        return html;
    }

    private Map<String, Object> getUserInfo(HttpServletRequest request) {
        Integer userId = ssoService.getUserInfo(request).getUserId();
        UserDto user = authApi.userInfo(userId).getData();
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("imgUrl", user.getImgUrl());
        userInfo.put("name", user.getName());
        return userInfo;
    }
}
