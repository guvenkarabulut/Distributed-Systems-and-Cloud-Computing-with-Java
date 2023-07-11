package com.guvenkarabulut.management;

import org.apache.zookeeper.KeeperException;

public interface OnElectionCallBack {
    void onElectToBeLeader() throws InterruptedException, KeeperException;
    void onWorker();

}
