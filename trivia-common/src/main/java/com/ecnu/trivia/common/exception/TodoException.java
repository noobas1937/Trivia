/** Created by Jack Chen at 2015/6/18 */
package com.ecnu.trivia.common.exception;

/**
 * 描述还未完成的任务的异常信息
 * 用于替换当前未完成但之后会进行的todo，避免在当前ide中查看到相应的事项
 *
 * @author Jack Chen
 */
public class TodoException extends IRCloudException {
    public TodoException() {
        super("暂未实现");
    }

    public TodoException(String message) {
        super(message);
    }

    public TodoException(String message, Throwable cause) {
        super(message, cause);
    }
}
