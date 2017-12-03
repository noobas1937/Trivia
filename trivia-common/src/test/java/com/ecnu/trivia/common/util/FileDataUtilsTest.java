package com.ecnu.trivia.common.util;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ecnu.trivia.common.component.test.testng.AssertEx;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.ResourceUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * 文件读写单元测试。
 * 测试将数据格式List<Map>写入到文件中，并将其读取出来为原来对象。
 *
 * @author wangwei
 * @date 2015/6/29.
 */
public class FileDataUtilsTest {

    private static final String path = "tempfile.out";

    private static List<Map<String, Object>> mapList;

    /**
     * 初始化要测试的数据对象mapList。
     * 格式List<Map<String, Object>>
     */
    @BeforeClass
    public void initData() {
        Map<String, Object> obj1 = new HashMap<>();
        obj1.put("a", 1);
        obj1.put("b", "2014-11-12");
        obj1.put("c", "b");
        obj1.put("d", "b");

        Map<String, Object> obj2 = new HashMap<>();
        obj2.put("a", 1);
        obj2.put("b", "2014-11-13");
        obj2.put("c", "b");
        obj2.put("d", "b");

        mapList = new ArrayList<>();
        mapList.add(obj1);
        mapList.add(obj2);
    }

    /**
     * 将对象转换成json字符串写入到文件中。
     */
    @Test
    public void testWriteFile() throws IOException {
        FileDataUtils.writeFile(path, JSONObject.toJSONString(mapList));

        File file = new File(path);
        Assert.assertTrue(file.exists());
        Files.delete(file.toPath());
    }

    /**
     * 将写入的文件读取成字符串。
     */
    @Test
    public void testReadFile() throws IOException {
        FileDataUtils.writeFile(path, JSONObject.toJSONString(mapList));

        File file = new File(path);
        String result = FileDataUtils.readFile(file);
        String expect = JSONObject.toJSONString(mapList);
        AssertEx.assertEquals(result, expect);
        Files.delete(file.toPath());
    }

    /**
     * 读取文件内容并转换为对象格式。
     */
    @Test
    public void testReadFileList() throws IOException {
        FileDataUtils.writeFile(path, JSONObject.toJSONString(mapList));

        File file = new File(path);
        List<Map<String, Object>> result = FileDataUtils.readFileList(file);
        AssertEx.assertEquals(result, mapList);
        Files.delete(file.toPath());
    }


    @DataProvider
    public Object[][] p4testWriteFile1() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 4;
        Object[] objects;

        //1 测试文件夹的上传、下载、删除
        objects = new Object[paramLength];
        objects[0] = "conf/ftp.properties";
        objects[1] = System.getProperty("java.io.tmpdir");
        objects[2] = "conf1/ftp1.properties";
        objects[3] = System.getProperty("java.io.tmpdir") + "/" + "conf1";
        objectsList.add(objects);


        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试本地文件的下载和上传,和文件夹的创建 */
    @Test(dataProvider = "p4testWriteFile1")
    public void testWriteFile1(String sourcePath, String toRootPath, String tofilePath, String deletePath) throws IOException {
        String fullPath = toRootPath + "/" + tofilePath;
        String tempFilePath = fullPath + ".temp";
        try{
            File file = ResourceUtils.getFile("classpath:" + sourcePath);

            //测试文件夹创建功能
            String folder = FilenameUtils.getPath(tofilePath);
            FileDataUtils.createDirectory(toRootPath, folder);
            AssertEx.assertTrue(Paths.get(toRootPath + "/" + folder).toFile().exists());
            //测试文件写入功能
            FileDataUtils.writeFile(fullPath, new FileInputStream(file));
            System.out.println(fullPath);
            AssertEx.assertTrue(Paths.get(fullPath).toFile().exists());

            //测试文件读取功能

            try(FileOutputStream outputStream = new FileOutputStream(tempFilePath)){
                FileDataUtils.readFile(fullPath, outputStream);
                outputStream.flush();
            }
            String context = FileDataUtils.readFile(new File(tempFilePath));
            String expected = FileDataUtils.readFile(file);
            System.out.println(context);
            AssertEx.assertTrue(Objects.equals(context, expected));

        } finally {
            try{
                FileUtils.deleteDirectory(Paths.get(deletePath).toFile());
            } finally {
                FileUtils.deleteDirectory(Paths.get(tempFilePath).toFile());
            }
        }
    }

}
