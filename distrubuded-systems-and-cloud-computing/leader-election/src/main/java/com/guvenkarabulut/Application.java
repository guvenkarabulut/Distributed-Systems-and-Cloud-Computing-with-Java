package com.guvenkarabulut;

import com.guvenkarabulut.management.LeaderElection;
import com.guvenkarabulut.management.ServiceRegistery;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class Application implements Watcher {
    private static final String ZOOKEEPER_ADDRESS="localhost:2181";
    private static final int SESSION_TIMEOUT=3000;
    private static final int DEFAULT_PORT = 8080;
    private ZooKeeper zooKeeper;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        int currentServerPort=args.length==1?Integer.parseInt(args[0]):DEFAULT_PORT;
        Application application = new Application();
        ZooKeeper zooKeeper = application.connectToZookeeper();

        ServiceRegistery serviceRegistery = new ServiceRegistery(zooKeeper);

        OnElectionAction onElectionAction=new OnElectionAction(serviceRegistery,currentServerPort);

        LeaderElection leaderElection= new LeaderElection(zooKeeper,onElectionAction);

        leaderElection.volunteerForLeaderShip();
        leaderElection.reelectLeader();

        application.run();
        application.close();
        System.out.println("Disconnected from Zookeeper");
    }
    public ZooKeeper connectToZookeeper() throws IOException {
        this.zooKeeper=new ZooKeeper(ZOOKEEPER_ADDRESS,SESSION_TIMEOUT,this);
        return zooKeeper;
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
        }
    }
}
