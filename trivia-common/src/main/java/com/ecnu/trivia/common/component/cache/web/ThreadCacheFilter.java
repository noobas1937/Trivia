/** Created by Jack Chen at 15-7-20 */
package com.ecnu.trivia.common.component.cache.web;

import javax.servlet.*;
import java.io.IOException;

/**
 * 用于实现线程缓存的清理
 *
 * @author Jack Chen
 */
public class ThreadCacheFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
        ThreadCache.clearThreadCache();
    }

    @Override
    public void destroy() {
    }
}
