package com.github.yanghyu.isid.common.lang.algorithm.concurrent.artbook.chapter05;

import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class LockUseCaseTest {

    Lock lock = new ReentrantLock();

    @Test
    public void test() {
        lock.lock();
        try {
            System.out.println("xx");
        } finally {
            lock.unlock();
        }
    }

}