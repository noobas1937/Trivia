/** Created by Jack Chen at 9/15/2014 */
package com.ecnu.trivia.common.component.domain;

import com.google.common.collect.Maps;
import com.ecnu.trivia.common.component.exception.Asserts;
import com.ecnu.trivia.common.exception.DataException;
import org.springframework.beans.BeanUtils;

import java.util.Map;

/**
 * 描述通用数据附加结构
 *
 * @author Jack Chen
 */
public class Attributable implements Cloneable {
    /** 描述通用的附加结构，相应的附加信息可以附加至属性中 */
    private Map<String, Object> attr;

    public Map<String, Object> getAttr() {
        return attr;
    }

    /** 批量设置属性,不推荐直接调用,请使用 attr进行单独设置 */
    public void setAttr(Map<String, Object> attr) {
        this.attr = attr;
    }

    /** 单个设置属性,不允许设置与当前对象相同的属性 */
    public <T> void attr(String key, T value) {
        if(attr == null) {
            attr = Maps.newHashMap();
        }

        Asserts.assertTrue(BeanUtils.getPropertyDescriptor(this.getClass(), key) == null, DataException.class, "不允许覆盖原对象应有的属性:{}", key);

        attr.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T attr(String key) {
        return attr == null ? null : (T) attr.get(key);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Attributable t = (Attributable) super.clone();

        //如果相应的数据表有信息,则强行重新组合,避免浅层复制
        if(this.attr != null) {
            t.attr = Maps.newHashMap(attr);
        }

        return t;
    }
}
