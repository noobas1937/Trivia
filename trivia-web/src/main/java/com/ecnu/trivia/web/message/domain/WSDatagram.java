package com.ecnu.trivia.web.message.domain;

import com.ecnu.trivia.web.rbac.domain.User;

import java.util.Date;

public class WSDatagram {
    Integer type;
    String content;
    User user;
    Date gmtCreated;

    public WSDatagram() {
        this.gmtCreated = new Date();
    }

    /**
     * 用户进退出房间数据包
     * @param type
     */
    public WSDatagram(Integer type) {
        this.type = type;
        this.gmtCreated = new Date();
    }

    /**
     * 聊天数据包构造函数
     * @param type
     * @param content
     * @param user
     */
    public WSDatagram(Integer type, String content, User user) {
        this.type = type;
        this.content = content;
        this.user = user;
        this.gmtCreated = new Date();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }
}
