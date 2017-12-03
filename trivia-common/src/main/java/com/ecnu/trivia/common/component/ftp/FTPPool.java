package com.ecnu.trivia.common.component.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 初始化连接池的相关配置信息
 */
@Component("fTPPool")
public class FTPPool extends Pool<FTPClient> implements InitializingBean {

    @Resource
    private FTPFactory ftpFactory;
    /** 连接池最大容量 */
    @Value("${ftp.pool.max.max.total:50}")
    private int maxTotal;

    @Override
    public void afterPropertiesSet() throws Exception {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        //最大池容量
        config.setMaxTotal(maxTotal);
        //池为空时取对象等待的最大毫秒数.
        config.setMaxWaitMillis(60 * 1000);
        //取出对象时验证(此处设置成验证ftp是否处于连接状态).
        config.setTestOnBorrow(true);
        //还回对象时验证(此处设置成验证ftp是否处于连接状态).
        config.setTestOnReturn(true);

        this.internalPool = new GenericObjectPool<>(ftpFactory, config);
    }
}
