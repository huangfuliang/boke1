package com.thread;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by huangfuliang on 2017/12/13.
 */
public class ReenterLockDemo {
    public static void main(String[] args) throws InterruptedException {
        ReentrantLock reentrantLock = new ReentrantLock();
        final int[] i = {0};
        Thread thread1 = new Thread(() -> {
            reentrantLock.lock();
            try {
                for (int j =0; j < 100000; j++){
                    i[0]++;
                }
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                reentrantLock.unlock();
            }


        });

        Thread thread2 = new Thread(() -> {
            reentrantLock.lock();
            try {
                for (int j =0; j < 100000; j++){
                    i[0]++;
                }
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                reentrantLock.unlock();
            }
        });

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println(i[0]);
    }

}
