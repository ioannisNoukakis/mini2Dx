package org.mini2Dx.core.collections.concurrent;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class ConcurrentObjectIntMapTest extends ConcurrentCollectionTest {
    @Override
    public void testGetLock() {
        assertNotNull(new ConcurrentObjectIntMap<>().getLock());
    }

    @Test
    public void testPutItems() {
        ConcurrentObjectIntMap<Integer> m = new ConcurrentObjectIntMap<>();
        Random r = new Random();
        AtomicInteger count = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(100);
        createStartAndJoinThreads(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 1000; i++) {
                    latch.countDown();
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    int key = r.nextInt();
                    int value = r.nextInt();
                    m.getLock().lockWrite();
                    if (!m.containsKey(key)){
                        m.put(key, value);
                        count.incrementAndGet();
                    }
                    m.getLock().unlockWrite();
                }
            }
        }, 100);
        assertEquals(count.get(), m.size);
    }

    @Test
    public void testRemoveItems() {
        ConcurrentObjectIntMap<Integer> m = new ConcurrentObjectIntMap<>();
        for (int i = 0; i < 10000; i++) {
            m.put(i, 2*i);
        }
        CountDownLatch latch = new CountDownLatch(1000);
        Thread[] kThreads = createAndStartThreads(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                for (int i = 0; i < 10; i++) {
                    m.getLock().lockWrite();
                    int key = m.keys().next();
                    assertTrue(m.containsKey(key));
                    m.remove(key, 0);
                    assertFalse(m.containsKey(key));
                    m.getLock().unlockWrite();
                }
            }
        }, 900);
        Thread[] vThreads = createAndStartThreads(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                for (int i = 0; i < 10; i++) {
                    m.getLock().lockWrite();
                    int value = m.values().next();
                    assertTrue(m.containsValue(value));
                    m.remove(m.findKey(value), 0);
                    assertFalse(m.containsValue(value));
                    m.getLock().unlockWrite();
                }
            }
        }, 100);
        joinAll(kThreads, vThreads);
        assertEquals(0, m.size);
    }
}
