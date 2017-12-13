package com.thread;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by huangfuliang on 2017/12/13.
 * 死锁实例
 */
public class ReentrantLockInterruptedDemo {

    public static void main(String[] args) {

        ReentrantLock lock1 = new ReentrantLock();
        ReentrantLock lock2 = new ReentrantLock();

        new Thread(() -> {
            try {
                lock1.lockInterruptibly();
                Thread.sleep(500);
                lock2.lockInterruptibly();
            } catch (InterruptedException e) {
                System.out.println("thread1 interrupted...");
                e.printStackTrace();
            } finally {
                if (lock1.isHeldByCurrentThread()) {
                    lock1.unlock();
                }
                if (lock2.isHeldByCurrentThread()) {
                    lock2.unlock();
                }
            }
        }, "thread1").start();

        new Thread(() -> {
            try {
                lock2.lockInterruptibly();
                Thread.sleep(100);
                lock1.lockInterruptibly();
            } catch (InterruptedException e) {
                System.out.println("thread2 interrupted...");
                e.printStackTrace();
            } finally {
                if (lock1.isHeldByCurrentThread()) {
                    lock1.unlock();
                }
                if (lock2.isHeldByCurrentThread()) {
                    lock2.unlock();
                }
            }

        }, "thread2").start();
        new ThreadChecker().check();
    }

    /**
     * 死锁检查类
     */
    static class ThreadChecker {
        private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        public void check() {
            Thread checkDealLockThread = new Thread(() -> {
                while (true) {
                    long[] dealLockedThreadIds = threadMXBean.findDeadlockedThreads();
                    if (dealLockedThreadIds != null) {
                        ThreadInfo[] threadInfo = threadMXBean.getThreadInfo(dealLockedThreadIds);
                        for (Thread thread : Thread.getAllStackTraces().keySet()) {
                            for (int i = 0; i < threadInfo.length; i++) {
                                if (thread.getId() == threadInfo[i].getThreadId()) {
                                    System.out.println(String.format("检查到死锁，关闭 name ：%s; id : %s", thread.getName(), thread.getId()));
                                    thread.interrupt();
                                }
                            }
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            checkDealLockThread.setDaemon(true);
            checkDealLockThread.start();
        }

    }
}
