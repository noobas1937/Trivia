/** Created by wei.wang at 2015/4/23 */
package com.ecnu.trivia.common.exception;

/**
 * 抽取自定义异常
 * @author wei.wang
 * */
public class ExtractException extends Exception {

    public ExtractException() {
        super();
    }

    public ExtractException(String message) {
        super(message);
    }

    public ExtractException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExtractException(Throwable cause) {
        super(cause);
    }
}
