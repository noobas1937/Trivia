/**
 * @Title: ResourceUtils.java
 * @Package com.iresearch.core.utils
 * @Description: TODO
 * Copyright: Copyright (c) 2016
 * Company:艾瑞咨询
 * @author Iresearch-billzhuang
 * @date 2016年3月22日 上午10:39:47
 * @version V1.0.0
 */
package com.ecnu.trivia.web.utils;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;


/**
 * @ClassName: ResourceUtils
 * @Description: 资源文件工具类
 * @author Iresearch-billzhuang
 * @date 2016年3月22日 上午10:39:47
 *
 */
public class ResourceUtils {

    private ResourceBundle resourceBundle;

    private ResourceUtils(String resource) {
        resourceBundle = ResourceBundle.getBundle(resource);
    }

    /**
     * 获取资源
     * @param resource 资源
     * @return 解析
     */
    public static ResourceUtils getResource(String resource) {
        return new ResourceUtils(resource);
    }

    /**
     * 根据key取得value
     * @param key 键值
     * @param args value中参数序列，参数:{0},{1}...,{n}
     * @return
     */
    public String getValue(String key, Object... args) {
        String temp = resourceBundle.getString(key);
        return MessageFormat.format(temp, args);
    }

    /**
     * 获取所有资源的Map表示
     * @return 资源Map
     */
    public Map<String, String> getMap() {
        Map<String, String> map = new HashMap<String, String>();
        for (String key : resourceBundle.keySet()) {
            map.put(key, resourceBundle.getString(key));
        }
        return map;
    }
}
