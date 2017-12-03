/** Created by Jack Chen at 5/14/2015 */
package com.ecnu.trivia.common.component.test.testng;

/**
 * 判断2个对象是否是相同的(语义层)
 *
 * @author Jack Chen
 */
public interface Equivalence<A, B> {
    boolean equals(A a, B b);
}
