/** Created by Jack Chen at 2015/7/16 */
package com.ecnu.trivia.common.util.converter;

import org.springframework.core.convert.converter.Converter;

/**
 * 修正默认实现，以支持从浮点数进行转换
 *
 * @author Jack Chen
 */
public class String2LongConverter implements Converter<String, Long> {

    @Override
    public Long convert(String source) {
        try{
            return Long.valueOf(source);
        } catch(NumberFormatException e) {
            return Double.valueOf(source).longValue();
        }
    }
}
