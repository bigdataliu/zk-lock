package com.lw.zookeeper.lock;

import org.I0Itec.zkclient.ZkClient;

/**
 * @description:
 * @author: liuwei
 * @create: 2019-10-23
 **/
public abstract class ZookeeperAbstractLock implements Lock {
    private static final String CONNECTSTRING = "127.0.0.1:2181";

    //创建连接
    protected ZkClient zkClient = new ZkClient(CONNECTSTRING,60*1000);
    protected static final String PATH2 = "/lock2";

    @Override
    public void getLock(){
        if (tryLock()){
            System.out.println("------获取lock锁的资源------");
        }else {
            //等待
            waitLock();
            //重新获取锁资源
            getLock();
        }
    }

    //获取锁资源
    abstract boolean tryLock();

    //等待
    abstract boolean waitLock();

}
