/** Created by Jack Chen at 12/12/2014 */
package com.ecnu.trivia.common.component.json.limit;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

/**
 * 用于描述限制属性信息
 *
 * @author Jack Chen
 */
class LimitPropertyVO {
    final List<StringProperty> sortedPathList;
    final List<StringProperty> sortedDescPathList;
    final List<PatternProperty> sortedPatternList;

    LimitPropertyVO(List<StringProperty> sortedPathList, List<PatternProperty> sortedPatternList) {
        this.sortedPathList = sortedPathList;
        this.sortedDescPathList = Lists.newArrayList(sortedPathList);
        Collections.reverse(this.sortedDescPathList);

        this.sortedPatternList = sortedPatternList;
    }

    public boolean allowPath(String path, int pathLength, Object source, String name, Object value, JSONSerializer jsonSerializer) {
        List<StringProperty> stringPropertyList = sortedDescPathList;
        List<PatternProperty> patternPropertyList = sortedPatternList;

        //先使用路径判断,如果匹配,则OK
        for(StringProperty sp : stringPropertyList) {
            if(sp.getPathLength() >= pathLength) {
                if(sp.allowPath(path, pathLength, source, name, value, jsonSerializer)) {
                    return true;
                }
            } else {//优化性判断,即当长度小于时,已不再需要判断
                break;
            }
        }

        //使用正则匹配
        for(PatternProperty pp : patternPropertyList) {
            if(pp.getPathLength() > pathLength) {//优化性判断,当长度大于时,也不需要判断
                break;
            } else if(pp.allowPath(path, pathLength, source, name, value, jsonSerializer)) {
                return true;
            }
        }
        return false;
    }
}
