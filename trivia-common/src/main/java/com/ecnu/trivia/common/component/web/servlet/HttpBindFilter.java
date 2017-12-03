/** Created by Jack Chen at 12/11/2014 */
package com.ecnu.trivia.common.component.web.servlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 进行http对象绑定
 *
 * @author Jack Chen
 */
public class HttpBindFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //nothing to do
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        HttpServletContext.setContext(httpServletRequest, httpServletResponse);

        chain.doFilter(request, response);

        HttpServletContext.removeContext();
    }

    @Override
    public void destroy() {
        //nothing to do
    }
}
