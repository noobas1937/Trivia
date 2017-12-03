/** Created by Jack Chen at 2014/6/24 */
package com.ecnu.trivia.common.util;

import com.ecnu.trivia.common.component.ClassFinder;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.List;

/**
 * 数据转换工具类，用于将指定的类转换为另一个类
 * 使用spring conversionService进行基础转换工作
 *
 * @author Jack Chen
 */
public class ConvertUtils {
    private static DefaultConversionService conversionService = new DefaultConversionService();

    static {
        @SuppressWarnings("unchecked")
        List<Class<? extends Converter>> converterClassList = ClassFinder.getInstance().findSubClass(Converter.class);
        List<Converter> converterList = ClassUtils.newInstance(converterClassList);
        for(Converter converter : converterList) {
            addConverter(converter);
        }
    }

    /** 是否能进行相应类型的转换 */
    public static boolean canConvert(Class<?> sourceType, Class<?> destType) {
        return conversionService.canConvert(sourceType, destType);
    }

    /** 将指定源数据转换为目标类型数据 */
    public static <D, S> D convert(S s, Class<D> destType) {
        return conversionService.convert(s, destType);
    }

    /** 添加转换器 */
    public static <D,S> void addConverter(Converter<S,D> converter) {
        conversionService.addConverter(converter);
    }
}
