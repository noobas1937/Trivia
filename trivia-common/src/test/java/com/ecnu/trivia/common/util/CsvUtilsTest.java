package com.ecnu.trivia.common.util;

import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.List;

/**
 * Created by Jack Chen at 15-10-27
 */
public class CsvUtilsTest {

    @DataProvider
    public Object[][] p4testReadLineByUrlAndHeaderAndColumn() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 4;
        Object[] objects;

        //1 在正确的情况下能够查找到相应的数据
        objects = new Object[paramLength];
        objects[0] = CsvUtilsTest.class.getResource(
                "/com/ecnu/trivia/common/util/domaincsvutils/Value.csv.txt");
        objects[1] = "V1";
        objects[2] = 6;
        objects[3] = true;
        objectsList.add(objects);

        //2 在列数不相同的情况下不能找到数据
        objects = new Object[paramLength];
        objects[0] = CsvUtilsTest.class.getResource(
                "/com/ecnu/trivia/common/util/domaincsvutils/Value.csv.txt");
        objects[1] = "V2";
        objects[2] = 7;//期望7列，实际列数为6
        objects[3] = false;
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试使用url和key以及匹配列数查找相应的数据 */
    @Test(dataProvider = "p4testReadLineByUrlAndHeaderAndColumn")
    public void testReadLineByUrlAndHeaderAndColumn(URL url, String header, int column, boolean found) throws Exception {
        String[] results = CsvUtils.readLine(url, header, column);

        if(found)
            assert results != null;
        else
            assert results == null;
    }

    @DataProvider
    public Object[][] p4testReadLineByUrlAndHeader() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //1 对于V2源，相应V3行，实际有效列仅4列，但实际上这个文件的列数为6列
        objects = new Object[paramLength];
        objects[0] = CsvUtilsTest.class.getResource(
                "/com/ecnu/trivia/common/util/domaincsvutils/Value2.csv.txt");
        objects[1] = "V3";
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试使用url和key，在忽略列数的情况下查找数据 */
    @Test(dataProvider = "p4testReadLineByUrlAndHeader")
    public void testReadLineByUrlAndHeader(URL url, String header) throws Exception {
        String[] results = CsvUtils.readLine(url, header);
        assert results != null;
    }
}
