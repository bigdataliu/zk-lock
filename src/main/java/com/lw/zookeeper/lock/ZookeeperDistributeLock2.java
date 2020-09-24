package com.lw.zookeeper.lock;

import org.I0Itec.zkclient.IZkDataListener;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @description:
 * @author: liuwei
 * @create: 2019-10-23
 **/
public class ZookeeperDistributeLock2 extends ZookeeperAbstractLock {
    private CountDownLatch countDownLatch = null;

    //当前请求节点前一个节点
    private String beforePath;
    //当前请求的节点
    private String currentPath;

    public ZookeeperDistributeLock2() {
        if (!this.zkClient.exists(PATH2)) {
            this.zkClient.createPersistent(PATH2);
        }
    }

    @Override
    boolean tryLock() {
        //如果currentPath为空则为第一次尝试加锁，第一次加锁赋值currentPath
        if (currentPath == null || currentPath.length() <= 0) {
            //创建一个临时顺序节点
            currentPath = this.zkClient.createEphemeralSequential(PATH2 + '/', "lock");
        }
        //获取所有临时节点并排序，临时节点名称为自增长的字符串如：0000000400
        List<String> childrens = this.zkClient.getChildren(PATH2);
        Collections.sort(childrens);
        //如果当前节点在所有节点中排名第一则获取锁成功
        if (currentPath.equals(PATH2 + '/' + childrens.get(0))) {
            System.out.println("当前节点："+currentPath);
            return true;
        } else {
            //如果当前节点在所有节点中排名不是第一，则获取前面的节点名称并赋值给beforePath
            int wz = Collections.binarySearch(childrens, currentPath.substring(7));
            beforePath = PATH2 + '/' + childrens.get(wz - 1);

        }

        return false;
    }

    @Override
    boolean waitLock() {
        IZkDataListener iZkDataListener = new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {

            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                    if(countDownLatch!=null){
                        countDownLatch.countDown();
                    }
            }
        };
        //给排在前面的节点增加数据删除的watcher,本质是启动另一个线程去监听前置节点
        this.zkClient.subscribeDataChanges(beforePath,iZkDataListener);
        if(this.zkClient.exists(beforePath)){
            countDownLatch = new CountDownLatch(1);
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.zkClient.unsubscribeDataChanges(beforePath,iZkDataListener);
        return false;
    }

    @Override
    public void unLock() {
        //删除当前临时节点
        zkClient.delete(currentPath);
    }
}
