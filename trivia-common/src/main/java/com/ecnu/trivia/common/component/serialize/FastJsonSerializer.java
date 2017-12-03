package com.ecnu.trivia.common.component.serialize;

import com.google.common.base.Charsets;
import com.ecnu.trivia.common.component.json.JsonUtils;

/** 使用fastjson作为序列化器 */
public class FastJsonSerializer implements Serializer {
    private static final byte[] EMPTY = new byte[0];

    /** 序列化为json字节数组 */
    @Override
    public byte[] serializeBytes(Object o) {
        if(o == null) {
            return EMPTY;
        }

        String result = JsonUtils.toJson(o);
        return result.getBytes(Charsets.UTF_8);
    }

    @Override
    public Object deserialize(byte[] bytes) {
        if(bytes == null || bytes.length == 0) {
            return null;
        }

        return JsonUtils.parse(new String(bytes, Charsets.UTF_8));
    }
}
