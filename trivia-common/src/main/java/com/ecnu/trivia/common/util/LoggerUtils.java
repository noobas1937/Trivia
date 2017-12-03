/** Created by Jack Chen at 2014/8/11 */
package com.ecnu.trivia.common.util;

import com.google.common.base.Function;
import org.slf4j.Logger;

/**
 * 提供对日志的工具处理
 *
 * @author Jack Chen
 */
public class LoggerUtils {
    public static  <T> T debugTime(Function<Void, T> function, Logger logger, String formatMessage, Object[] param) {

        if(logger.isDebugEnabled()) {
            long start = System.currentTimeMillis();
            T t = function.apply(null);
            long end = System.currentTimeMillis();

            //空对象处理
            if(param == null || param.length == 0) {
                param = new Object[1];
            }

            //可能存在的扩容
            if(param[param.length - 1] != null) {
                Object[] newParam = new Object[param.length + 1];
                System.arraycopy(param, 0, newParam, 0, param.length);
                param = newParam;
            }

            param[param.length - 1] = (end - start);

            logger.debug(formatMessage, param);

            return t;
        }

        return function.apply(null);
    }
}
