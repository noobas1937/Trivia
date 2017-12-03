package com.ecnu.trivia.common.exception;

/**
 * 描述业务内部异常
 *
 * @author Jack Chen
 */
@ExceptionStatus(ExceptionStatusValue.INTERNAL_SERVER_ERROR)
public class BusinessException extends IRCloudException {
    public BusinessException() {
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
