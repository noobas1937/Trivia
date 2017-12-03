/** Created by Jack Chen at 2015/6/29 */
package com.ecnu.trivia.common.component.json.format;

import com.alibaba.fastjson.serializer.ValueFilter;
import com.ecnu.trivia.common.util.DateUtils;
import com.ecnu.trivia.common.util.ObjectUtils;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 用于处理日期格式化的特定场景
 *
 * @author Jack Chen
 */
public class FormatValueFilter implements ValueFilter {
    /** 内部格式化信息 */
    private JsonFormat jsonFormat;

    public FormatValueFilter(JsonFormat jsonFormat) {
        this.jsonFormat = jsonFormat;
    }

    @Override
    public Object process(Object object, String name, Object value) {
        if(!(value instanceof Date)) {
            return value;
        }

        if(!jsonFormat.type().isInstance(object)) {
            return value;
        }

        if(!Objects.equals(name, jsonFormat.value())) {
            return value;
        }

        Date valueDateTime = (Date) value;

        Date nowDate = DateUtils.getDateX(new Date()).toDate();
        Date valueDate = DateUtils.getDateX(valueDateTime).toDate();
        int interval = (int) TimeUnit.MILLISECONDS.toDays(nowDate.getTime() - valueDate.getTime());

        StringBuilder builder = new StringBuilder(_getDateStr(valueDate, interval, jsonFormat.dateFormat()));

        if(jsonFormat.time()) {
            String timeStr = ObjectUtils.isNullOrEmpty(jsonFormat.timeFormat()) ? DateUtils.formatTime(valueDateTime) : DateUtils.format(valueDateTime, jsonFormat.timeFormat());
            builder.append(" ").append(timeStr);
        }

        return builder.toString();
    }

    /**
     * 获取相应的日期字符串
     *
     * @param interval 表示距离今天还有多少天
     */
    private String _getDateStr(Date date, int interval, String format) {
        String dateStr;

        switch(interval) {
            case 0: {
                dateStr = "今天";
                break;
            }
            case 1: {
                dateStr = "昨天";
                break;
            }
            case 2: {
                dateStr = "前天";
                break;
            }
            case -1: {
                dateStr = "明天";
                break;
            }
            case -2: {
                dateStr = "后天";
                break;
            }
            default: {
                dateStr = ObjectUtils.isNullOrEmpty(jsonFormat.dateFormat()) ? DateUtils.formatDate(date) : DateUtils.format(date, format);
            }
        }

        return dateStr;
    }
}
