/**
 * Created by shilian.peng at 16/6/21
 */
package com.ecnu.trivia.common.component.ftp;

import com.google.common.base.Charsets;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.StringTokenizer;

/**
 * 对ftpClient的简单封装，如果这个功能直接写成工具类，
 * 那么所有的方法都要传入ftpClient对象，所以这边新建了一个类，这个类是线程不安全的，使用的时候需要new出来，
 * 和FTPUtils类搭配使用
 *
 * @author shilian.peng
 */
public class FTPClientOperation {
    private static final Logger logger = LoggerFactory.getLogger(FTPClientOperation.class);
    private FTPClient ftpClient;

    /** 目标路径，如 data/data1 */
    private String toPath;

    /** 文件或目录的目标名称，如data、aaa.png */
    private String toFileName;

    /** 源文件或目录的全路径，如 c:/data 、c:/data/aa.png */
    private String sourceFilePath;

    /** 用于文件上传功能的初始化 */
    public static FTPClientOperation buildOperation(FTPClient ftpClient, String uploadPath, String sourceFilePath, String toFileName) {
        FTPClientOperation ftpClientOperation = new FTPClientOperation(ftpClient);
        ftpClientOperation.setSourceFilePath(sourceFilePath);
        ftpClientOperation.setToPath(uploadPath);
        ftpClientOperation.setToFileName(toFileName);
        return ftpClientOperation;
    }

