/** Created by Jack Chen at 2014/6/18 */
package com.ecnu.trivia.common.util;

import com.google.common.base.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 提供数据压缩和解压的功能
 *
 * @author Jack Chen
 */
public class GzipUtils {
    private static final int BUFFER_SIZE = 1024 * 1024;//缓存区 1M
    private static final Logger logger = LoggerFactory.getLogger(GzipUtils.class);

    /** 对数据流进行gzip压缩 */
    public static byte[] gzip(final byte[] sourceBytes) {
        return LoggerUtils.debugTime(new Function<Void, byte[]>() {
            @Nullable
            @Override
            public byte[] apply(@Nullable Void aVoid) {
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(BUFFER_SIZE);
                    GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
                    gzipOutputStream.write(sourceBytes);
                    gzipOutputStream.flush();
                    gzipOutputStream.close();

                    return outputStream.toByteArray();
                } catch(Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }, logger, "压缩数据长度:{},花费时间:{}", new Object[] {sourceBytes.length, null});

    }

    /**
     * 对数据流进行解压
     * 如果数据流不是gzip格式，则不作任何操作
     */
    public static byte[] ungzip(final byte[] sourceBytes) {
        if(sourceBytes == null) {
            return null;
        }

        if(sourceBytes.length < 2) {
            return sourceBytes;
        }

        if(sourceBytes[0] != 0x1F || sourceBytes[1] != (byte)0x8B) {
            return sourceBytes;
        }

        return LoggerUtils.debugTime(new Function<Void, byte[]>() {
            @Nullable
            @Override
            public byte[] apply(@Nullable Void aVoid) {
                try {
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(sourceBytes);
                    GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream, BUFFER_SIZE);

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(BUFFER_SIZE);
                    byte[] bytes = new byte[BUFFER_SIZE];
                    int i;
                    while((i = gzipInputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, i);
                    }

                    gzipInputStream.close();
                    outputStream.flush();

                    return outputStream.toByteArray();
                } catch(Exception e) {
                    logger.warn("在进行数据解压时失败:" + e.getMessage(), e);
                    return sourceBytes;
                }
            }
        }, logger, "解压数据长度:{},花费时间:{}", new Object[] {sourceBytes.length, null});

    }
}
