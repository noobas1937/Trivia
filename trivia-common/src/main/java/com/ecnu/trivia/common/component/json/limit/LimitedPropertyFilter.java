/** Created by Jack Chen at 12/12/2014 */
package com.ecnu.trivia.common.component.json.limit;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerialContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ecnu.trivia.common.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 用于限制属性的输出
 *
 * @author Jack Chen
 */
public class LimitedPropertyFilter implements PropertyFilter {
    /** 用于缓存的匹配项 */
    private static Map<List<String>, LimitPropertyVO> cacheMap = Maps.newHashMap();
    /** 有效的属性限制格式,即以字母数字开头,中间可接 .abc 或 .* 或 *的格式 */
    private static Pattern validPropertyPattern = Pattern.compile("^[_a-zA-Z0-9\\*]+(?:\\.(?:[_a-zA-Z0-9]+|\\*))*$");
    /** 当前对象的匹配属性 */
    private LimitPropertyVO limitProperty;
    private JSONSerializer jsonSerializer;

    public LimitedPropertyFilter(List<String> limitProperties, JSONSerializer jsonSerializer) {
        this.limitProperty = makeLimitProperty(limitProperties);
        this.jsonSerializer = jsonSerializer;
    }

    private LimitPropertyVO makeLimitProperty(List<String> limitProperties) {
        LimitPropertyVO limitProperty = cacheMap.get(limitProperties);
        if(limitProperty != null) {
            return limitProperty;
        }

        Set<StringProperty> stringPropertySet = Sets.newHashSet();
        Set<PatternProperty> patternPropertySet = Sets.newHashSet();

        for(String s : limitProperties) {
            if(!validPropertyPattern.matcher(s).matches()) {
                throw new RuntimeException("错误的路径字符串：" + s);
            }

            int firstIndex = s.indexOf('*');
            if(firstIndex != -1) {
                //处理*之前的.路径，将其加入stringProperty以便之前能够处理前面的数据,即如果存在a.b.* 则需要将a,a.b都加入到stringProperty中
                int firstIndexDot = s.lastIndexOf('.', firstIndex);
                if(firstIndexDot != -1) {
                    String firstString = s.substring(0, firstIndexDot);//返回a.b
                    for(int i = firstString.indexOf('.');i != -1;) {
                        String temp = firstString.substring(0, i);
                        stringPropertySet.add(new StringProperty(temp));
                        i = firstString.indexOf('.', i + 1);
                    }
                    stringPropertySet.add(new StringProperty(firstString));
                }

                //从第1个*开始，依次根据.往后推
                for(int i = s.indexOf('.', firstIndex);i != -1;) {
                    String temp = s.substring(0, i);
                    patternPropertySet.add(new PatternProperty(temp, false));
                    i = s.indexOf('.', i + 1);
                }
                patternPropertySet.add(new PatternProperty(s, true));
            } else {
                stringPropertySet.add(new StringProperty(s));
            }
        }

        //排序,以尽快的处理,如果前面的不能匹配,则后面的自然不能匹配
        List<StringProperty> stringPropertyList = Lists.newArrayList(stringPropertySet);
        List<PatternProperty> patternPropertyList = Lists.newArrayList(patternPropertySet);
        Collections.sort(stringPropertyList);
        Collections.sort(patternPropertyList);

        limitProperty = new LimitPropertyVO(stringPropertyList, patternPropertyList);
        cacheMap.put(limitProperties, limitProperty);
        return limitProperty;
    }

    @Override
    public boolean apply(Object object, String name, Object value) {
        SerialContext nowContext = new SerialContext(jsonSerializer.getContext(), object, name, 0, 0);
        String nowPath = getLinkedPath(nowContext);
        int pathLength = countDot(nowPath);

        return limitProperty.allowPath(nowPath, pathLength, object, name, value, jsonSerializer);
    }

//    public boolean apply(JSONSerializer serializer, Object source, String name) {
//        SerialContext nowContext = new SerialContext(serializer.getContext(), source, name, 0);
//        String nowPath = getLinkedPath(nowContext);
//        return allowPath(nowPath);
//    }

    /** 输出结果 类似a.b.c.d等格式，忽略[] */
    private static String getLinkedPath(SerialContext serialContext) {
        //这里有点bad smell，即要考虑parent为null,又要考虑fieldName为null，且对collection判断只能从fieldName，而不能从object入手
        boolean isCollection = serialContext.fieldName instanceof Integer;
        boolean isFieldNameNull = serialContext.fieldName == null;
        if(serialContext.parent == null) {
            return isCollection || isFieldNameNull ? "" : String.valueOf(serialContext.fieldName);
        }

        String parentLinkedPath = getLinkedPath(serialContext.parent);
        if(isCollection || isFieldNameNull) {
            return parentLinkedPath;
        }

        return parentLinkedPath.length() == 0 ? String.valueOf(serialContext.fieldName) : parentLinkedPath + "." + serialContext.fieldName;
    }

    static int countDot(String s) {
        if(s == null) {
            return 0;
        }

        return StringUtils.count(s, '.');
    }
}
