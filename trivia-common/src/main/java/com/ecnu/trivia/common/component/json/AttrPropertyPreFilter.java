/** Created by Jack Chen at 12/12/2014 */
package com.ecnu.trivia.common.component.json;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.ecnu.trivia.common.component.domain.Domain;

/**
 * 针对在浏览器处理时不再处理attr属性
 *
 * @author Jack Chen
 */
public class AttrPropertyPreFilter implements PropertyPreFilter {
    private static final String propertyName = "attr";

    @Override
    public boolean apply(JSONSerializer serializer, Object object, String name) {
        //如果为domain,此当前存在attr属性,则忽略
        if(object instanceof Domain && propertyName.equals(name)) {
            return false;
        }

        return true;
    }
}
