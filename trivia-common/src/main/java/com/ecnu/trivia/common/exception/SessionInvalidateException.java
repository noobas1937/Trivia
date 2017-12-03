/** Created by Jack Chen at 12/15/15 */
package com.ecnu.trivia.common.exception;

/** @author Jack Chen */
public class SessionInvalidateException extends IRCloudException {

    public SessionInvalidateException() {
    }

    public SessionInvalidateException(String message) {
        super(message);
    }

    public SessionInvalidateException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}
