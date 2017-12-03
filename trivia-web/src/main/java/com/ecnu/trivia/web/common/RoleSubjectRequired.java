/** Created by Jack Chen at 15-8-30 */
package com.ecnu.trivia.web.common;

import com.ecnu.trivia.common.component.domain.Domain;
import com.ecnu.trivia.web.common.interceptor.permission.MethodParam;

/**
 * 用于描述角色主体获取的接口
 *
 * @author Jack Chen
 */
public interface RoleSubjectRequired<T extends Domain<T>> {

    /** 获取相应的主体 */
    T p4Subject(MethodParam methodParam);
}
