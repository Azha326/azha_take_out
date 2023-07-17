package com.azha.azha_take_out.filter;

import com.alibaba.fastjson.JSON;
import com.azha.azha_take_out.common.BaseContext;
import com.azha.azha_take_out.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response=(HttpServletResponse)servletResponse;

        //获取本次请求URI
        String requestURI=request.getRequestURI();
        //判断本次请求是否需要处理
        String[] whiteListUris = {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg**",
                "/user/login**",

        };

        boolean check = check(requestURI, whiteListUris);
        //如果不需要处理直接放行
        if(check){
            log.info("本次请求{}无需处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //判断网页登录状态，如果已经登录直接放行-1
        if(request.getSession().getAttribute("employee") != null){
            Long empId=(Long)request.getSession().getAttribute("employee");
            log.info("用户已登录，ID为{}",empId);
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }
        //判断移动端登录状态，如果已经登录直接放行-2
        if(request.getSession().getAttribute("user") != null){
            Long userId=(Long)request.getSession().getAttribute("user");
            log.info("用户已登录，ID为{}",userId);
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }
        //如果未登录如果返回未登录结果,通过输出流向客户端页面响应数据
        log.info("用户未登录,或是ID未能存入Session");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    //路径匹配检查
    public boolean check(String requestURI,String[] urls){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match)return true;
        }
        return false;
    }
}
