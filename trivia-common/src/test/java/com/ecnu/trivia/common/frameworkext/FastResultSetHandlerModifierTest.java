/** Created by Jack Chen at 11/13/2014 */
package com.ecnu.trivia.common.frameworkext;

import org.testng.annotations.Test;

/** @author Jack Chen */
@Test
public class FastResultSetHandlerModifierTest {

    @Test
    public void testPostProcessBeanFactory() {
        //能正常地运行
        FastResultSetHandlerModifier modifier = new FastResultSetHandlerModifier();
        modifier.postProcessBeanFactory(null);
    }
}
