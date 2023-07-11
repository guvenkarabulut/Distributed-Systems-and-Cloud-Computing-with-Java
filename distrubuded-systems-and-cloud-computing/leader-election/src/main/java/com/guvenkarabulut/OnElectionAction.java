package com.guvenkarabulut;

import com.guvenkarabulut.management.OnElectionCallBack;
import com.guvenkarabulut.management.ServiceRegistery;
import org.apache.zookeeper.KeeperException;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class OnElectionAction implements OnElectionCallBack {
    private final ServiceRegistery serviceRegistery;
    private final int port;

    public OnElectionAction(ServiceRegistery serviceRegistery, int port) {
        this.serviceRegistery = serviceRegistery;
        this.port = port;
    }

    @Override
    public void onElectToBeLeader() throws InterruptedException, KeeperException {
        serviceRegistery.unregisterFromCluster();
        serviceRegistery.registerForUpdates();
    }

    @Override
    public void onWorker() {
        try {
            String currentServerAdress= String.format("http://%s:%d", InetAddress.getLocalHost().getCanonicalHostName(),port);
            serviceRegistery.registerToCluster(currentServerAdress);
        } catch (UnknownHostException | InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }
}
