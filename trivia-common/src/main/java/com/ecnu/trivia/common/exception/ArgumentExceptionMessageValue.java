package com.ecnu.trivia.common.exception;

import com.ecnu.trivia.common.component.exception.ExceptionMessage;

/**
 * 描述参数级错误信息
 *
 * @author Jack Chen
 */
public interface ArgumentExceptionMessageValue {
    ExceptionMessage ACCESS_DENIED_TEST = new ExceptionMessage("access.denied.test", "仅在测试场景下进行访问");

    ExceptionMessage ARGUMENT_ERROR = new ExceptionMessage("argument.error", "参数错误");
}
