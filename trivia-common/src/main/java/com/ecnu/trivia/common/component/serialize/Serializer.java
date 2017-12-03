/** Created by Jack Chen at 2014/7/31 */
package com.ecnu.trivia.common.component.serialize;

/** @author Jack Chen */
public interface Serializer<T> {
    /** 序列化为json字节数组 */
    public byte[] serializeBytes(T obj);

    /** 反序列化 */
    public T deserialize(byte[] bytes);
}
