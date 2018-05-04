package com.lab.commons.security.interceptor;

import com.lab.commons.consts.SessionName;
import eu.bitwalker.useragentutils.Manufacturer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.wah.doraemon.security.exception.BrowserNotSupportException;
import org.wah.doraemon.utils.UserAgentUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class PageInterceptor extends HandlerInterceptorAdapter{

    //路径匹配器
    private static final AntPathMatcher matcher = new AntPathMatcher();

    //忽略拦截列表
    @Getter
    @Setter
    private List<String> excludes;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        //忽略
        String url = request.getRequestURI().substring(request.getContextPath().length());
        if(excludes != null && !excludes.isEmpty()){
            for(String exclude : excludes){
                if(matcher.match(exclude, url)){
                    return true;
                }
            }
        }

        //是否允许IE访问
        Boolean allowed = (Boolean) request.getSession().getAttribute(SessionName.ALLOW_IE);
        if(allowed != null && allowed){
            return true;
        }

        //判断浏览器
        String userAgent = request.getHeader("User-Agent");
        Manufacturer manufacturer = UserAgentUtils.getManufacturer(userAgent);
        //微软厂商
        if(Manufacturer.MICROSOFT.equals(manufacturer)){
            throw new BrowserNotSupportException("不支持微软厂商的客户端");
        }

        return true;
    }
}