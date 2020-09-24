package com.lw.zookeeper.lock;

/**
 * @description:
 * @author: liuwei
 * @create: 2019-10-23
 **/
public interface Lock {
    void getLock();

    void unLock();
}
