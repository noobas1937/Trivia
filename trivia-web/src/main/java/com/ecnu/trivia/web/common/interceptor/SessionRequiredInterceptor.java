/**
 * Created by zhenjie.tang at 15-8-27
 */
package com.ecnu.trivia.web.common.interceptor;

import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.rbac.utils.UserUtils;
import com.ecnu.trivia.web.utils.Resp;
import com.ecnu.trivia.web.utils.json.JSONUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截当前用户身份验证
 *
 * @author zhenjie.tang
 */
public class SessionRequiredInterceptor extends HandlerInterceptorAdapter {

    private String mappingURL;

    public void setMappingURL(String mappingURL) {
        this.mappingURL = mappingURL;
    }

    /**
     * 返回异常
     *
     * @param resp
     */
    private void responseException(Resp resp, HttpServletResponse response) throws Exception {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().print(JSONUtils.toJsonString(resp));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //判断请求URL是否合法
        String url = request.getRequestURI();
        if (mappingURL != null && !url.matches(mappingURL)) {
            responseException(new Resp(HttpRespCode.PERMISSION_ERROR), response);
            return false;
        }

        //判断用户是否登录
        if (UserUtils.fetchUser().equals(User.nullUser())) {
            responseException(new Resp(HttpRespCode.USER_NOT_LOGIN), response);
            return false;
        }
        return true;
    }
}
