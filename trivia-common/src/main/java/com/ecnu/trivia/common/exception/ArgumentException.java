package com.ecnu.trivia.common.exception;

/**
 * 描述参数级错误
 *
 * @author Jack Chen
 */
@ExceptionStatus(ExceptionStatusValue.BAD_REQUEST)
public class ArgumentException extends IRCloudException {
    private static final String prefix = "参数错误:";

    public ArgumentException() {
        super(prefix);
    }

    public ArgumentException(String message) {
        super(prefix + message);
    }

    public ArgumentException(String message, Throwable cause) {
        super(prefix + message, cause);
    }
}
