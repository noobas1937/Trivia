/**
 * Created by Jack Chen at 11/9/2014
 */
package com.ecnu.trivia.common.component.web.mvc.interceptor;

import com.google.common.collect.Maps;
import com.ecnu.trivia.common.component.json.JsonUtils;
import com.ecnu.trivia.common.component.web.HttpRespCode;
import com.ecnu.trivia.common.exception.ExceptionStatus;
import com.ecnu.trivia.common.log.ErrorStackLogger;
import com.ecnu.trivia.common.log.MethodInvokeInfo;
import com.ecnu.trivia.common.log.MethodInvokeStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jack Chen
 */
public class ExceptionInterceptor extends AbstractHandlerExceptionResolver {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionInterceptor.class);
    private static final Logger errorStackLogger = ErrorStackLogger.logger;
    private static final int defaultStatusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
    /**
     * 异常头的key值
     */
    private static final String exceptionHeaderKey = "message";
    private Map<Class, Integer> exceptionStatusMapping = Maps.newIdentityHashMap();

    @PostConstruct
    public void init() {
        exceptionStatusMapping.put(Exception.class, defaultStatusCode);
        exceptionStatusMapping.put(Throwable.class, defaultStatusCode);
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (errorStackLogger.isErrorEnabled()) {
            //记录栈
            errorStackLogger.error("程序调用出错:" + ex.getMessage(), ex);

            //记录调用栈
            errorStackLogger.error("此次出错的所有调用栈如下:");
            MethodInvokeInfo loggerMethodInfo;
            while ((loggerMethodInfo = MethodInvokeStack.pop()) != null) {
                errorStackLogger.error(loggerMethodInfo.toDetailMessage());
                errorStackLogger.error("------------------------------");
            }
        }

        try {
//            int statusCode = getStatusCode(ex);
//            String message = ex.getMessage();
            HttpRespCode respCode = HttpRespCode.INTERNAL_SERVER_ERROR;
            //如果原异常为数据库异常，则不能将数据库异常直接返回给前台，而转换为其它异常信息
//            if(ex instanceof DataAccessException || ex instanceof SQLException)
//                message = "当前访问数据出错，请联系客服或检查输入数据";

//            if(ex instanceof NestedRuntimeException)
//                message = "当前系统运行时异常,请联系客服或检查输入数据";

            //如果原异常为NullPointerException，则相应的message为null
//            if(ObjectUtils.isNullOrEmpty(message))
//                message = "NullPointerException";
//            if (ex instanceof OlapException) {
//                respCode = HttpRespCode.OLAP_EXCEPTION;
//            }

//            String convertedMessage = URLEncoder.encode(message, "UTF8");//url编码为UTF-8,在客户端以decodeURLComponent解码
//            response.addHeader(exceptionHeaderKey, convertedMessage);
//            response.setContentType("text/html");//保证客户端响应正确文本
//            response.setCharacterEncoding("UTF-8");//保证客户端响应正确编码
//
//            response.setStatus(statusCode);

            //将数据返回至body中
            response.setContentType("application/json;charset=utf-8");
            Map resp = new HashMap();
            resp.put("resCode", respCode.getCode());
            resp.put("resMsg", respCode.getText());
            resp.put("resTime", System.currentTimeMillis());
            String message = JsonUtils.toJson(resp, false);

            //转码处理，防止输出出现脚本注入 因为在前台进行接收此数据是通过js来接收的，因此这里将<>等转换为hex，而不是&lt;以便script能正常接收
//            String convertedMessage = JavaScriptUtils.javaScriptEscape(message);
//            OutputStream outputStream = response.getOutputStream();
//            outputStream.write(convertedMessage.getBytes(Charsets.UTF_8)); //这里直接将相应的错误信息显示输出中
//            outputStream.flush();
//            outputStream.close();
            response.getWriter().print(message);
        } catch (IOException e) {
            logger.error("ExceptionInterceptor:", e.getMessage(), e);
        }

        return new ModelAndView();
    }

    private int getStatusCode(Exception e) {
        Class<?> sourceClazz = e.getClass();
        Integer status = exceptionStatusMapping.get(sourceClazz);
        int statusV = status == null ? 0 : status;
        if (statusV != 0) {
            return statusV;
        }

        Class<?> clazz = sourceClazz;
        while (statusV == 0 && clazz != Object.class) {
            //尝试两种匹配,common中设置的以及spring mvc中设置的异常均可
            ExceptionStatus exceptionStatus = clazz.getAnnotation(ExceptionStatus.class);
            if (exceptionStatus != null) {
                statusV = exceptionStatus.value().getValue();
            } else {
                ResponseStatus responseStatus = clazz.getAnnotation(ResponseStatus.class);
                if (responseStatus != null) {
                    statusV = responseStatus.value().value();
                }
            }
            clazz = clazz.getSuperclass();
        }

        if (statusV == 0) {
            statusV = defaultStatusCode;
        }
        exceptionStatusMapping.put(sourceClazz, statusV);

        return statusV;
    }
}
