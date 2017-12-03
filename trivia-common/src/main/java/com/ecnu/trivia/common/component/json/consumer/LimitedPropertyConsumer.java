/** Created by Jack Chen at 2015/6/29 */
package com.ecnu.trivia.common.component.json.consumer;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.ecnu.trivia.common.component.function.Consumer;
import com.ecnu.trivia.common.component.json.limit.LimitedPropertyFilter;
import com.ecnu.trivia.common.util.ObjectUtils;

import java.util.Arrays;

/**
 * 实现对jsonSerializer的consume语义
 *
 * @author Jack Chen
 */
public class LimitedPropertyConsumer implements Consumer<JSONSerializer> {
    private String[] limitedProperties;

    public LimitedPropertyConsumer(String[] limitedProperties) {
        this.limitedProperties = limitedProperties;
    }

    @Override
    public void accept(JSONSerializer serializer) {
        //启用属性限制
        if(!ObjectUtils.isNullOrEmpty(limitedProperties)) {
            serializer.getPropertyFilters().add(new LimitedPropertyFilter(Arrays.asList(limitedProperties), serializer));
        }
    }
}
