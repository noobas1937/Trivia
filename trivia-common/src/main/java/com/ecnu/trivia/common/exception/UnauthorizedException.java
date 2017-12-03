package com.ecnu.trivia.common.exception;

/**
 * 描述没有被授权的异常信息
 *
 * @author Jack Chen
 */
@ExceptionStatus(ExceptionStatusValue.UNAUTHORIZED)
public class UnauthorizedException extends IRCloudException {
    public UnauthorizedException() {
    }

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}
