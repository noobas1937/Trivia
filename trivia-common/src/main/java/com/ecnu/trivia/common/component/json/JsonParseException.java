/** Created by Jack Chen at 12/9/2014 */
package com.ecnu.trivia.common.component.json;

import com.ecnu.trivia.common.exception.ArgumentException;

/**
 * json解析异常
 *
 * @author Jack Chen
 */
public class JsonParseException extends ArgumentException {
    public JsonParseException() {
    }

    public JsonParseException(String message) {
        super(message);
    }

    public JsonParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
