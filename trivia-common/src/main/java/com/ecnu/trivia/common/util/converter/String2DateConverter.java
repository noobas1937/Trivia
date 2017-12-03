/** Created by Jack Chen at 2014/6/25 */
package com.ecnu.trivia.common.util.converter;

import com.ecnu.trivia.common.util.DateUtils;
import com.ecnu.trivia.common.util.ObjectUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

/**
 * 由字符串转换为时间
 *
 * @author Jack Chen
 */
public class String2DateConverter implements Converter<String, Date> {
    @Override
    public Date convert(String source) {
        if(ObjectUtils.isNullOrEmpty(source)) {
            return null;
        }

        Date result;

        //进行3次转换 datetime date time

        result = DateUtils.parseDateTime(source, null);
        if(result != null) {
            return result;
        }

        result = DateUtils.parseDate(source, null);
        if(result != null) {
            return result;
        }

        result = DateUtils.parseTime(source, null);
        if(result != null) {
            return result;
        }

        throw new IllegalArgumentException("错误的时间参数:" + source);
    }
}
