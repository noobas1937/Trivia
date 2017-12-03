package com.ecnu.trivia.common.exception;

/**
 * 描述任务功能是不可恢复的异常。
 *
 * @author Jack Chen
 */
@ExceptionStatus(ExceptionStatusValue.INTERNAL_SERVER_ERROR)
public class UnableRecoveryException extends IRCloudException {
    public UnableRecoveryException() {
    }

    public UnableRecoveryException(String message) {
        super(message);
    }

    public UnableRecoveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
