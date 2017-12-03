/** Created by Jack Chen at 11/13/2014 */
package com.ecnu.trivia.common.exception;

/**
 * 描述错误的授权信息，即不能进行访问的异常
 *
 * @author Jack Chen
 */
@ExceptionStatus(ExceptionStatusValue.FORBIDDEN)
public class ForbiddenException extends IRCloudException {
    public ForbiddenException() {
    }

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
