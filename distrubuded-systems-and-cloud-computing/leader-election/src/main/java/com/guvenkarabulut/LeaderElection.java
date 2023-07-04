package com.guvenkarabulut;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;


public class LeaderElection implements Watcher {
    private static final String ZOOKEEPER_ADDRESS="localhost:2181";
    private static final String ELECTION_NAMESPACE="/election";

    private static final int SESSION_TIMEOUT=3000;
    private ZooKeeper zooKeeper;
    private String currentZnodeName;
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        LeaderElection leaderEelction = new LeaderElection();
        leaderEelction.connectToZookeeper();
        leaderEelction.volunteerForLeaderShip();
        leaderEelction.electLeader();
        leaderEelction.run();
        leaderEelction.close();
        System.out.println("Disconnected from Zookeeper");
    }

    public void connectToZookeeper() throws IOException {
        this.zooKeeper=new ZooKeeper(ZOOKEEPER_ADDRESS,SESSION_TIMEOUT,this);
    }
    public void volunteerForLeaderShip() throws InterruptedException, KeeperException {
        String znodePrefix = ELECTION_NAMESPACE + "/c_";
        String znodeFullPath=zooKeeper.create(znodePrefix,new byte[]{},ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("znode name "+znodeFullPath);
        this.currentZnodeName=znodeFullPath.replace(ELECTION_NAMESPACE+"/","");
    }
    public void electLeader() throws InterruptedException, KeeperException {
        List<String> children= zooKeeper.getChildren(ELECTION_NAMESPACE,false);

        Collections.sort(children);
        String smallestChild=children.get(0);

        if (smallestChild.equals(currentZnodeName)){
            System.out.println("I am the leader");
            return;
        }
        System.out.println("I am not the leader, " + smallestChild + "is the leader");
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
