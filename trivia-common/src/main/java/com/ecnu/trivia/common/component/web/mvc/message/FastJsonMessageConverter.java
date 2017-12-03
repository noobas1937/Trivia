/**
 * Created by Jack Chen at 12/12/2014
 */
package com.ecnu.trivia.common.component.web.mvc.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.google.common.base.Charsets;
import com.ecnu.trivia.common.component.json.JsonUtils;
import com.ecnu.trivia.common.component.json.format.JsonFormat;
import com.ecnu.trivia.common.component.json.format.JsonFormats;
import com.ecnu.trivia.common.component.json.limit.JsonLimit;
import com.ecnu.trivia.common.component.web.mvc.interceptor.MethodInjectInterceptor;
import com.ecnu.trivia.common.component.web.servlet.HttpServletContext;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;

/**
 * 扩展默认的消息处理器,以支持特定的数据输出
 *
 * @author Jack Chen
 */
public class FastJsonMessageConverter extends FastJsonHttpMessageConverter implements
        GenericHttpMessageConverter<Object> {
    static {
        //必须初始化JsonUtils
        //以触发jsonConfig.setAsmEnable(false);parseConfig.setAsmEnable(false);//与上同理
        new JsonUtils();
    }

    @Override
    protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException,
            HttpMessageNotWritableException {
        //处理限制信息
        HttpServletRequest request = HttpServletContext.getRequest();
        HandlerMethod handlerMethod = (HandlerMethod) request.getAttribute(MethodInjectInterceptor.HANDLER_METHOD_ATTRIBUTE);
        String[] limitProperties = {};
        JsonFormat[] jsonFormatArray = null;
        if (handlerMethod != null) {
            JsonLimit jsonLimit = handlerMethod.getMethodAnnotation(JsonLimit.class);
            if (jsonLimit != null) {
                limitProperties = jsonLimit.value();
            }

            JsonFormats jsonFormats = handlerMethod.getMethodAnnotation(JsonFormats.class);
            if (jsonFormats != null) {
                jsonFormatArray = jsonFormats.value();
            }

            JsonFormat jsonFormat = handlerMethod.getMethodAnnotation(JsonFormat.class);
            if (jsonFormat != null) {
                jsonFormatArray = new JsonFormat[]{jsonFormat};
            }
        }

        OutputStream out = outputMessage.getBody();
        String text = jsonFormatArray == null ? JsonUtils.toBrowserJson(obj, limitProperties) : JsonUtils.toBrowserJson(obj, jsonFormatArray, limitProperties);
        byte[] bytes = text.getBytes(Charsets.UTF_8);
        out.write(bytes);
    }

    @Override
    public boolean canRead(Type type, Class contextClass, MediaType mediaType) {
        return canRead(mediaType);
    }

    /**
     * 用于支持泛型的参数解析,如 [1,2,3] 解析为List<Long> 时能够正确的解析，避免解析为 List<Integer>
     */
    @Override
    public Object read(Type type, Class contextClass, HttpInputMessage inputMessage) throws IOException,
            HttpMessageNotReadableException {
        byte[] bytes = FileCopyUtils.copyToByteArray(inputMessage.getBody());
        return JSON.parseObject(bytes, 0, bytes.length, UTF8.newDecoder(), type);
    }

    //@Override
    @Override
    public boolean canWrite(Type type, Class<?> aClass, MediaType mediaType) {
        return canWrite(mediaType);
    }

    //@Override
    @Override
    public void write(
            Object o, Type type, MediaType mediaType, HttpOutputMessage httpOutputMessage
    ) throws IOException, HttpMessageNotWritableException {
        writeInternal(o, httpOutputMessage);
    }
}
