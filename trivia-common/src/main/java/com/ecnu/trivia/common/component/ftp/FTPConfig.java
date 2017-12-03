package com.ecnu.trivia.common.component.ftp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * ftp 配置类
 */
@Component("fTPConfig")
public class FTPConfig {
    @Value("${ftp.host:}")
    private String host;

    @Value("${ftp.port:}")
    private Integer port;

    @Value("${ftp.username:}")
    private String userName;

    @Value("${ftp.password:}")
    private String password;

    @Value("${ftp.path:}")
    private String path;
    /**
     * 重连的间隔时间
     * 默认20秒
     */
    private int reconnectTimeout = 1000 * 20;
    /**
     * 为0 不控制超时,单位毫秒
     * 默认1分钟
     */
    private int connectTimeout = 1000 * 60;
    /**
     * 为0 打开数据连接为长暂停，单位毫秒
     */
    private int dataTimeout = -1;
    /**
     * 设置的等待时间之间发送控制连接保持活动消息 当处理文件上传或下载。
     * 为0 禁用 单位毫秒
     */
    private int controlKeepAliveTimeout = 1000 * 60;
    /**
     * 设置等待多久控制保持活着的回复。
     * 单位毫秒
     */
    private int controlKeepAliveReplyTimeout = 1000 * 60;
    /**
     * 上传下载数据缓冲区
     */
    private int bufferSize = 1024 * 10;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getDataTimeout() {
        return dataTimeout;
    }

    public void setDataTimeout(int dataTimeout) {
        this.dataTimeout = dataTimeout;
    }

    public int getControlKeepAliveTimeout() {
        return controlKeepAliveTimeout;
    }

    public void setControlKeepAliveTimeout(int controlKeepAliveTimeout) {
        this.controlKeepAliveTimeout = controlKeepAliveTimeout;
    }

    public int getControlKeepAliveReplyTimeout() {
        return controlKeepAliveReplyTimeout;
    }

    public void setControlKeepAliveReplyTimeout(int controlKeepAliveReplyTimeout) {
        this.controlKeepAliveReplyTimeout = controlKeepAliveReplyTimeout;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public int getReconnectTimeout() {
        return reconnectTimeout;
    }

    public void setReconnectTimeout(int reconnectTimeout) {
        this.reconnectTimeout = reconnectTimeout;
    }

}
