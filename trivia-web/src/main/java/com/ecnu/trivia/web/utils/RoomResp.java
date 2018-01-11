package com.ecnu.trivia.web.utils;

/**
 * @Title: Resp.java
 * @Package com.iresearch.core.utils
 * @Description: TODO
 * Copyright: Copyright (c) 2016
 * Company:艾瑞咨询
 * @author Iresearch-billzhuang
 * @date 2016年3月24日 上午9:35:49
 * @version V1.0.0
 */

import com.ecnu.trivia.common.component.web.HttpRespCode;

import java.io.Serializable;
import java.util.Date;

import com.ecnu.trivia.common.component.web.HttpRespCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: Resp
 * @Description: TODO
 * @author Iresearch-billzhuang
 * @date 2016年3月24日 上午9:35:49
 *
 */
public class RoomResp implements Serializable {
    private static final long serialVersionUID = -8660197629749596025L;

    protected String resCode;
    protected String resMsg;
    protected Integer count;

    /**
     * @return the resTime
     */
    public Long getResTime() {
        if (resTime == null) {
            return 0L;
        }
        return resTime.getTime();
    }

    /**
     * @param resTime the resTime to set
     */
    public void setResTime(Date resTime) {
        this.resTime = resTime;
    }

    protected Date resTime;
    protected Object data;

    public RoomResp() {

    }


    public RoomResp(HttpRespCode resCode) {
        this.resCode = resCode.getCode();
        this.resMsg = resCode.getText();
        this.resTime = new Date();
    }

    public RoomResp(HttpRespCode resCode, Object data) {
        this.resCode = resCode.getCode();
        this.resMsg = resCode.getText();
        this.resTime = new Date();
        this.data = data;
    }

    public RoomResp(HttpRespCode resCode, Object data,Integer count) {
        this.resCode = resCode.getCode();
        this.resMsg = resCode.getText();
        this.resTime = new Date();
        this.data = data;
        this.count = count;
    }


    public RoomResp(String resCode, String resMsg) {
        this.resCode = resCode;
        this.resMsg = resMsg;

    }

    public RoomResp(String resCode, String resMsg, Date resTime) {
        this.resCode = resCode;
        this.resMsg = resMsg;
        this.resTime = resTime;

    }

    public RoomResp(String resCode, String resMsg, Date resTime, Object data) {
        this.resCode = resCode;
        this.resMsg = resMsg;
        this.resTime = resTime;
        this.data = data;
    }

    public RoomResp(String resCode, String resMsg, Object data) {
        this.resCode = resCode;
        this.resMsg = resMsg;
        this.data = data;
    }

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Integer getCount() { return count; }

    public void setCount(Integer count) { this.count = count; }
}
