/** Created by Jack Chen at 12/8/2014 */
package com.ecnu.trivia.common.util;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.ecnu.trivia.common.component.CompositeV2;
import com.ecnu.trivia.common.exception.IRCloudException;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;

/**
 * 实现httpClient的基本功能
 * 包括GET,POST功能
 *
 * @author Jack Chen
 */
public class HttpClientUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);
    private static final int DEFAULT_CONNECTION_TIMEOUT = 20000;//默认连接时间 20秒
    private static final int DEFAULT_SOCKET_TIMEOUT = 30000;//默认超时时间 30秒
    private static final RequestConfig defaultRequestConfig;

    private static final CloseableHttpClient httpClient;

    //private static final  CloseableHttpAsyncClient httpAsyncClient = HttpAsyncClientBuilder.create().build();


    private HttpClientUtils(){

    }

    /** 描述进行http处理的错误信息 */
    public static class ErrorMessage extends CompositeV2<Integer, String> {
        public final int status;
        public final String message;

        public ErrorMessage(Integer first, String second) {
            super(first, second);
            this.status = first;
            this.message = second;
        }
    }

    static {
        try{
            RequestConfig.Builder builder = RequestConfig.custom();
            builder.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
            builder.setSocketTimeout(DEFAULT_SOCKET_TIMEOUT);
            defaultRequestConfig = builder.build();

            HttpClientBuilder clientBuilder = HttpClientBuilder.create();
            clientBuilder.setDefaultRequestConfig(defaultRequestConfig);
            clientBuilder.setConnectionReuseStrategy(NoConnectionReuseStrategy.INSTANCE);
            clientBuilder.setDefaultHeaders(Lists.newArrayList(new BasicHeader("connection", "close")));//不使用长连接
            /** 以下代码为支持不验证指定域名的https请求处理 */
//            clientBuilder.setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
//                @Override
//                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
//
//                }
//
//                @Override
//                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
//
//                }
//
//                @Override
//                public X509Certificate[] getAcceptedIssuers() {
//                    return null;
//                }
//            }}, null);
//            clientBuilder.setSslcontext(sslContext);
            httpClient = clientBuilder.build();
        }catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /** HTTP Get 获取内容 */
    public static String doGet(final String url, Map<String, String> params, Charset charset) throws IOException {
        return doGet(url, params, charset, Functions.<String>identity(), new Function<ErrorMessage, String>() {
            @Override
            public String apply(ErrorMessage input) {
                throw new IRCloudException(StringUtils.format("请求错误，返回状态码{},请求地址:{} :结果:{}", input.status, url, input.message));
            }
        });
    }

    public static <T> T doGet(String url, Map<String, String> params, Charset charset, Function<String, T> successFunction, Function<ErrorMessage, T> failFunction) throws IOException {
        if(!ObjectUtils.isNullOrEmpty(params)) {
            List<NameValuePair> pairs = ListUtils.asList(params, new Function<Map.Entry<String, String>, NameValuePair>() {
                @Override
                public NameValuePair apply(Map.Entry<String, String> input) {
                    return new BasicNameValuePair(input.getKey(), input.getValue());
                }
            });

            boolean hasQuest = url.contains("?");
            url += (hasQuest ? "&" : "?") + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
        }

        logger.debug("GET 请求地址:{}", url);

        HttpGet httpGet = new HttpGet(url);
        try(CloseableHttpResponse response = httpClient.execute(httpGet)){
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            String result = null;
            if(entity != null) {
                result = EntityUtils.toString(entity, charset);
            }
            EntityUtils.consume(entity);

            if(statusCode < 200 || statusCode >= 300) {
                return failFunction.apply(new ErrorMessage(statusCode, result));
            } else {
                return successFunction.apply(result);
            }
        }
    }

    /** HTTP Post 获取内容 */
    public static String doPost(final String url, Map<String, String> params, Charset charset) throws IOException {
        return doPost(url, params, charset, Functions.<String>identity(), new Function<ErrorMessage, String>() {
            @Override
            public String apply(ErrorMessage input) {
                throw new IRCloudException(StringUtils.format("请求错误，返回状态码{},请求地址:{} :结果:{}", input.status, url, input.message));
            }
        });
    }

    /** HTTP Post 获取内容，并按照成功和失败分别处理 */
    public static <T> T doPost(String url, Map<String, String> params, Charset charset, Function<String, T> successFunction, Function<ErrorMessage, T> failFunction) throws IOException {
        HttpPost httpPost = new HttpPost(url);

        if(!ObjectUtils.isNullOrEmpty(params)) {
            List<NameValuePair> pairs = ListUtils.asList(params, new Function<Map.Entry<String, String>, NameValuePair>() {
                @Override
                public NameValuePair apply(Map.Entry<String, String> input) {
                    return new BasicNameValuePair(input.getKey(), input.getValue());
                }
            });

            httpPost.setEntity(new UrlEncodedFormEntity(pairs, charset));
        }

        try(CloseableHttpResponse response = httpClient.execute(httpPost)){
            int statusCode = response.getStatusLine().getStatusCode();

            HttpEntity entity = response.getEntity();
            String result = null;
            if(entity != null) {
                result = EntityUtils.toString(entity, charset);
            }

            EntityUtils.consume(entity);

            if(statusCode < 200 || statusCode >= 300) {
                return failFunction.apply(new ErrorMessage(statusCode, result));
            } else {
                return successFunction.apply(result);
            }
        }
    }


    public static StringBuilder httpCommonCall(HttpMethod method, String url,
                                       Map<String, String> headers, StringBuffer reqEntity,
                                       Map<String, String> parameters) {
        HttpUriRequest methodRequest=getMethodRequest(method, url, headers, reqEntity, parameters);
        return executeHttpCall(methodRequest);
    }

//    /**
//     * http client 异步调用
//     *
//     * @param method
//     *            http method
//     * @param url
//     *            http request url
//     * @param headers
//     *            http header data
//     * @param reqEntity
//     *            http request body data
//     * @returnId
//     *
//     */
//    public static StringBuilder httpAsyncCall(HttpMethod method, String url,
//                                       Map<String, String> headers, StringBuffer reqEntity,
//                                       Map<String, String> parameters) {
//        HttpUriRequest methodRequest=getMethodRequest(method, url, headers, reqEntity, parameters);
//        return executeHttpAsyncCall(httpAsyncClient,methodRequest);
//    }

     static HttpUriRequest getMethodRequest(HttpMethod method, String url,
                                                   Map<String, String> headers, StringBuffer reqEntity,
                                                   Map<String, String> parameters){
            if (Objects.isNull(url)) {
                throw new RuntimeException("http request url is null!");
            }
            HttpUriRequest methodRequest;
            if (!org.apache.commons.collections.MapUtils.isEmpty(parameters)) {
                List<NameValuePair> listParam = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> entry : parameters.entrySet()) {
                    listParam.add(new BasicNameValuePair(String.valueOf(entry
                            .getKey()), String.valueOf(entry.getValue())));
                }
                url += "?" + URLEncodedUtils.format(listParam, Charsets.UTF_8);
            }
            switch(method){
                case GET:
                    methodRequest = new HttpGet(url);
                    break;
                case POST:
                    methodRequest = new HttpPost(url);
                    break;
                case PUT:
                    methodRequest = new HttpPut(url);
                    break;
                case DELETE:
                    methodRequest = new HttpDelete(url);
                    break;
                default:
                    methodRequest = new HttpGet(url);
            }

            if (!org.apache.commons.collections.MapUtils.isEmpty(headers)) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    methodRequest.addHeader(entry.getKey(), entry.getValue());
                }
            }

            if (reqEntity != null && reqEntity.length() > 0
                    && (methodRequest instanceof HttpPut || methodRequest instanceof HttpPost)) {
                ((HttpEntityEnclosingRequestBase) methodRequest).setEntity(new StringEntity(
                        reqEntity.toString(), Consts.UTF_8));
            }
        return methodRequest;
    }

    static StringBuilder executeHttpCall(HttpUriRequest methodRequest){
        StringBuilder sb=new StringBuilder();
        try{
            //启动httpclient进程
            HttpClient client = HttpClientBuilder.create().build();
            HttpResponse response = client.execute(methodRequest);
            if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                processResponseEntity(response.getEntity(), sb);
            }
        } catch (Exception e) {
            logger.error("HttpClientUtil.executeHttpCall throw Exception:", e);
        }
        return sb;
    }

    static StringBuilder executeHttpAsyncCall(CloseableHttpAsyncClient httpAsyncClient,HttpUriRequest methodRequest){
        StringBuilder sb=new StringBuilder();
        try{
            //启动http异步client进程
            httpAsyncClient.start();
            Future<HttpResponse> future  = httpAsyncClient.execute(methodRequest,null);
            HttpResponse response = future.get();
            if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                processResponseEntity(response.getEntity(), sb);
            }
        } catch (Exception e) {
            logger.error("HttpClientUtil.httpAsyncCall throw Exception:", e);
        }
        return sb;
    }

    private static void processResponseEntity(HttpEntity resEntity,StringBuilder sb) throws IOException {
        if(resEntity != null) {
            InputStream is = null;
            BufferedReader br = null;
            try{
                is = resEntity.getContent();
                if(resEntity.getContentEncoding() != null
                        && resEntity.getContentEncoding().getValue()
                        .contains("gzip")) {
                    is = new GZIPInputStream(is);
                }
                br = new BufferedReader(new InputStreamReader(is, Consts.UTF_8));
                while(br.readLine() != null) {
                    sb.append(br.readLine());
                }
            } finally {
                try{
                    is.close();
                    br.close();
                } catch(Exception e2) {
                    logger.error("HttpClientUtil.processResponseEntity throw exception when closing the stream:", e2);
                }
            }
        }
    }
}
