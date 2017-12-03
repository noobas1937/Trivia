/** Created by Jack Chen at 12/14/2014 */
package com.ecnu.trivia.common.component.test;

import com.ecnu.trivia.common.ApplicationContextHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 测试标记,用于在实现层面兼容测试考虑,避免与容器绑定
 *
 * @author Jack Chen
 */
@Component
public class TestConfig {
    private static TestConfig instance;

    @Value("${testFlag:false}")
    private Boolean testFlag = false;

    @Value("${mockFlag:false}")
    private Boolean mockFlag = false;

    /** 是否开启测试标识 */
    public boolean isTestFlag() {
        return testFlag == null ? false : testFlag;
    }

    /** 是否在模拟环境中 */
    public boolean isMockFlag() {
        return mockFlag == null ? false : mockFlag;
    }

    public static TestConfig getInstance() {
        if(instance == null) {
            instance = ApplicationContextHolder.getInstance(TestConfig.class);
        }

        return instance;
    }
}
