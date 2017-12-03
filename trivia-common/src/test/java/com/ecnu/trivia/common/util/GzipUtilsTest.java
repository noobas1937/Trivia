/** Created by Jack Chen at 11/26/2014 */
package com.ecnu.trivia.common.util;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.ecnu.trivia.common.component.test.testng.AssertEx;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

/** @author Jack Chen */
public class GzipUtilsTest {

    @DataProvider
    public Object[][] p4testGzip() throws Exception {
        int paramLength = 1;
        List<Object[]> objectList = Lists.newArrayList();

        //通过gzip命令处理的原文件和目标文件
        Object[] objects = new Object[paramLength];
        objects[0] = Resources.toByteArray(GzipUtils.class.getResource("/test/gziputils/pom.xml"));
        objectList.add(objects);

        return objectList.toArray(new Object[objectList.size()][]);
    }



    @DataProvider
    public Object[][] p4testUnGzip() throws Exception {
        int paramLength = 2;
        List<Object[]> objectList = Lists.newArrayList();

        //通过gzip命令处理的原文件和目标文件 使用二进制文件避免不同平台的数据差异
        Object[] objects = new Object[paramLength];
        objects[0] = Resources.toByteArray(GzipUtils.class.getResource("/test/gziputils/log.rar.gz"));
        objects[1] = Resources.toByteArray(GzipUtils.class.getResource("/test/gziputils/log.rar.source"));
        objectList.add(objects);

        //2 如果相应的数据不是gzip数据，则返回原数据
        objects = new Object[paramLength];
        objects[0] = new byte[]{1, 2};
        objects[1] = new byte[]{1, 2};
        objectList.add(objects);

        objects = new Object[paramLength];
        objects[0] = new byte[]{1};
        objects[1] = new byte[]{1};
        objectList.add(objects);

        return objectList.toArray(new Object[objectList.size()][]);
    }

    @Test(dataProvider = "p4testUnGzip")
    public void testUnGzip(byte[] sourceBytes, byte[] destBytes) {
        byte[] result = GzipUtils.ungzip(sourceBytes);
        AssertEx.assertEquals(result, destBytes);
    }

    @Test
    public void testGzip() throws Exception {

    }
}
