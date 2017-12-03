/** Created by Jack Chen at 12/12/2014 */
package com.ecnu.trivia.common.component.json;

import com.alibaba.fastjson.serializer.AfterFilter;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerialContext;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.ecnu.trivia.common.component.domain.Domain;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 用于在输出domain正常属性后强制输出相应的attr属性,并且将此属性视为与当前对象属性相同的级别
 * 正常的输出{f1:xx,attr:{attrF1:yy}}
 * 转换之后的输出:{f1:xx,attrF1:yy}
 * <p/>
 * 有一个限制,即attr中的属性不能与当前对象冲突.
 * 此限制的保证由
 * Attributable.attr方法保证
 * <p/>
 * * @author Jack Chen
 */
public class AttrAfterFilter extends AfterFilter {
    private static final ThreadLocal<JSONSerializer> parentSerializerLocal;
    private static final ThreadLocal<Character> parentSeperatorLocal;
    private final static Character COMMA = ',';

    static {
        try{
            Field field = AfterFilter.class.getDeclaredField("serializerLocal");
            field.setAccessible(true);
            parentSerializerLocal = (ThreadLocal<JSONSerializer>) field.get(null);

            field = AfterFilter.class.getDeclaredField("seperatorLocal");
            field.setAccessible(true);
            parentSeperatorLocal = (ThreadLocal<Character>) field.get(null);
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void writeAfter(Object object) {
        if(object instanceof Domain) {
            Domain self = (Domain) object;
            Map<String, Object> map = self.getAttr();
            if(map != null) {
                //以下使用取自fastjson 的serializerWriter来实现 serializer.writeKeyValue
                //不再每个for循环中获取serializer，而避免在下个attribute对象写时出现NPE异常
                //原因为，针对每次attr对象时，父类的实现为在afterWrite时会设置local的write为null，导致循环中的下个对象会出现npe

                JSONSerializer serializer = parentSerializerLocal.get();
                SerializeWriter writer = serializer.getWriter();

                SerialContext context = serializer.getContext();

                try{
                    for(Map.Entry<String, Object> e : map.entrySet()) {
                        char separator = parentSeperatorLocal.get();
                        writer.write(separator);
                        writer.writeFieldName(e.getKey());
                        serializer.writeWithFieldName(e.getValue(), e.getKey());

                        if(separator != ',') {
                            parentSeperatorLocal.set(COMMA);
                        }
                    }
                } finally {
                    serializer.setContext(context);
                }
            }
        }
    }
}
