
package com.ecnu.trivia.common.util.converter;

import org.springframework.core.convert.converter.Converter;

import java.util.Objects;

public class Boolean2LongConverter implements Converter<Boolean, Long> {

    @Override
    public Long convert(Boolean source) {
        if(Objects.equals(source, true)) {
            return 1L;
        } else {
            return 0L;
        }
    }
}
