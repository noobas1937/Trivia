/** Created by shilian.peng at 16/6/29 */
package com.ecnu.trivia.web.common.domain.enums;

import com.google.common.collect.Maps;
import com.ecnu.trivia.common.exception.IRCloudException;
import com.ecnu.trivia.common.util.FTPUtils;
import com.ecnu.trivia.common.util.FileDataUtils;
import com.ecnu.trivia.common.util.ObjectUtils;
import com.ecnu.trivia.common.util.PropertiesUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/** @author shilian.peng */
public enum UploadTypeValue {

    ftp("ftp") {
        @Override
        public boolean uploadFile(String uploadPath, InputStream inputStream) {
            return FTPUtils.uploadRemoteFile(uploadPath, inputStream);
        }

        @Override
        public boolean downloadFile(String downloadPath, OutputStream outputStream) {
            return FTPUtils.downloadRemoteFile(downloadPath, outputStream);
        }

        /** 下载文件并压缩到Zip,然后返回压缩文件路径 */
        @Override
        public void downloadFiles2Zip(List<String> downloadPaths, OutputStream outputStream) {
            String temp = System.getProperty("java.io.tmpdir");
            String uuid = UUID.randomUUID().toString();
            String downloadFolder = temp + File.separator + uuid;

            try{
                //创建临时目录用于保存下载文件
                FileDataUtils.createDirectory(temp, uuid);

                //处理重名
                Map<String, Integer> nameIndexMap = Maps.newHashMap();

                //下载文件
                downloadPaths.forEach(t -> {
                    String name = FilenameUtils.getName(t);
                    Integer count = nameIndexMap.get(name);
                    if(Objects.isNull(count)) {
                        nameIndexMap.put(name, 1);
                    } else {
                        count = count + 1;
                        name = FilenameUtils.getBaseName(name) + " " + String.valueOf(count) + "." + FilenameUtils.getExtension(name);
                        nameIndexMap.put(name, count);
                    }

                    FTPUtils.downLoadRemoteFile(t, downloadFolder, name);
                });

                //将文件夹压缩成zip
                FileDataUtils.fileToZip(downloadFolder, outputStream);

                //删除整个文件夹
                FileUtils.deleteDirectory(new File(temp + File.separator + uuid));
            } catch(IOException e) {
                throw new IRCloudException(e.getMessage());
            }
        }
    }, local("本地") {
        @Override
        public boolean uploadFile(String uploadPath, InputStream inputStream) {
            try{
                FileDataUtils.createDirectory(getLocalRootPath(), FilenameUtils.getPath(uploadPath));
                FileDataUtils.writeFile(getFullPath(uploadPath), inputStream);
                return true;
            } catch(IOException e) {
                logger.error("文件上传失败：", e);
                return false;
            }
        }

        @Override
        public boolean downloadFile(String downloadPath, OutputStream outputStream) {
            try{
                FileDataUtils.readFile(getFullPath(downloadPath), outputStream);
                return true;
            } catch(IOException e) {
                logger.error("文件下载失败：", e);
                return false;
            }
        }

        @Override
        public void downloadFiles2Zip(List<String> downloadPaths, OutputStream outputStream) {
            List<File> fileList = downloadPaths.stream()
                    .map(t -> new File(getFullPath(t)))
                    .collect(Collectors.toList());

            FileDataUtils.fileToZip(fileList, outputStream);

        }
    };

    private static final Logger logger = LoggerFactory.getLogger(UploadTypeValue.class);

    private String desc;

    /** 指定文件上传的根目录 */
    private static final String localRootPath = "/";

    /** 上传文件 */
    public abstract boolean uploadFile(String uploadPath, InputStream inputStream);

    /** 下载文件 */
    public abstract boolean downloadFile(String downloadPath, OutputStream outputStream);


    /** 下载文件并压缩到Zip,然后返回压缩文件路径 */
    public abstract void downloadFiles2Zip(List<String> downloadPaths, OutputStream outputStream);

    /** 生成本地上传路径 */
    public static String getFullPath(String relativePath) {
        return getLocalRootPath() + "/" + relativePath;
    }

    public static String getLocalRootPath() {
        String localPropPath = PropertiesUtils.getProperty("file.local.path");
        if(ObjectUtils.isNullOrEmpty(localPropPath)) {
            localPropPath = localRootPath;
        }
        return localPropPath;
    }

    UploadTypeValue(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
