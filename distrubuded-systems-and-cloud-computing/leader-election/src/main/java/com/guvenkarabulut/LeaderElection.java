package com.guvenkarabulut;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

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
        leaderEelction.reelectLeader();
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
    public void reelectLeader() throws InterruptedException, KeeperException {
        Stat predecessorStat = null;
        String predecessorZnodeName = "";
        while (predecessorStat == null) {
            List<String> children = zooKeeper.getChildren(ELECTION_NAMESPACE, false);
            Collections.sort(children);
            String smallestChild = children.get(0);

            if (smallestChild.equals(currentZnodeName)) {
                System.out.println("I am the leader");
                return;
            } else {
                System.out.println("I am not the leader");
                int predecessorIndex = Collections.binarySearch(children, currentZnodeName) - 1;
                predecessorZnodeName = children.get(predecessorIndex);
                predecessorStat = zooKeeper.exists(ELECTION_NAMESPACE + "/" + predecessorZnodeName, this);
            }
        }
        System.out.println("Watching znode, " + predecessorZnodeName);

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
        switch (watchedEvent.getType()) {
            case None -> {
                switch (watchedEvent.getState()) {
                    case SyncConnected -> System.out.println("Successfully connected ZooKeeper");
                    case Expired -> System.out.println("Session expired");
                    case Disconnected -> System.out.println("Disconnected from ZooKeeper");
                    case AuthFailed -> System.out.println("Authentication failed");
                    case SaslAuthenticated -> System.out.println("SASL authenticated");
                    case ConnectedReadOnly -> System.out.println("Connected as read-only");
                    default -> System.out.println("Unknown state: " + watchedEvent.getState());
                }
            }
            case NodeDeleted -> {
                try {
                    reelectLeader();
                } catch (InterruptedException e) {
                } catch (KeeperException e) {
                }
            }

        }

    }
}
