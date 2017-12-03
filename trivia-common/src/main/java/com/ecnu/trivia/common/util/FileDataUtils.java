/** Created by Jack Chen at 9/29/2014 */
package com.ecnu.trivia.common.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 针对数据内容作处理
 *
 * @author Jack Chen
 */
public class FileDataUtils {
    private static final int BUFFER_SIZE = 1024 * 1024;//缓冲区大小 1M
    private static final Logger logger = LoggerFactory.getLogger(FileDataUtils.class);

    private static final Pattern crlfPattern = Pattern.compile("[\\r\\n]");
    private static final Pattern tabPattern = Pattern.compile("\\t");
    private static Joiner joiner = Joiner.on("\t").useForNull("");

    /** 获取指定文件有效行数,如果文件不存在，则返回0 */
    public static int getFileLine(File file) throws IOException {
        if(!file.exists()) {
            return 0;
        }

        return Files.readLines(file, Charsets.UTF_8, new LineProcessor<Integer>() {
            int i = 0;

            @Override
            public boolean processLine(String line) throws IOException {
                if(!ObjectUtils.isNullOrEmpty(line)) {
                    i++;
                }
                return true;
            }

            @Override
            public Integer getResult() {
                return i;
            }
        });
    }

    /** 删除指定的文件 */
    public static void deleteFile(File file) {
        file.delete();
    }

    /** 保证文件存在 */
    public static void existFile(File file) {
        if(file.exists()) {
            return;
        }
        try{
            file.createNewFile();
        } catch(IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /** 保证文件夹存在 */
    public static void existDirectory(File directory) {
        if(!directory.exists()) {
            directory.mkdirs();
        }
    }

    /** 将数据信息追加至文件当中,为保证数据的正确性，替换特殊字符,末尾追加回车 */
    public static void appendFile(List<List<String>> contentList, File file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), Charsets.UTF_8));

        //处理数据
        int i = 0;
        for(List<String> strList : contentList) {
            List<String> translateStrList = Lists.transform(strList, new Function<String, String>() {
                @Override
                public String apply(String input) {
                    if(input == null) {
                        return null;
                    }
                    input = crlfPattern.matcher(input).replaceAll("");
                    input = tabPattern.matcher(input).replaceAll("    ");//四个空格
                    input = input.trim();
                    return input;
                }
            });

            String line = joiner.join(translateStrList);
            writer.write(line);
            writer.write("\n");


            i++;
            if(i % 1000 == 0) {
                writer.flush();
            }
        }
        writer.flush();
        writer.close();
    }


    /** 从文件中读取字符串 */
    public static String readFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8));
        String temp;
        StringBuilder sb = new StringBuilder("");
        while((temp = reader.readLine()) != null) {
            sb.append(temp);
        }
        reader.close();
        return sb.toString();
    }

    public static byte[] readFileByte(File file) throws IOException{
        Path dataFilePath=file.toPath();
        return java.nio.file.Files.readAllBytes(dataFilePath);
    }

    /** 读取文件到输出流 */
    public static void readFile(String downloadPath, OutputStream outputStream) throws IOException {
        outputStream.write(Files.toByteArray(new File(downloadPath)));
        logger.debug("文件读取成功");
    }

    /** 从文件中读取集合字符串，生成格式list map */
    public static List<Map<String, Object>> readFileList(File file) throws IOException {
        String result = readFile(file);

        JSONArray jsonArray = JSONArray.parseArray(result);
        Iterator<Object> it = jsonArray.iterator();
        List<Map<String, Object>> list = new ArrayList<>();
        while(it.hasNext()) {
            JSONObject json = (JSONObject) it.next();
            Map<String, Object> map = new HashMap<>();
            for(Map.Entry<String, Object> entry : json.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
            list.add(map);
        }

        return list;
    }

    /** 将多个文件的内容整合到同一个文件当中 */
    public static void mergeFile(File fileName, List<File> mergeList) throws IOException {
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fileName), BUFFER_SIZE);
        for(File file : mergeList) {
            if(!file.exists()) {
                continue;
            }
            InputStream in = new FileInputStream(file);
            writeFile(outputStream, in);
        }

        outputStream.flush();
        outputStream.close();
    }

    /** 将字符串文件写入文件 */
    public static void writeFile(String path, String data) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), Charsets.UTF_8));
        writer.write(data);
        writer.flush();
        writer.close();
    }

    /** 将流写入到文件 */
    public static void writeFile(String path, InputStream inputStream) throws IOException {
        try(BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(path))){
            writeFile(bufferedOutputStream, inputStream);
            logger.debug("文件写入成功：" + path);
        }
    }

    /** 创建目录 */
    public static void createDirectory(String root, String relativePath) throws IOException {
        StringTokenizer s = new StringTokenizer(relativePath, "/");
        s.countTokens();
        String path = root;
        while(s.hasMoreElements()) {
            path = path + "/" + s.nextElement();
            Path dir = Paths.get(path);
            if(!dir.toFile().exists()) {
                java.nio.file.Files.createDirectory(dir);
            }
        }
        logger.debug("文件夹创建成功：" + root + "/" + relativePath);
    }

    private static void writeFile(BufferedOutputStream outputStream, InputStream in) throws IOException {
        BufferedInputStream inputStream = new BufferedInputStream(in);
        byte[] bytes = new byte[BUFFER_SIZE];
        int read;
        while((read = inputStream.read(bytes, 0, BUFFER_SIZE)) != -1) {
            outputStream.write(bytes, 0, read);
        }
        inputStream.close();
        outputStream.flush();
    }


    /**
     * 将存放在sourceFilePath目录下的源文件，打包成fileName名称的zip文件，并存放到输出流
     */
    public static boolean fileToZip(String sourceFilePath, OutputStream outputStream) {
        File sourceFile = new File(sourceFilePath);
        List<File> sourceFiles = Lists.newArrayList(sourceFile.listFiles());
        return fileToZip(sourceFiles, outputStream);
    }

    /** 将文件列表下载到输出流 */
    public static boolean fileToZip(List<File> sourceFiles, OutputStream outputStream) {
        try(ZipOutputStream zos = new ZipOutputStream(outputStream)){
            byte[] bufs = new byte[1024 * 10];
            //处理重名
            Map<String, Integer> nameIndexMap = Maps.newHashMap();

            for(File sourceFile : sourceFiles) {
                //创建ZIP实体，并添加进压缩包
                String name = sourceFile.getName();
                Integer count = nameIndexMap.get(name);
                if(Objects.isNull(count)) {
                    nameIndexMap.put(name, 1);
                } else {
                    count = count + 1;
                    name = FilenameUtils.getBaseName(name) + " " + String.valueOf(count) + "." + FilenameUtils.getExtension(name);

                    nameIndexMap.put(name, count);
                }

                ZipEntry zipEntry = new ZipEntry(name);
                zos.putNextEntry(zipEntry);
                //读取待压缩的文件并写进压缩包里
                try(FileInputStream fis = new FileInputStream(sourceFile);
                    BufferedInputStream bis = new BufferedInputStream(fis, 1024 * 10)){
                    int read;

                    while((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
                        zos.write(bufs, 0, read);
                    }
                }
            }
            return true;
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }


}
