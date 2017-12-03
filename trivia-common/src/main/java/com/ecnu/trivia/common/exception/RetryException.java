/** Created by Jack Chen at 12/9/2014 */
package com.ecnu.trivia.common.exception;

/**
 * 描述业务内部异常
 *
 * @author Jack Chen
 */
@ExceptionStatus(ExceptionStatusValue.INTERNAL_SERVER_ERROR)
public class RetryException extends IRCloudException {
    public RetryException() {
    }

    public RetryException(String message) {
        super(message);
    }

    public RetryException(String message, Throwable cause) {
        super(message, cause);
    }
}
