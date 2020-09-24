package com.lw.zookeeper.lock;

/**
 * @description:
 * @author: liuwei
 * @create: 2019-10-23
 **/
public class Client implements Runnable {
//    private Lock lock = new ZookeeperDistributeLock2();

    static int data = 0;

    public void getNumber() {
        try {
//            lock.getLock();
            data++;
            System.out.println(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            lock.unLock();
        }
    }

    @Override
    public void run() {
        getNumber();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 20; i++) {
            new Thread(new Client()).start();
        }
    }
}
