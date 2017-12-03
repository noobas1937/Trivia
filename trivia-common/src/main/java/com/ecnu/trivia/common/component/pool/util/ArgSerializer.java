/** Created by Jack Chen at 9/18/2014 */
package com.ecnu.trivia.common.component.pool.util;

import com.ecnu.trivia.common.component.json.JsonUtils;

/** @author Jack Chen */
public class ArgSerializer {

    public static String serialize(Object argObj) {
        return JsonUtils.toJson(argObj);
    }

    public static <T> T deserialize(String value, Class<T> type) {
        return JsonUtils.parse(value, type);
    }
}
