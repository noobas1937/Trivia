/** Created by Jack Chen at 15-11-16 */
package com.ecnu.trivia.common.component.metric;

import com.codahale.metrics.jvm.CachedThreadStatesGaugeSet;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

import static com.ecnu.trivia.common.component.metric.MetricsHolder.METRICS;
import static com.ecnu.trivia.common.component.metric.MetricsHolder.name;

/**
 * 用于注册针对jvm层的度量指标监控信息
 *
 * @author Jack Chen
 */
@Component
public class MetricsJvmRegister {

    @PostConstruct
    public void init() {
        METRICS.register(name("memory"), new MemoryUsageGaugeSet());
        METRICS.register(name("thread"), new CachedThreadStatesGaugeSet(10, TimeUnit.SECONDS));
        METRICS.register(name("gc"), new GarbageCollectorMetricSet());
    }
}
