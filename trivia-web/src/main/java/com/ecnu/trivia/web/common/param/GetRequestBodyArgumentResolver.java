/** Created by Jack Chen at 5/25/2015 */
package com.ecnu.trivia.web.common.param;

import com.alibaba.fastjson.JSON;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 实现getRequestBody的参数解析
 *
 * @author Jack Chen
 */
public class GetRequestBodyArgumentResolver implements HandlerMethodArgumentResolver {
    /** 必须有RequestParamMap注释，同时必须是map的子类 */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(GetRequestBody.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        GetRequestBody getRequestBody = parameter.getParameterAnnotation(GetRequestBody.class);

        String paramKey = getRequestBody.value();
        String paramValue = webRequest.getParameter(paramKey);

        if(paramValue == null) {
            return null;
        }

        return JSON.parseObject(paramValue, parameter.getGenericParameterType());
    }
}
