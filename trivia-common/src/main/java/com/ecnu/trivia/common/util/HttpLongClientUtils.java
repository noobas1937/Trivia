package com.ecnu.trivia.common.util;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import java.util.Map;

/**
 * Created by zhenjie tang at 2016/4/22
 *
 * @author zhenjie tang
 */
public class HttpLongClientUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpLongClientUtils.class);
    private static CloseableHttpAsyncClient httpAsyncLongClient;

    static{
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(30000)
                .setConnectTimeout(30000).build();
        httpAsyncLongClient = HttpAsyncClients.custom()
                .setDefaultRequestConfig(requestConfig).setMaxConnTotal(100).setMaxConnPerRoute(100)//此处为多并发设置
                .build();
        httpAsyncLongClient.start();
    }


    /**
     * http client 异步调用
     *
     * @param method
     *            http method
     * @param url
     *            http request url
     * @param headers
     *            http header data
     * @param reqEntity
     *            http request body data
     * @returnId
     *
     */
    public static void callAsyncLongClient(HttpMethod method, String url,
                                       Map<String, String> headers, StringBuffer reqEntity,
                                       Map<String, String> parameters) {
        HttpUriRequest methodRequest=HttpClientUtils.getMethodRequest(method, url, headers, reqEntity, parameters);
        try{
            httpAsyncLongClient.execute(methodRequest,null);
        } catch (Exception e) {
            logger.error("HttpLongClientUtils.callAsyncLongClient throw Exception:", e);
        }
    }

    public static void closeAsyncLongClient(){
        try{
        httpAsyncLongClient.close();
        } catch (Exception e) {
            logger.error("HttpLongClientUtils.closeAsyncLongClient throw Exception:", e);
        }
    }
}
