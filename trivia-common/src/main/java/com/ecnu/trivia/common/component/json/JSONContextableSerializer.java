package com.ecnu.trivia.common.component.json;

import com.alibaba.fastjson.serializer.SerialContext;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;

import java.util.IdentityHashMap;

/**
 * 用于解决原fastjson不能正确处理上下文和循环引用之间的关系
 *
 * @author Jack Chen
 */
public class JSONContextableSerializer extends com.alibaba.fastjson.serializer.JSONSerializer {

    public JSONContextableSerializer(SerializeWriter out, SerializeConfig config) {
        super(out, config);
    }

    @Override
    public void setContext(SerialContext parent, Object object, Object fieldName, int features, int fieldFeatures) {
        SerialContext context = new SerialContext(parent, object, fieldName, features,fieldFeatures);
        if(references == null) {
            references = new IdentityHashMap<Object, SerialContext>();
        }
        this.references.put(object, context);
        setContext(context);
    }
}
