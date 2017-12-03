/**
 * Created by shilian.peng at 16/6/21
 */
package com.ecnu.trivia.common.util;

import com.ecnu.trivia.common.ApplicationContextHolder;
import com.ecnu.trivia.common.component.ftp.FTPClientOperation;
import com.ecnu.trivia.common.component.ftp.FTPConfig;
import com.ecnu.trivia.common.component.ftp.FTPPool;
import com.ecnu.trivia.common.exception.IRCloudException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

/**
 * FTP相关工具类,封装上传、下载、删除等常用操作
 *
 * @author shilian.peng
 */
public class FTPUtils {

    public static Logger logger = LoggerFactory.getLogger(FTPUtils.class);
    private static FTPPool ftpPool;

    public static FTPPool getFtpPool() {
        if (Objects.nonNull(ftpPool)) {
            return ftpPool;
        }

        ftpPool = ApplicationContextHolder.getApplicationContext().getBean("fTPPool", FTPPool.class);
        return ftpPool;
    }

    /**
     * 上传文件，可以是文件也可以是目录 ,toPath目录在本地必须存在
     */
    public static boolean deleteRemoteFile(String deleteFilePath) {
        return execute(ftpClient -> {
            try {
                String filePath = FTPClientOperation.gbkToIso8859(deleteFilePath);
                FTPClientOperation ftpClientOperation = new FTPClientOperation(ftpClient);
                return ftpClientOperation.deleteFileOrDirectory(filePath);
            } catch (IOException e) {
                logger.error("文件删除失败", e);
                throw new IRCloudException(e.getMessage());
            }
        });
    }

    /**
     * 切换ftp工作目录
     *
     * @param pathname
     * @return
     */
    public static boolean changeWorkDirectory(String pathname) {
        return execute(ftpClient -> {
            try {
                return ftpClient.changeWorkingDirectory(pathname);
            } catch (IOException e) {
                logger.error("文件下载失败", e);
                throw new IRCloudException(e.getMessage(), e);
            }
        });
    }

    /**
     * 上传文件，可以是文件也可以是目录 ,toPath目录在本地必须存在 ，sourceFile文件或者路径
     */
    public static boolean downLoadRemoteFile(String sourceFilePath, String toPath, String toName) {
        return execute(ftpClient -> {
            try {
                FTPClientOperation ftpClientOperation =
                        FTPClientOperation.buildOperation(ftpClient, FTPClientOperation.gbkToIso8859(toPath), FTPClientOperation.gbkToIso8859(sourceFilePath), FTPClientOperation.gbkToIso8859(toName));

                return ftpClientOperation.downloadFileOrDirectory(FTPClientOperation.gbkToIso8859(sourceFilePath));
            } catch (IOException e) {
                logger.error("文件下载失败", e);
                throw new IRCloudException(e.getMessage());
            }
        });
    }


    /**
     * 简化版，将uploadPath和newFileName合并
     */
    public static boolean uploadRemoteFile(File localFile, String fileName) {
        int endIndex = fileName.lastIndexOf("/");
        String uploadPath = fileName.substring(0, endIndex);
        String newFileName = fileName.substring(endIndex + 1);
        return FTPUtils.uploadRemoteFile(localFile, uploadPath, newFileName);
    }

    /**
     * 上传文件，可以是文件也可以是目录
     */
    public static boolean uploadRemoteFile(File localFile, String uploadPath, String newFileName) {
        return execute(ftpClient -> {
            try {
                FTPClientOperation ftpClientOperation =
                        FTPClientOperation.buildOperation(ftpClient, uploadPath, localFile.getPath(), newFileName);

                if (!Objects.equals(uploadPath, "")) {
                    ftpClientOperation.createDirectories(uploadPath);
                }

                return ftpClientOperation.uploadFileOrDirectory(localFile);
            } catch (IOException e) {
                logger.error("文件上传失败", e);
                throw new IRCloudException(e.getMessage());
            }
        });
    }

    /**
     * 下载文件到输出流
     */
    public static boolean downloadRemoteFile(String remoteFile, OutputStream out) {
        return execute(ftpClient -> {
                    try {
                        FTPClientOperation ftpClientOperation = new FTPClientOperation(ftpClient);
                        return ftpClientOperation.download(remoteFile, out);

                    } catch (IOException e) {
                        logger.error("文件下载失败", e);
                        throw new IRCloudException(e.getMessage());
                    }
                }
        );
    }

    /**
     * 从输入流上传文件
     */
    public static boolean uploadRemoteFile(String remoteFile, InputStream input) {
        return execute(ftpClient -> {
                    try {
                        FTPClientOperation ftpClientOperation = new FTPClientOperation(ftpClient);
                        return ftpClientOperation.uploadFile(remoteFile, input);
                    } catch (IOException e) {
                        logger.error("文件下载失败", e);
                        throw new IRCloudException(e.getMessage());
                    }
                }
        );
    }

    /**
     * 用日期来生成一个目录
     */
    public static String getFilePathWithDate(String prefix, Date date) {
        String filePath = new SimpleDateFormat("yyyyMM/dd").format(date);
        if (prefix == null || prefix.length() == 0) {
            return filePath;
        }
        return prefix + "/" + filePath;
    }

    /**
     * 通过uuid来生成一个文件名
     */
    public static String getNewFilename(String fileName) {
        return UUID.randomUUID().toString().replace("-", "") + "."
                + FilenameUtils.getExtension(fileName);
    }

    /**
     * 获取文件类型
     */
    public static String getFileMimeType(String fileName) {
        String mimeType = new MimetypesFileTypeMap().getContentType(fileName);
        if (mimeType == null) {
            return "";
        }
        return mimeType;
    }

    /**
     * 模板方法，所有FTPClient相关操作都必须调这个方法
     */
    private static <T> T execute(Function<FTPClient, T> function) {
        FTPClient ftpClient = getFtpPool().getResource();
        //在使前前必须将工作目录切换到根目录，不然会导致上传或下载目录错乱
        FTPConfig config = ApplicationContextHolder.getApplicationContext().getBean(FTPConfig.class);
        try {
            if (!ObjectUtils.isNullOrEmpty(config.getPath())) {
                ftpClient.changeWorkingDirectory(config.getPath());
            }
        } catch (IOException e) {
            throw new IRCloudException(e.getMessage());
        }

        try {
            return function.apply(ftpClient);
        } finally {
            getFtpPool().returnResource(ftpClient);
        }
    }

}
