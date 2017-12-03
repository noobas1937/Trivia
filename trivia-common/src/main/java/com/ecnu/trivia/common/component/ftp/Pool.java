package com.ecnu.trivia.common.component.ftp;

import com.ecnu.trivia.common.exception.IRCloudException;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 模仿redis的连接池实现,实现获取，返回，销毁的功能
 * shilian.peng
 */
public abstract class Pool<T> {
    Logger logger = LoggerFactory.getLogger(Pool.class);
    protected GenericObjectPool<T> internalPool;

    public T getResource() {
        try{
            return this.internalPool.borrowObject();
        } catch(Exception e) {
            logger.error("error", e);
            throw new IRCloudException(e.getMessage());
        }
    }

    public void returnResource(T resource) {
        try{
            this.internalPool.returnObject(resource);
        } catch(Exception e) {
            logger.error("error", e);
            throw new IRCloudException(e.getMessage());
        }
    }

    public void destroy() {
        try{
            this.internalPool.close();
        } catch(Exception e) {
            logger.error("error", e);
            throw new IRCloudException(e.getMessage());
        }
    }

}
