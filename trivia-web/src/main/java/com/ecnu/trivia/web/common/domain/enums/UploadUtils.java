/** Created by shilian.peng at 16/11/23 */
package com.ecnu.trivia.web.common.domain.enums;

import com.ecnu.trivia.common.util.PropertiesUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 *  隔离本地和ftp的两种方式的操作
 *  @author shilian.peng
 *  */
public final class UploadUtils {
    private static final UploadTypeValue uploadTypeValue;

    static {
        String name = PropertiesUtils.resolveEmbeddedValue("${file.upload.type:local}");
        uploadTypeValue = UploadTypeValue.valueOf(name);
    }

    /** 上传文件 */
    public static boolean uploadFile(String uploadPath, InputStream inputStream) {
        return uploadTypeValue.uploadFile(uploadPath, inputStream);
    }

    /** 下载文件 */
    public static boolean downloadFile(String downloadPath, OutputStream outputStream) {
        return uploadTypeValue.downloadFile(downloadPath, outputStream);
    }

    /** 下载文件到Zip */
    public static void downloadFiles2Zip(List<String> downloadPaths, OutputStream outputStream) {
        uploadTypeValue.downloadFiles2Zip(downloadPaths, outputStream);
    }

}
