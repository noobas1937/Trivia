/** Created by Jack Chen at 11/18/2014 */
package com.ecnu.trivia.common.component.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.*;
import com.google.common.collect.Lists;
import com.ecnu.trivia.common.component.function.Consumer;
import com.ecnu.trivia.common.component.function.Consumers;
import com.ecnu.trivia.common.component.json.consumer.FormatValueConsumer;
import com.ecnu.trivia.common.component.json.consumer.LimitedPropertyConsumer;
import com.ecnu.trivia.common.component.json.format.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.List;

/**
 * json工具，用于格式化对象为json
 *
 * @author Jack Chen
 */
public class JsonUtils {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    private static final SerializeConfig jsonConfig = new SerializeConfig();
    private static final ParserConfig parseConfig = ParserConfig.getGlobalInstance();
    private static final SerializerFeature[] browserJsonFeature;
    private static SerializerFeature[] jsonFeatures;
    private static SerializerFeature[] jsonFeaturesNotWriteClassName;

    private static final AfterFilter afterFilter = new AttrAfterFilter();
    private static final PropertyPreFilter propertyPreFilter = new AttrPropertyPreFilter();

    static {
        List<SerializerFeature> serializerFeatureList = Lists.newArrayList();
        serializerFeatureList.add(SerializerFeature.SkipTransientField);
        serializerFeatureList.add(SerializerFeature.WriteDateUseDateFormat);
        serializerFeatureList.add(SerializerFeature.WriteNullListAsEmpty);
        serializerFeatureList.add(SerializerFeature.QuoteFieldNames);
        serializerFeatureList.add(SerializerFeature.DisableCircularReferenceDetect);
        serializerFeatureList.add(SerializerFeature.BrowserCompatible);
        serializerFeatureList.add(SerializerFeature.WriteEnumUsingToString);
        serializerFeatureList.add(SerializerFeature.BrowserSecure);
        browserJsonFeature = serializerFeatureList.toArray(new SerializerFeature[serializerFeatureList.size()]);
    }

    static {
        List<SerializerFeature> serializerFeatureList = Lists.newArrayList();
        serializerFeatureList.add(SerializerFeature.WriteClassName);
        serializerFeatureList.add(SerializerFeature.SkipTransientField);
        serializerFeatureList.add(SerializerFeature.WriteDateUseDateFormat);

        jsonFeatures = serializerFeatureList.toArray(new SerializerFeature[serializerFeatureList.size()]);

        serializerFeatureList = Lists.newArrayList();
        serializerFeatureList.add(SerializerFeature.SkipTransientField);
        serializerFeatureList.add(SerializerFeature.WriteDateUseDateFormat);
        jsonFeaturesNotWriteClassName = serializerFeatureList.toArray(new SerializerFeature[serializerFeatureList.size()]);
    }

    static {
        jsonConfig.setAsmEnable(false);//由于当前版本的asm生成有一定问题,因此不再使用asm生成(在1.1.43以后的版本,asmFactory已被重构)
        parseConfig.setAsmEnable(false);//与上同理
    }

    private static <T> String _toBrowserJson(T t, Consumer<JSONSerializer> consumer) {
        try(SerializeWriter out = new SerializeWriter(browserJsonFeature)){
            JSONSerializer serializer = new JSONContextableSerializer(out, jsonConfig);

            //启用attr属性处理
            serializer.getAfterFilters().add(afterFilter);
            serializer.getPropertyPreFilters().add(propertyPreFilter);

            consumer.accept(serializer);

            serializer.write(t);

            return out.toString();
        }
    }

    /**
     * 将对象输出为浏览器使用的json字符串,以用于进行浏览器显示和处理
     *
     * @param limitProperties 限制的属性集,如果存在,则只会限制内的属性处理
     */
    public static <T> String toBrowserJson(T t, final String... limitProperties) {
        return _toBrowserJson(t, new LimitedPropertyConsumer(limitProperties));
    }

    /**
     * 将对象输出为浏览器使用的json字符串,以用于进行浏览器显示和处理
     *
     * @param jsonFormats     对于特定的日期数据，期望的输出信息
     * @param limitProperties 限制的属性集,如果存在,则只会限制内的属性处理
     */
    public static <T> String toBrowserJson(T t, JsonFormat[] jsonFormats, String... limitProperties) {
        return _toBrowserJson(t, Consumers.andThen(new LimitedPropertyConsumer(limitProperties), new FormatValueConsumer(jsonFormats)));
    }

    /** 将对象输出为可反序列化的json字符串，以用于数据存储和传输 */
    public static <T> String toJson(T t) {
        return toJson(t, true);
    }

    /**
     * 将对象输出为可反序列化的json字符串，以用于数据存储和传输
     *
     * @param writeClassName 是否输出类名
     */
    public static <T> String toJson(T t, boolean writeClassName) {
        SerializerFeature[] features = writeClassName ? jsonFeatures : jsonFeaturesNotWriteClassName;

        return JSON.toJSONString(t, jsonConfig, features);
    }

    public static void addSerializer(Type type, ObjectSerializer serializer) {
        jsonConfig.put(type, serializer);
    }

    public static void addDerializer(Type type, ObjectDeserializer deserializer) {
        parseConfig.putDeserializer(type, deserializer);
    }

    public static JSONObject parseObject(String json){
        return JSON.parseObject(json);
    }

    /** 将json字符串转换为指定类型的对象 */
    public static <T> T parse(String json, Class<T> clazz) {
        try{
            return JSON.parseObject(json, clazz);
        } catch(Exception e) {
            throw new JsonParseException(e.getMessage(), e);
        }
    }

    /** 将json字符串转换为指定类型的对象，如果不能转换，则返回指定的默认值 */
    public static <T> T parse(String json, Class<T> clazz, T defaultValue) {
        try{
            return JSON.parseObject(json, clazz);
        } catch(RuntimeException e) {
            logger.error("json parse exception:"+e.getMessage(), e);
            return defaultValue;
        }
    }

    /**
     * 将json字符串转换为指定的对象
     * 如果对象是按照指定的fastjson组织的，则转换为@type指定的对象，否则转换为JsonObject类型
     */
    @SuppressWarnings("unchecked")
    public static <T> T parse(String json) {
        try{
            return (T) JSON.parse(json);
        } catch(Exception e) {
            throw new JsonParseException(e.getMessage(), e);
        }
    }

    /** 将json字符串转换为指定类型的list集合 */
    public static <T> List<T> parse2List(String json, Class<T> clazz) {
        try{
            return JSON.parseArray(json, clazz);
        } catch(Exception e) {
            throw new JsonParseException(e.getMessage(), e);
        }
    }
}
