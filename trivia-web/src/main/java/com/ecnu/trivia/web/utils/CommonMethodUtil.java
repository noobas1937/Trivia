package com.ecnu.trivia.web.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class CommonMethodUtil {

    private static Logger logger = LoggerFactory.getLogger(CommonMethodUtil.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        InputStream in;
        try {
            in = new FileInputStream(new File("e:\\5000个视讯版手机号.csv"));
            String outPath = "e:\\test.csv";
            OutputStream os = new FileOutputStream(outPath);
            writeFile(in, os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入文件
     */
    public static boolean writeFile(InputStream in, OutputStream os) {
        boolean write = false;
        if (in == null || os == null) {
            return write;
        }
        try {
            int len = in.available();
            //判断长度是否大于1M
            if (len <= 1024 * 1024) {
                byte[] bytes = new byte[len];
                in.read(bytes);
                os.write(bytes);
            } else {
                int byteCount = 0;
                //1M逐个读取  
                byte[] bytes = new byte[1024 * 1024];
                while ((byteCount = in.read(bytes)) != -1) {
                    os.write(bytes, 0, byteCount);
                }
            }
            in.close();
            os.flush();
            os.close();
            write = true;
        } catch (Exception e) {
            logger.error("", e);
        }
        return write;
    }


    public static void deleteFile(String outPath) {
        File outFile = new File(outPath);
        if (outFile.exists()) {
            outFile.delete();
        }
    }


    /**
     * 文件写入
     *
     * @param content
     * @param fileName
     * @return
     */
    public static boolean writeTxtFile(String content, File file) {
        boolean flag = false;
        if (content == null) {
            return flag;
        }
        FileOutputStream o = null;
        try {
            o = new FileOutputStream(file);
            o.write(content.getBytes("UTF-8"));
            o.close();
            flag = true;
        } catch (Exception e) {
            logger.error("", e);
        }
        return flag;
    }

}
