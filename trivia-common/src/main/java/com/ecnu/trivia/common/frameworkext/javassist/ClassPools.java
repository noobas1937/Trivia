/** Created by Jack Chen at 12/24/2014 */
package com.ecnu.trivia.common.frameworkext.javassist;

import javassist.ClassClassPath;
import javassist.ClassPool;

/**
 * 获取由javassist提供的类池信息，避免每次重新获取
 *
 * @author Jack Chen
 */
public class ClassPools {

    private static class Inner {
        private static final ClassPool classPool;

        static {
            classPool = ClassPool.getDefault();
            classPool.appendClassPath(new ClassClassPath(ClassPools.class));
        }
    }

    /** 获取默认的类池 */
    public static ClassPool getDefault() {
        return Inner.classPool;
    }
}
