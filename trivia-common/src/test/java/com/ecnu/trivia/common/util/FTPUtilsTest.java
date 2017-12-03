package com.ecnu.trivia.common.util;

import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.ecnu.trivia.common.component.test.testng.AssertEx;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FTPUtilsTest {

    public static Logger logger = LoggerFactory.getLogger(FTPUtilsTest.class);

    @DataProvider
    public Object[][] p4testUploadRemoteFile() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 6;
        Object[] objects;

        //1 测试文件夹的上传、下载、删除
        objects = new Object[paramLength];
        objects[0] = "com/yunat/newbi/common/component/domain";
        objects[1] = "java/src/工具类";
        objects[2] = System.getProperty("java.io.tmpdir");
        objects[3] = "util";
        objects[4] = "java";
        objects[5] = System.getProperty("java.io.tmpdir") + "/" + "util";
        objectsList.add(objects);

        //2 测试文件的上传、下载、删除
        objects = new Object[paramLength];
        objects[0] = "com/ecnu/trivia/common/util/FTPUtilsTest/test.txt";
        objects[1] = "data/test1.txt";
        objects[2] = System.getProperty("java.io.tmpdir");
        objects[3] = "test.txt";
        objects[4] = "data";
        objects[5] = System.getProperty("java.io.tmpdir") + "/" + "test.txt";
        objectsList.add(objects);
        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    @Test(dataProvider = "p4testUploadRemoteFile")
    public void testUploadRemoteFile(String localPath, String uploadPath, String downloadLocalPath, String downloadLocalFileName, String deleteRomoteFile, String deleteLocal) throws Exception {
        File file = Paths.get(Resources.getResource(localPath).toURI()).toFile();
        try{

            boolean upload = FTPUtils.uploadRemoteFile(file, uploadPath);
            AssertEx.assertTrue(upload);

            boolean download = FTPUtils.downLoadRemoteFile(uploadPath, downloadLocalPath, downloadLocalFileName);
            AssertEx.assertTrue(download);
        } finally {
            try{
                boolean delete = FTPUtils.deleteRemoteFile(deleteRomoteFile);
                AssertEx.assertTrue(delete);
            } finally {
                File file1 = new File(deleteLocal);
                if(file1.isDirectory()) {
                    FileUtils.deleteDirectory(file1);
                } else {
                    Files.delete(file1.toPath());
                }
                logger.debug("本地文件删除成功：" + deleteLocal);
            }
        }
    }

}
