/** Created by Jack Chen at 10/15/2014 */
package com.ecnu.trivia.common.component.pool;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/** @author Jack Chen */
class ExtBlockingQueue extends AbstractQueue<Runnable> implements BlockingQueue<Runnable> {
    private static final int INITIAL_CAPACITY = 16;
    private ExtFutureTask[] queue = new ExtFutureTask[INITIAL_CAPACITY];
    private final ReentrantLock lock = new ReentrantLock();
    private int size = 0;

    private Thread leader = null;

    private final Condition available = lock.newCondition();

    /**
     * Sift element added at bottom up to its heap-ordered spot.
     * Call only when holding lock.
     */
    private void siftUp(int k, ExtFutureTask key) {
        while(k > 0) {
            int parent = (k - 1) >>> 1;
            ExtFutureTask e = queue[parent];
            if(key.compareTo(e) >= 0) {
                break;
            }
            queue[k] = e;
            k = parent;
        }
        queue[k] = key;
    }

    /**
     * Sift element added at top down to its heap-ordered spot.
     * Call only when holding lock.
     */
    private void siftDown(int k, ExtFutureTask key) {
        int half = size >>> 1;
        while(k < half) {
            int child = (k << 1) + 1;
            ExtFutureTask c = queue[child];
            int right = child + 1;
            if(right < size && c.compareTo(queue[right]) > 0) {
                c = queue[child = right];
            }
            if(key.compareTo(c) <= 0) {
                break;
            }
            queue[k] = c;
            k = child;
        }
        queue[k] = key;
    }

