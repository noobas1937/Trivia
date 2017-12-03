package com.ecnu.trivia.common.component.serialize;/**
 * Created by shuyun on 2016/11/2.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by Jack Chen at 2016/11/2
 *
 * @author Jack Chen
 **/
public class SerializeUtils {

    private static Logger logger = LoggerFactory.getLogger(SerializeUtils.class);

    public static byte[] serialize(Object obj) {
        byte[] bytes = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ;
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            bytes = baos.toByteArray();
            baos.close();
            oos.close();
        } catch (IOException e) {
            logger.error("", e);
        }
        return bytes;
    }

    public static Object deSerialize(byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            obj = ois.readObject();
        } catch (Exception e) {
            logger.error("", e);
        }
        return obj;
    }
}
