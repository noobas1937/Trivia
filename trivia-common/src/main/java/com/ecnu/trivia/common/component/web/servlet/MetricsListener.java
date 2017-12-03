/** Created by Jack Chen at 15-11-1 */
package com.ecnu.trivia.common.component.web.servlet;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlet.InstrumentedFilterContextListener;
import com.ecnu.trivia.common.component.metric.MetricsHolder;

/**
 * 用于实现基于servlet的度量监听
 *
 * @author Jack Chen
 */
public class MetricsListener extends InstrumentedFilterContextListener {
    @Override
    protected MetricRegistry getMetricRegistry() {
        return MetricsHolder.METRICS;
    }
}
