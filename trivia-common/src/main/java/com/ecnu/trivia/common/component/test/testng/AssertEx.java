/**
 * Created by Jack Chen at 2014/5/5
 */
package com.ecnu.trivia.common.component.test.testng;

import org.testng.Assert;
import org.testng.collections.Lists;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/** @author Jack Chen */
public class AssertEx extends Assert {
    /** 判断2个集合是否是无序相等的 */
    @SuppressWarnings("all")
    public static void assertEqualsNoOrder(Collection<?> actual, Collection<?> expected) {
        if(actual == expected) {
            return;
        }

        if(actual == null || expected == null) {
            fail("Collections not equal: expected: " + expected + " and actual: " + actual);
        }

        assertEquals(actual.size(), expected.size(), " lists don't have the same size");

        List<?> actual2 = Lists.newArrayList(actual);
        actual2.removeAll(expected);
        if(actual2.size() != 0) {
            fail("Collection not equal: " + expected + " and " + actual);
        }

    }

    /** 用于判断2个集合在语义上是否是相同的 */
    public static <A, B> void assertEqualsNoOrder(Collection<A> actual, Collection<B> expected, Equivalence<A, B> equivalence) {
        if(actual == null || expected == null) {
            fail("Collections not equal: expected: " + expected + " and actual: " + actual);
        }
        assertEquals(actual.size(), expected.size(), " lists don't have the same size");

        final Equivalence<A, B> comparator = equivalence;
        Collection<A> removed = expected.stream().map(b -> actual.stream().filter(a -> comparator.equals(a, b)).findFirst().get())
                .collect(Collectors.toList());
        assertTrue(removed.size() == actual.size(), "Collection not equal: " + expected + " and " + actual);
        List<A> actual2 = Lists.newArrayList(actual);

        actual2.removeAll(removed);
        assertTrue(actual2.size() == 0, "Collection not equal: " + expected + " and " + actual);
    }

    /** 用于判断实际的集合是否包括预期的集合信息,在语义上 */
    public static <A, B> void assertContains(Collection<A> actual, Collection<B> expected, Equivalence<A, B> equivalence) {

        //都为Null，两Null不相等
        if(actual == null || expected == null) {
            fail("Collections not equal: expected: " + expected + " and actual: " + actual);
        }

        final Equivalence<A, B> comparator = equivalence;
        Collection<B> removed = Lists.newArrayList(expected);
        //循环子集B
        //拿出B中某个元素，如果在A集合中，能找到之相等的元素，则删除B中的此元素
        expected.stream().filter(b -> actual.stream().anyMatch(input -> comparator.equals(input, b)))
                .forEach(removed::remove);

        if(removed.size() != 0) {
            fail("Collection actual not fully included expected, except:" + removed);
        }
        assertTrue(removed.size() == 0);
    }
}
