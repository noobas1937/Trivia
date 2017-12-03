/** Created by Jack Chen at 15-8-5 */
package com.ecnu.trivia.common.component.test.testng;


import com.ecnu.trivia.common.component.cache.web.ThreadCache;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/** @author Jack Chen */
public class ThreadCacheTestExecutionListener extends AbstractTestExecutionListener {
    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        ThreadCache.clearThreadCache();
    }
}
