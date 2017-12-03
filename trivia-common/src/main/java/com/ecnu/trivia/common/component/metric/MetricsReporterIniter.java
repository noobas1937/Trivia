/** Created by Jack Chen at 15-11-1 */
package com.ecnu.trivia.common.component.metric;

import com.codahale.metrics.CsvReporter;
import com.codahale.metrics.ganglia.GangliaReporter;
import com.ecnu.trivia.common.util.ObjectUtils;
import info.ganglia.gmetric4j.gmetric.GMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * 负责启动相应的报告输出信息
 *
 * @author Jack Chen
 */
public class MetricsReporterIniter implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(MetricsReporterIniter.class);

    /** csv报告的输出目录 */
    @Value("${metrics.csv.directory:}")
    private String csvReporterDirectory;

    /** csv报告的输出频率,默认5秒 */
    @Value("${metrics.csv.interval:5}")
    private int csvReporterInterval;

    /** ganglia报告的当前分组名 */
    @Value("${metrics.ganglia.group:}")
    private String gangliaReporterGroup;

    /** ganglia报告的当前端口信息,默认8649 */
    @Value("${metrics.ganglia.port:8649}")
    private int gangliaReporterPort;

    /** ganglia报告的当前网络模式,默认单播 */
    @Value("${metrics.ganglia.mode:UNICAST}")
    private GMetric.UDPAddressingMode gangliaReporterMode;

    /** ganglia报告的传输超时设置,默认5秒 */
    @Value("${metrics.ganglia.ttl:5}")
    private int gangliaReporterTtl;

    /** ganglia报告的输出频率，默认5秒 */
    @Value("${metrics.ganglia.interval:5}")
    private int gangliaReporterInterval;

    private transient CsvReporter csvReporter;
    private transient GangliaReporter gangliaReporter;

    @PostConstruct
    public void init() {
        initCsvReporter();

        try{
            initGangliaReporter();
        } catch(IOException e) {
            logger.error("初始化ganglia报告器失败:{}", e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        if(csvReporter != null) {
            csvReporter.close();
        }

        if(gangliaReporter != null) {
            gangliaReporter.close();
        }
    }

    /** 初始化csv报告器 */
    private void initCsvReporter() {
        if(ObjectUtils.isNullOrEmpty(csvReporterDirectory)) {
            return;
        }

        csvReporter = CsvReporter.forRegistry(MetricsHolder.METRICS)
                .formatFor(Locale.CHINA)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build(new File(csvReporterDirectory));

        csvReporter.start(csvReporterInterval, TimeUnit.SECONDS);
    }

    /** 初始化ganglia报告器 */
    private void initGangliaReporter() throws IOException {
        if(ObjectUtils.isNullOrEmpty(gangliaReporterGroup)) {
            return;
        }

        GMetric gMetric = new GMetric(gangliaReporterGroup, gangliaReporterPort, gangliaReporterMode, gangliaReporterTtl);
        gangliaReporter = GangliaReporter.forRegistry(MetricsHolder.METRICS)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build(gMetric);

        gangliaReporter.start(gangliaReporterInterval, TimeUnit.SECONDS);
    }
}
