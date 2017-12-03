/** Created by Jack Chen at 2015/6/29 */
package com.ecnu.trivia.common.component.function;

import com.google.common.base.Function;

/**
 * 针对consumer的工具类
 *
 * @author Jack Chen
 */
public class Consumers {

    /** 实现 consumer的andThen语义 */
    public static <T> Consumer<T> andThen(final Consumer<T> first, final Consumer<T> second) {
        return new Consumer<T>() {
            @Override
            public void accept(T t) {
                first.accept(t);
                second.accept(t);
            }
        };
    }

    /** 从function转换为consumer */
    public static <T> Consumer<T> fromFunction(final Function<T, ?> function) {
        return new Consumer<T>() {
            @Override
            public void accept(T t) {
                function.apply(t);
            }
        };
    }
}
