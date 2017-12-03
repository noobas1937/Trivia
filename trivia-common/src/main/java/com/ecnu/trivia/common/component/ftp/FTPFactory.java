/** Created by shilian.peng at 16/6/21 */
package com.ecnu.trivia.common.component.ftp;

import com.ecnu.trivia.common.exception.IRCloudException;
import com.ecnu.trivia.common.util.ObjectUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 连接池中使用到的工厂类，实现了创建、销毁、校验的功能。
 *
 * @author shilian.peng
 */
@Component("ftpFactory")
public class FTPFactory extends BasePooledObjectFactory<FTPClient> {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private FTPConfig config;

    public FTPFactory() {
    }

    @Override
    public FTPClient create() throws Exception {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(config.getHost(), config.getPort());
        ftpClient.setBufferSize(config.getBufferSize());
        ftpClient.setDataTimeout(config.getDataTimeout());
        if(!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            ftpClient.disconnect();
            logger.error("FTP服务器[" + config.getHost() + "]拒绝连接。");
        }
        ftpClient.login(config.getUserName(), config.getPassword());
        if(!ObjectUtils.isNullOrEmpty(config.getPath())) {
            ftpClient.changeWorkingDirectory(config.getPath());
        }
        // 设置ftpClient的默认FileType
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        return ftpClient;
    }

    @Override
    public PooledObject<FTPClient> wrap(FTPClient obj) {
        return new DefaultPooledObject<>(obj);
    }

    @Override
    public void destroyObject(PooledObject<FTPClient> pooledObject) throws Exception {
        FTPClient ftpClient = pooledObject.getObject();
        try{
            if(ftpClient != null && ftpClient.isConnected()) {
                ftpClient.logout();
            }
        } finally {
            // 注意,一定要在finally代码中断开连接，否则会导致占用ftp连接情况
            if(ftpClient != null) {
                ftpClient.disconnect();
            }
        }
    }

    @Override
    public boolean validateObject(PooledObject<FTPClient> p) {
        try{
            return p.getObject().sendNoOp();
        } catch(IOException e) {
            logger.error("error", e);
            throw new IRCloudException("Failed to validate client: " + e, e);
        }
    }

    @Override
    public void activateObject(PooledObject<FTPClient> p) throws Exception {

    }
}