    /**
     * Resize the heap array.  Call only when holding lock.
     */
    private void grow() {
        int oldCapacity = queue.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1); // grow 50%
        if(newCapacity < 0) // overflow
        {
            newCapacity = Integer.MAX_VALUE;
        }
        queue = Arrays.copyOf(queue, newCapacity);
    }

    /**
     * Find index of given object, or -1 if absent
     */
    private int indexOf(Object x) {
        if(x != null) {
            for(int i = 0;i < size;i++) {
                if (x.equals(queue[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public boolean contains(Object x) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return indexOf(x) != -1;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean remove(Object x) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int i = indexOf(x);
            if(i < 0) {
                return false;
            }

            int s = --size;
            ExtFutureTask replacement = queue[s];
            queue[s] = null;
            if(s != i) {
                siftDown(i, replacement);
                if(queue[i] == replacement) {
                    siftUp(i, replacement);
                }
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return size;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public ExtFutureTask peek() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return queue[0];
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean offer(Runnable x) {
        if(x == null) {
            throw new NullPointerException();
        }
        ExtFutureTask e = (ExtFutureTask) x;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int i = size;
            if(i >= queue.length) {
                grow();
            }
            size = i + 1;
            if(i == 0) {
                queue[0] = e;
            } else {
                siftUp(i, e);
            }
            if(queue[0] == e) {
                leader = null;
                available.signal();
            }
        } finally {
            lock.unlock();
        }
        return true;
    }

    @Override
    public void put(Runnable e) {
        offer(e);
    }

    @Override
    public boolean add(Runnable e) {
        return offer(e);
    }

    @Override
    public boolean offer(Runnable e, long timeout, TimeUnit unit) {
        return offer(e);
    }

    /**
     * Performs common bookkeeping for poll and take: Replaces
     * first element with last and sifts it down.  Call only when
     * holding lock.
     *
     * @param f the task to remove and return
     */
    private ExtFutureTask finishPoll(ExtFutureTask f) {
        int s = --size;
        ExtFutureTask x = queue[s];
        queue[s] = null;
        if(s != 0) {
            siftDown(0, x);
        }
        return f;
    }

    @Override
    public ExtFutureTask poll() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            ExtFutureTask first = queue[0];
            if(first == null || first.getDelay(TimeUnit.NANOSECONDS) > 0) {
                return null;
            } else {
                return finishPoll(first);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public ExtFutureTask take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            for(;;) {
                ExtFutureTask first = queue[0];
                if(first == null) {
                    available.await();
                } else {
                    long delay = first.getDelay(TimeUnit.NANOSECONDS);
                    if(delay <= 0) {
                        return finishPoll(first);
                    } else if(leader != null) {
                        available.await();
                    } else {
                        Thread thisThread = Thread.currentThread();
                        leader = thisThread;
                        try {
                            available.awaitNanos(delay);
                        } finally {
                            if(leader == thisThread) {
                                leader = null;
                            }
                        }
                    }
                }
            }
        } finally {
            if(leader == null && queue[0] != null) {
                available.signal();
            }
            lock.unlock();
        }
    }

    @Override
    public ExtFutureTask poll(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            for(;;) {
                ExtFutureTask first = queue[0];
                if(first == null) {
                    if(nanos <= 0) {
                        return null;
                    } else {
                        nanos = available.awaitNanos(nanos);
                    }
                } else {
                    long delay = first.getDelay(TimeUnit.NANOSECONDS);
                    if(delay <= 0) {
                        return finishPoll(first);
                    }
                    if(nanos <= 0) {
                        return null;
                    }
                    if(nanos < delay || leader != null) {
                        nanos = available.awaitNanos(nanos);
                    } else {
                        Thread thisThread = Thread.currentThread();
                        leader = thisThread;
                        try {
                            long timeLeft = available.awaitNanos(delay);
                            nanos -= delay - timeLeft;
                        } finally {
                            if(leader == thisThread) {
                                leader = null;
                            }
                        }
                    }
                }
            }
        } finally {
            if(leader == null && queue[0] != null) {
                available.signal();
            }
            lock.unlock();
        }
    }

    @Override
    public void clear() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            for(int i = 0;i < size;i++) {
                ExtFutureTask t = queue[i];
                if(t != null) {
                    queue[i] = null;
                }
            }
            size = 0;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Return and remove first element only if it is expired.
     * Used only by drainTo.  Call only when holding lock.
     */
    private ExtFutureTask pollExpired() {
        ExtFutureTask first = queue[0];
        if(first == null || first.getDelay(TimeUnit.NANOSECONDS) > 0) {
            return null;
        }
        return finishPoll(first);
    }

    @Override
    public int drainTo(Collection<? super Runnable> c) {
        if(c == null) {
            throw new NullPointerException();
        }
        if(c == this) {
            throw new IllegalArgumentException();
        }
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            ExtFutureTask first;
            int n = 0;
            while((first = pollExpired()) != null) {
                c.add(first);
                ++n;
            }
            return n;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int drainTo(Collection<? super Runnable> c, int maxElements) {
        if(c == null) {
            throw new NullPointerException();
        }
        if(c == this) {
            throw new IllegalArgumentException();
        }
        if(maxElements <= 0) {
            return 0;
        }
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            ExtFutureTask first;
            int n = 0;
            while(n < maxElements && (first = pollExpired()) != null) {
                c.add(first);
                ++n;
            }
            return n;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Object[] toArray() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return Arrays.copyOf(queue, size, Object[].class);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if(a.length < size) {
                return (T[]) Arrays.copyOf(queue, size, a.getClass());
            }
            System.arraycopy(queue, 0, a, 0, size);
            if(a.length > size) {
                a[size] = null;
            }
            return a;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Iterator<Runnable> iterator() {
        return new Itr(Arrays.copyOf(queue, size));
    }

    /**
     * Snapshot iterator that works off copy of underlying q array.
     */
    private class Itr implements Iterator<Runnable> {
        final ExtFutureTask[] array;
        int cursor = 0;     // index of next element to return
        int lastRet = -1;   // index of last element, or -1 if no such

        Itr(ExtFutureTask[] array) {
            this.array = array;
        }

        @Override
        public boolean hasNext() {
            return cursor < array.length;
        }

        @Override
        public Runnable next() {
            if(cursor >= array.length) {
                throw new NoSuchElementException();
            }
            lastRet = cursor;
            return array[cursor++];
        }

        @Override
        public void remove() {
            if(lastRet < 0) {
                throw new IllegalStateException();
            }
            ExtBlockingQueue.this.remove(array[lastRet]);
            lastRet = -1;
        }
    }

}
