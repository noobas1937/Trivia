package com.ecnu.trivia.common.exception;

/** @author Jack Chen */
public class UnSupportException extends IRCloudException {

    public UnSupportException() {
        super("不支持此方法");
    }

    public UnSupportException(String msg){
        super(msg);
    }
}
