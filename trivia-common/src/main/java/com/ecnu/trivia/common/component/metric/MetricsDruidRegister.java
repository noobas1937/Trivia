/** Created by Jack Chen at 15-11-16 */
package com.ecnu.trivia.common.component.metric;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceStatValue;
import com.codahale.metrics.CachedGauge;
import com.codahale.metrics.Gauge;
import com.ecnu.trivia.common.ApplicationContextHolder;
import com.ecnu.trivia.common.util.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

import static com.ecnu.trivia.common.component.metric.MetricsHolder.METRICS;
import static com.ecnu.trivia.common.component.metric.MetricsHolder.name;

/**
 * 用于注册对druid数据源的度量指标监控
 *
 * @author Jack Chen
 */
@Component
public class MetricsDruidRegister {
    private static final Logger logger = LoggerFactory.getLogger(MetricsDruidRegister.class);

    /** 业务中使用的数据库的bean名字 */
    @Value("${metrics.db.druid.datasourceId:}")
    private String datasourceName;

    /** 是否druid数据连接池可用 */
    private static boolean isDruidAvailable() {
        try{
            Class.forName("com.alibaba.druid.pool.DruidDataSource");
            return true;
        } catch(ClassNotFoundException ignore) {
            return false;
        }
    }

    @PostConstruct
    public void init() {
        if(ObjectUtils.isNullOrEmpty(datasourceName)) {
            return;
        }

        if(!isDruidAvailable()) {
            logger.info("druid服务不可用，因此不再开启druid监控");
            return;
        }

        METRICS.register(name("druid", "poolingCount"), (Gauge<Integer>) () -> DruidHolder.getGauge(this).getValue().getPoolingCount());
        METRICS.register(name("druid", "poolingPeak"), (Gauge<Integer>) () -> DruidHolder.getGauge(this).getValue().getPoolingPeak());
        METRICS.register(name("druid", "activeCount"), (Gauge<Integer>) () -> DruidHolder.getGauge(this).getValue().getActiveCount());
        METRICS.register(name("druid", "activePeak"), (Gauge<Integer>) () -> DruidHolder.getGauge(this).getValue().getActivePeak());
        METRICS.register(name("druid", "connectCount"), (Gauge<Long>) () -> DruidHolder.getGauge(this).getValue().getConnectCount());
        METRICS.register(name("druid", "closeCount"), (Gauge<Long>) () -> DruidHolder.getGauge(this).getValue().getCloseCount());
        METRICS.register(name("druid", "waitThreadCount"), (Gauge<Long>) () -> DruidHolder.getGauge(this).getValue().getWaitThreadCount());
        METRICS.register(name("druid", "logicConnectErrorCount"), (Gauge<Long>) () -> DruidHolder.getGauge(this).getValue().getLogicConnectErrorCount());
        METRICS.register(name("druid", "physicalConnectCount"), (Gauge<Long>) () -> DruidHolder.getGauge(this).getValue().getPhysicalConnectCount());
        METRICS.register(name("druid", "physicalCloseCount"), (Gauge<Long>) () -> DruidHolder.getGauge(this).getValue().getPhysicalCloseCount());
        METRICS.register(name("druid", "physicalConnectErrorCount"), (Gauge<Long>) () -> DruidHolder.getGauge(this).getValue().getPhysicalConnectErrorCount());
        METRICS.register(name("druid", "executeCount"), (Gauge<Long>) () -> DruidHolder.getGauge(this).getValue().getExecuteCount());
        METRICS.register(name("druid", "errorCount"), (Gauge<Long>) () -> DruidHolder.getGauge(this).getValue().getErrorCount());
        METRICS.register(name("druid", "commitCount"), (Gauge<Long>) () -> DruidHolder.getGauge(this).getValue().getCommitCount());
        METRICS.register(name("druid", "rollbackCount"), (Gauge<Long>) () -> DruidHolder.getGauge(this).getValue().getRollbackCount());
        METRICS.register(name("druid", "pstmtCacheHitCount"), (Gauge<Long>) () -> DruidHolder.getGauge(this).getValue().getPstmtCacheHitCount());
        METRICS.register(name("druid", "pstmtCacheMissCount"), (Gauge<Long>) () -> DruidHolder.getGauge(this).getValue().getPstmtCacheMissCount());
    }

    /** 用于持有相应的对象，避免外层类静态初始化失败 */
    private static class DruidHolder {
        private static final DruidDataSourceStatValue emptyStatValue = new DruidDataSourceStatValue();

        /** 临时缓存的统计持有器 */
        private static transient Gauge<DruidDataSourceStatValue> statValueGauge;

        private static Gauge<DruidDataSourceStatValue> getGauge(MetricsDruidRegister register) {
            if(statValueGauge == null) {
                DruidDataSource druidDataSource = (DruidDataSource) ApplicationContextHolder.getApplicationContext().getBean(register.datasourceName);
                //缓存时长 30秒
                statValueGauge = new CachedGauge<DruidDataSourceStatValue>(30, TimeUnit.SECONDS) {
                    @Override
                    public DruidDataSourceStatValue getValue() {
                        DruidDataSourceStatValue statValue = super.getValue();
                        return statValue == null ? DruidHolder.emptyStatValue : statValue;
                    }

                    @Override
                    protected DruidDataSourceStatValue loadValue() {
                        //避免还没有取得统计信息的情况
                        if(druidDataSource.getDataSourceStat() == null) {
                            return null;
                        }

                        return druidDataSource.getStatValueAndReset();
                    }
                };
            }

            return statValueGauge;
        }
    }
}
