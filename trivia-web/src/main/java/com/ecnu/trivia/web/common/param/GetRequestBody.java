/** Created by Jack Chen at 5/25/2015 */
package com.ecnu.trivia.web.common.param;

import java.lang.annotation.*;

/**
 * 用于描述使用GET方法在传递参数时模拟的requestbody信息
 *
 * @author Jack Chen
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GetRequestBody {

    /** 相应的参数key,默认为body,即当传递body=abc时，获取的body值即为abc */
    String value() default "body";
}
