/** Created by Jack Chen at 12/10/2014 */
package com.ecnu.trivia.common.exception;

/**
 * 描述内部的数据异常,此异常通常是由于内部的数据不一致产生,属于程序内部出错
 *
 * @author Jack Chen
 */
@ExceptionStatus(ExceptionStatusValue.INTERNAL_SERVER_ERROR)
public class DataException extends IRCloudException {
    private static String postfix = " 请联系客服人员或开发人员!";

    public DataException() {
        super(postfix);
    }

    public DataException(String message) {
        super(message + postfix);
    }

    public DataException(String message, Throwable cause) {
        super(message + postfix, cause);
    }
}
