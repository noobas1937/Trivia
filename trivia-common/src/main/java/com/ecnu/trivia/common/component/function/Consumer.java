/** Created by Jack Chen at 2015/6/29 */
package com.ecnu.trivia.common.component.function;

/**
 * copy自java 8,消费处理
 *
 * @author Jack Chen
 */
public interface Consumer<T> {

    void accept(T t);
}
