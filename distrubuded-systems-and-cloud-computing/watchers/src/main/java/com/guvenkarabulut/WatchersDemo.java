package com.guvenkarabulut;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

public class WatchersDemo implements Watcher {
    private static final String ZOOKEEPER_ADDRESS="localhost:2181";
    private static final String ELECTION_NAMESPACE="/election";
    private static final String TARGET_ZNODE="/target_znode";

    private static final int SESSION_TIMEOUT=3000;
    private ZooKeeper zooKeeper;
    private String currentZnodeName;
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        WatchersDemo watchersDemo = new WatchersDemo();
        watchersDemo.connectToZookeeper();
        watchersDemo.run();
        watchersDemo.watchTargetZnode();
        watchersDemo.close();
        System.out.println("Disconnected from Zookeeper");
    }

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
    public void watchTargetZnode() throws InterruptedException, KeeperException {
        Stat stat = zooKeeper.exists(TARGET_ZNODE,this);
        if (stat == null){
            return;
        }
        byte[] data = zooKeeper.getData(TARGET_ZNODE,this,stat);
        List<String> children=zooKeeper.getChildren(TARGET_ZNODE,this);

        System.out.println("Data : "+ new String(data) + "children : "+children);
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
            case NodeDeleted -> System.out.println(TARGET_ZNODE+ " was deleted");
            case NodeCreated -> System.out.println(TARGET_ZNODE+ " was created");
            case NodeDataChanged -> System.out.println(TARGET_ZNODE+ " was data changed");
            case NodeChildrenChanged -> System.out.println(TARGET_ZNODE+ " was children data changed");
        }
        try {
            watchTargetZnode();
        } catch (KeeperException e) {
        } catch (InterruptedException e) {

        }
    }
}