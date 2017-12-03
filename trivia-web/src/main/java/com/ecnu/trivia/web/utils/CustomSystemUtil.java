package com.ecnu.trivia.web.utils;

import com.alibaba.fastjson.JSON;
import com.ecnu.trivia.web.utils.http.HttpRespons;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
* @description 获取公网IP
* @author Jack Chen
* @date 2017/10/15
*/
public class CustomSystemUtil {
    private static HttpUtils httpclient = new HttpUtils();

    /**
     * @param args
     * @throws Exception
     * @author Jack Chen
     */
    public static void main(String[] args) throws Exception {
        while(true){
            String ip = CustomSystemUtil.getWebIP("http://ip.chinaz.com/getip.aspx");
            Map param = new HashMap(1);
            param.put("ip",ip);
            httpclient.sendPost("http://115.159.35.11:8080/pb/restful/session/ip",param);
            System.out.println("本机的外网IP是："+ip);
            Thread.sleep(1000*300);
        }
    }


    /**
     * 获取外网地址
     * @param strUrl
     * @return
     * @author Jack Chen
     */
    public static String getWebIP(String strUrl) {
        HttpRespons reps = null;
        try {
            reps = httpclient.sendGet(strUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JSON.parseObject(reps.content).get("ip").toString();
    }
}
