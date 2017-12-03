/** Created by Jack Chen at 15-11-1 */
package com.ecnu.trivia.common.component.metric;

import com.codahale.metrics.MetricRegistry;

/**
 * 用于持有相应的全局注册信息
 *
 * @author Jack Chen
 */
public class MetricsHolder {
    /** 全局引用类，用于持有惟一的引用信息 */
    public static final MetricRegistry METRICS = new MetricRegistry();

    public static final String METRICS_GROUP = "NewBI";

    /** 返回相应的组名 */
    public static String name(String... postName) {
        return MetricRegistry.name(METRICS_GROUP, postName);
    }
}
