package com.ecnu.trivia.common.util;

import java.util.Random;

/**
 * Created by Jack Chen at 8/24/16
 *
 * @author Jack Chen
 */
public class RandomUtils {

    private static Random random = new Random();

    /** 生成一个随机6位长整型 */
    public static Long generateRandomNum() {
        int randomInt = random.nextInt(899999)+100000;
        return Long.valueOf(randomInt);
    }
}
