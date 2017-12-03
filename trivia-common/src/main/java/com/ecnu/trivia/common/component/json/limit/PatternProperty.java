/** Created by Jack Chen at 12/12/2014 */
package com.ecnu.trivia.common.component.json.limit;

import com.alibaba.fastjson.serializer.*;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.ecnu.trivia.common.component.json.asm.AsmSerializer;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 描述基于正则表达式的属性表达式
 *
 * @author Jack Chen
 */
class PatternProperty implements LimitProperty<PatternProperty> {
    private static Pattern convertStartPattern = Pattern.compile("\\*");
    private static Pattern convertDotPattern = Pattern.compile("\\.");
    private static List<Class<? extends ObjectSerializer>> allowObjectClassList = Lists.newArrayList();

    /** 原始的匹配字符串 */
    private final String sourcePatternStr;
    /** 处理后的正则式 */
    private final Pattern pattern;
    /** 全匹配标记,即当前正则式匹配最终的值,如果不是最终匹配,则要求当前值必须能够往下匹配(特定的类型) */
    private final boolean fullFlag;
    private final int pathLength;

    static {
        allowObjectClassList.add(MapSerializer.class);//map
        allowObjectClassList.add(ListSerializer.class);//list
        allowObjectClassList.add(CollectionCodec.class);//collection
        allowObjectClassList.add(ArraySerializer.class);//array
        allowObjectClassList.add(JavaBeanSerializer.class);//object
        allowObjectClassList.add(AsmSerializer.class);//asm
    }

    public PatternProperty(String sourcePatternStr, boolean fullFlag) {
        this.sourcePatternStr = sourcePatternStr;
        this.pathLength = LimitedPropertyFilter.countDot(sourcePatternStr);
        String s = sourcePatternStr;
        s = convertStartPattern.matcher(s).replaceAll("[_a-zA-Z0-9\\.]+");//允许*占位a.b.c中的a.b
        s = convertDotPattern.matcher(s).replaceAll("\\\\.");
        this.pattern = Pattern.compile("^" + s + "$");
        this.fullFlag = fullFlag;
    }

    @Override
    public int compareTo(@Nonnull PatternProperty o) {
        return Ints.compare(this.pathLength, o.pathLength);
    }

    @Override
    public boolean allowPath(String path, int sPathLength, Object source, String name, Object resultValue, JSONSerializer jsonSerializer) {
        boolean match = pathLength <= sPathLength && pattern.matcher(path).matches();
        if(fullFlag) {
            return match;
        }

        if(resultValue == null) {
            return false;
        }

        ObjectSerializer objectSerializer = jsonSerializer.getObjectWriter(resultValue.getClass());
        for(Class<? extends ObjectSerializer> clazz : allowObjectClassList) {
            if(clazz.isInstance(objectSerializer)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int getPathLength() {
        return pathLength;
    }

    @Override
    public String toString() {
        return "PatternProperty{" +
                "sourcePatternStr='" + sourcePatternStr + '\'' +
                ", pattern=" + pattern +
                ", pathLength=" + pathLength +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        PatternProperty that = (PatternProperty) o;

        if(fullFlag != that.fullFlag) {
            return false;
        }
        return !(!sourcePatternStr.equals(that.sourcePatternStr));

    }

    @Override
    public int hashCode() {
        int result = sourcePatternStr.hashCode();
        result = 31 * result + (fullFlag ? 1 : 0);
        return result;
    }
}
