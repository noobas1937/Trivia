/** Created by Jack Chen at 3/16/2015 */
package com.ecnu.trivia.common.component.test.testng;

import com.ecnu.trivia.common.component.domain.Domain;
import com.ecnu.trivia.common.component.mapper.Mapper;

import java.util.List;

/**
 * 基础测试类
 *
 * @author Jack Chen
 */
public class BaseTest extends SpringContextTests {
    /** 保存单个对象 */
    protected <T extends Domain> void save(T t, Mapper<T> mapper) {
        mapper.save(t);
    }

    /** 批量保存对象 */
    protected <T extends Domain> void save(List<T> tList, Mapper<T> mapper) {
        for(T t : tList) {
            mapper.save(t);
        }
    }
}
