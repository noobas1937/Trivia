/** Created by Jack Chen at 11/13/2014 */
package com.ecnu.trivia.common.component.exception;

import com.ecnu.trivia.common.component.CompositeV2;
import com.ecnu.trivia.common.util.StringUtils;

/**
 * 错误数据信息
 *
 * @author Jack Chen
 */
public class ExceptionMessage extends CompositeV2<String, String> {
    public ExceptionMessage(String first, String second) {
        super(first, second);
    }

    /** 转换为显示消息 */
    public String toMessage(Object... args) {
        String message = second;
        message = StringUtils.format(message, args);

        return message;
    }
}
