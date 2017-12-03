package com.ecnu.trivia.common.component.test.testng;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.ecnu.trivia.common.component.json.JsonUtils;
import com.ecnu.trivia.common.exception.IRCloudException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class AssertJsonTest extends BaseTest {

    @DataProvider
    public Object[][] p4testAssertEquals() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 4;
        Object[] objects;

        //场景1：测试两个值同时为null的场景，同一个字符串生成的json比较
        objects = new Object[paramLength];
        objects[0] = "com/ecnu/trivia/common/component/test/testng/AssertJson/case.json";
        objects[1] = "com/ecnu/trivia/common/component/test/testng/AssertJson/case.json";
        objects[2] = "success";
        objects[3] = "";
        objectsList.add(objects);

        //场景2：测试JSONObject 元素个数不一致， 删除 caption
        objects = new Object[paramLength];
        objects[0] = "com/ecnu/trivia/common/component/test/testng/AssertJson/case.json";
        objects[1] = "com/ecnu/trivia/common/component/test/testng/AssertJson/case1.json";
        objects[2] = "faliure";
        objects[3] = "json比较失败，失败信息：key:$.caption,actual:商品数据,expected:null";
        objectsList.add(objects);

        //场景3：测试JSONArray 大小不一致的情况，删除 id.dimensionList.children 节点下的最后一个值
        objects = new Object[paramLength];
        objects[0] = "com/ecnu/trivia/common/component/test/testng/AssertJson/case.json";
        objects[1] = "com/ecnu/trivia/common/component/test/testng/AssertJson/case2.json";
        objects[2] = "faliure";
        objects[3] = "json比较失败，失败信息：key:$.dimensionList[0].children[0].children[5],actual:{\"id\":-106,\"description\":\"销售时间_年/周\",\"caption\":\"销售时间_年/周\",\"concreteType\":\"year_week\",\"yearMonthDateCubeFieldId\":-103,\"category\":\"DATE\"},expected:null";
        objectsList.add(objects);

        //测试4：key一致，value不一致 caption改为 ”商品数据1”
        objects = new Object[paramLength];
        objects[0] = "com/ecnu/trivia/common/component/test/testng/AssertJson/case.json";
        objects[1] = "com/ecnu/trivia/common/component/test/testng/AssertJson/case3.json";
        objects[2] = "faliure";
        objects[3] = "json比较失败，失败信息：key:$.caption,actual:商品数据,expected:商品数据1";
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }


    @Test(dataProvider = "p4testAssertEquals")
    public void testAssertEquals(String actual, String expected, String status, String msg) throws Exception {

        String json1 = Files.lines(Paths.get(Resources.getResource(actual).toURI())).collect(Collectors.joining());
        Object actualValue = JsonUtils.parse(json1);

        String json2 = Files.lines(Paths.get(Resources.getResource(expected).toURI())).collect(Collectors.joining());
        Object expectedValue = JsonUtils.parse(json2);

        switch(status) {
            case "faliure":
                try{
                    AssertJson.assertEquals(actualValue, expectedValue);
                } catch(IRCloudException e) {
                    AssertEx.assertEquals(e.getMessage(), msg);
                }
                break;
            case "success":
                AssertJson.assertEquals(actualValue, expectedValue);
        }
    }
}
