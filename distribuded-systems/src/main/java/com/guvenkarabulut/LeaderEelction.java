package com.guvenkarabulut;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;


public class LeaderEelction implements Watcher {
    private static final String ZOOKEEPER_ADDRESS="localhost:2181";
    private static final int SESSION_TIMEOUT=3000;
    private ZooKeeper zooKeeper;

    public void connectToZookeeper() throws IOException {
        this.zooKeeper=new ZooKeeper(ZOOKEEPER_ADDRESS,SESSION_TIMEOUT,this);
    }

    public void run() throws InterruptedException{
        synchronized (zooKeeper){
            zooKeeper.wait();
        }
    }

    public void close()throws InterruptedException{
        zooKeeper.close();
    }
    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getState()==Event.KeeperState.SyncConnected){
            System.out.println("Successfully connected ZooKeeper");
        }else {
            synchronized (zooKeeper){
                zooKeeper.notifyAll();
            }
        }
    }
}
