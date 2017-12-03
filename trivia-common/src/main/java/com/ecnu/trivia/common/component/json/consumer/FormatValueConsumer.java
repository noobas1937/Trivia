/** Created by Jack Chen at 2015/6/29 */
package com.ecnu.trivia.common.component.json.consumer;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.ecnu.trivia.common.component.function.Consumer;
import com.ecnu.trivia.common.component.json.format.FormatValueFilter;
import com.ecnu.trivia.common.component.json.format.JsonFormat;

/**
 * 实现对日期的处理语义
 *
 * @author Jack Chen
 */
public class FormatValueConsumer implements Consumer<JSONSerializer> {
    private JsonFormat[] jsonFormats;

    public FormatValueConsumer(JsonFormat[] jsonFormats) {
        this.jsonFormats = jsonFormats;
    }

    @Override
    public void accept(JSONSerializer serializer) {
        for(JsonFormat jsonFormat : jsonFormats) {
            serializer.getValueFilters().add(new FormatValueFilter(jsonFormat));
        }
    }
}
