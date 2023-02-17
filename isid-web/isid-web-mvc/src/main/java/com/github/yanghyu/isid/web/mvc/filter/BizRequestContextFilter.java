package com.github.yanghyu.isid.web.mvc.filter;


import com.github.yanghyu.isid.common.core.request.BizRequestContextVariableAssist;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter("/*")
public class BizRequestContextFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Do nothing
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
            BizRequestContextVariableAssist.init(httpServletRequest);
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            BizRequestContextVariableAssist.close();
        }
    }

    @Override
    public void destroy() {
        // Do nothing
    }

}
