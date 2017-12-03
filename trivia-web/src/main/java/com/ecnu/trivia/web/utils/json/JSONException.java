/**
 * @Title: JSONException.java
 * @Package com.iresearch.core.json
 * @Description: TODO
 * Copyright: Copyright (c) 2016
 * Company:艾瑞咨询
 * @author Iresearch-billzhuang
 * @date 2016年3月22日 上午10:21:17
 * @version V1.0.0
 */
package com.ecnu.trivia.web.utils.json;

/**
 * @ClassName: JSONException
 * @Description: TODO
 * @author Iresearch-billzhuang
 * @date 2016年3月22日 上午10:21:17
 *
 */
public class JSONException extends Exception {
    private static final long serialVersionUID = 0;
    private Throwable cause;

    /**
     * Constructs a JSONException with an explanatory message.
     * @param message Detail about the reason for the exception.
     */
    public JSONException(String message) {
        super(message);
    }

    public JSONException(Throwable cause) {
        super(cause.getMessage());
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