    public FTPClientOperation(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    //======================删除文件==============================================//start

    /** 删除一个空目录 */
    public boolean removeDirectory(String path) throws IOException {
        return ftpClient.removeDirectory(path);
    }

    /** 删除一个文件 */
    public boolean deleteFile(String filePath) throws IOException {
        return ftpClient.deleteFile(filePath);
    }

    /** 递归删除一个目录 */
    public boolean deleteFileOrDirectory(String filePath)
            throws IOException {
        boolean returnValue;
        //如果是文件直接删除，退出递归
        if (isFile(filePath)) {
            returnValue = deleteFile(filePath);
            writeDeleteLog(filePath, returnValue);
            return returnValue;
        }

        //先递归删除文件夹下面的文件，在删除当前文件夹
        for (FTPFile file : ftpClient.listFiles(filePath)) {
            returnValue = deleteFileOrDirectory(filePath + "/" + file.getName());
            if (!returnValue) {
                return false;
            }
        }
        returnValue = removeDirectory(filePath);
        writeDeleteLog(filePath, returnValue);

        return returnValue;
    }

    private void writeDeleteLog(String filePath, boolean returnValue) throws UnsupportedEncodingException {
        String gbk = iso8859ToGbk(filePath);
        if (!returnValue) {
            logger.debug("删除失败，路径：" + gbk);
        } else {
            logger.debug("删除成功，路径：" + gbk);
        }
    }

    //======================删除文件==============================================//end


    //======================文件下载==============================================//start

    /** 下载文件到输出流 */
    public boolean download(String remoteFileName, OutputStream out) throws IOException {
        return ftpClient.retrieveFile(gbkToIso8859(remoteFileName), out);
    }

    /** 递归下载文件 */
    public boolean downloadFileOrDirectory(String fileName) throws IOException {

        boolean returnValue;
        //如果是文件，直接下载并退出递归
        String downloadPath = getDownloadPath(fileName);
        if (isFile(fileName)) {
            String pathname = downloadPath + "/" + getLocalFileName(fileName);
            try (FileOutputStream local = new FileOutputStream(new File(pathname))) {
                ftpClient.enterLocalPassiveMode();
                returnValue = ftpClient.retrieveFile(fileName, local);
                writeDownloadLog(pathname, returnValue);
                return returnValue;
            }
        }

        Files.createDirectory(Paths.get(downloadPath));
        logger.debug("文件下载成功，目录：" + downloadPath);

        for (FTPFile file : ftpClient.listFiles(fileName)) {
            returnValue = this.downloadFileOrDirectory(fileName + "/" + file.getName());
            if (!returnValue) {
                return false;
            }
        }

        return true;
    }

    private void writeDownloadLog(String fileName, boolean returnValue) {
        if (returnValue) {
            logger.debug("文件下载成功，目录：" + fileName);
        } else {
            logger.debug("文件下载失败，目录：" + fileName);
        }
    }

    /** 获取文件路径，主要处理修改下载目录的名称 */
    private String getDownloadPath(String file) throws IOException {
        String path = !this.isFile(file) ? file : FilenameUtils.getPath(file);
        return iso8859ToGbk(this.getToPath() + "/" +
                (isDownloadFile(file) ? "" : (this.getToFileName() + "/" + path.substring(this.getSourceFilePath().length()))));
    }

    /** 获取文件名称，主要处理修改文件名称的情况 */
    private String getLocalFileName(String file) throws IOException {
        String fileName = this.isDownloadFile(file) ? this.getToFileName() : FilenameUtils.getName(file);
        return iso8859ToGbk(fileName);
    }

    /** 判断远程文件是否是文件 */
    public boolean isFile(String fileName) throws IOException {
        String workingDirectory = this.getWorkingDirectory();
        try {
            return !ftpClient.changeWorkingDirectory(fileName);
        } finally {
            this.ftpClient.changeWorkingDirectory(workingDirectory);
        }
    }

    /** 判断第一次传递过来的是文件还是目录 */
    public boolean isDownloadFile(String fileName) throws IOException {
        return this.isFile(fileName) && Objects.equals(fileName, this.getSourceFilePath());
    }

    //======================文件下载==============================================//end


    //======================文件上传==============================================//start

    /** 通过流来上传一个文件 */
    public boolean uploadFile(String newName, InputStream inStream) throws IOException {
        createDirectories(FilenameUtils.getPath(gbkToIso8859(newName)));
        return ftpClient.storeFile(gbkToIso8859(newName), inStream);
    }

    /**
     * 递归上传一个文件或者目录
     */
    public boolean uploadFileOrDirectory(File file) throws IOException {
        boolean returnValue;

        //如果是文件直接上传并跳出递归
        String filePath = getPath(file);
        if (file.isFile()) {
            try (FileInputStream inputStream = new FileInputStream(file)) {
                String fileName = filePath + "/" + getFileName(file);
                returnValue = ftpClient.storeFile(fileName, inputStream);
                writeUploadLog(returnValue, fileName);
                return returnValue;
            }
        }

        //先创建目录，循环上传子文件
        returnValue = ftpClient.makeDirectory(filePath);
        writeUploadLog(returnValue, filePath);
        if (!returnValue) {
            return false;
        }

        for (String name : file.list()) {
            returnValue = this.uploadFileOrDirectory(new File(file.getPath() + "/" + name));
            if (!returnValue) {
                return false;
            }
        }

        return true;
    }

    private void writeUploadLog(boolean returnValue, String fileName) throws UnsupportedEncodingException {
        String gbk = iso8859ToGbk(fileName);
        if (returnValue) {
            logger.debug("上传成功，文件路径：" + gbk);
        } else {
            logger.debug("上传失败，文件路径：" + gbk);
        }
    }

    /** 获取文件名称，主要处理修改文件名称的情况 */
    private String getFileName(File file) throws UnsupportedEncodingException {
        String fileName = isUploadFile(file) ? this.getToFileName() : file.getName();
        return gbkToIso8859(fileName);
    }

    private boolean isUploadFile(File file) {
        return file.isFile() && Objects.equals(file.getPath(), this.getSourceFilePath());
    }

    /** 获取文件路径，主要处理修改上传目录的名称 */
    private String getPath(File file) throws IOException {
        String path = file.isDirectory() ? file.getPath() : file.getParent();
        return gbkToIso8859(getWorkingDirectory() +
                this.getToPath() + "/" +
                (isUploadFile(file) ? "" : (this.getToFileName() + "/" + path.substring(this.getSourceFilePath().length()))));

    }

    /**
     * 在服务器上创建目录
     */
    public void createDirectories(String path) throws IOException {
        StringTokenizer s = new StringTokenizer(path, "/");
        s.countTokens();
        String pathName = getWorkingDirectory();
        while (s.hasMoreElements()) {
            pathName = pathName + "/" + s.nextElement();
            ftpClient.makeDirectory(pathName);
        }
    }

    //======================文件上传==============================================//end

    /** 字符编码转换 */
    public static String gbkToIso8859(String str) throws UnsupportedEncodingException {
        return new String(str.getBytes("GBK"), Charsets.ISO_8859_1);
    }

    /** 字符编码转换 */
    public static String iso8859ToGbk(String str) throws UnsupportedEncodingException {
        return new String(str.getBytes(Charsets.ISO_8859_1), "GBK");
    }

    /**
     * 获取ftp服务器的根路径
     */
    public String getWorkingDirectory() throws IOException {
        return ftpClient.printWorkingDirectory().replace("\"", "");
    }

    public String getToPath() {
        return toPath;
    }

    public void setToPath(String toPath) {
        this.toPath = toPath;
    }

    public String getSourceFilePath() {
        return sourceFilePath;
    }

    public void setSourceFilePath(String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
    }

    public String getToFileName() {
        return toFileName;
    }

    public void setToFileName(String toFileName) {
        this.toFileName = toFileName;
    }
}
