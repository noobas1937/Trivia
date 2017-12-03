package com.ecnu.trivia.common.exception;

/**
 * Created by Jack Chen on 2017/8/30.
 */
public class OlapException extends DataException {

    public OlapException() {
    }

    public OlapException(String message) {
        super(message);
    }

    public OlapException(String message, Throwable cause) {
        super(message, cause);
    }

}
