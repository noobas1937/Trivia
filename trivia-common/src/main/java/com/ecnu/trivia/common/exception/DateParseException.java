/** Created by Jack Chen at 11/13/2014 */
package com.ecnu.trivia.common.exception;

/**
 * 日期解析异常
 *
 * @author Jack Chen
 */
public class DateParseException extends ArgumentException {
    public DateParseException() {
    }

    public DateParseException(String message) {
        super(message);
    }

    public DateParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
