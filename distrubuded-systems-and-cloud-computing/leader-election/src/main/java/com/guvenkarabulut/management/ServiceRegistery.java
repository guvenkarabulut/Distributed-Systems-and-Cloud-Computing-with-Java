package com.guvenkarabulut.management;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServiceRegistery implements Watcher {
    private static final String REGISTERY_ZNODE = "/service_registery";
    private final ZooKeeper zooKeeper;
    private String currentZnode;
    private List<String> allServiceAdresses = null;

    public ServiceRegistery (ZooKeeper zooKeeper){
        this.zooKeeper = zooKeeper;
        createServiceRegisteryZnode();
    }

    public void registerToCluster(String metadata) throws InterruptedException, KeeperException {
        this.currentZnode=zooKeeper.create(REGISTERY_ZNODE+"/n_",metadata.getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("Registered this cluster");
    }

    public void registerForUpdates(){
        try {
            updateAdress();
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }

    public synchronized List<String> getAllServiceAdresses() throws InterruptedException, KeeperException {
        if (allServiceAdresses==null){
            updateAdress();
        }
        return allServiceAdresses;
    }
    public void unregisterFromCluster() throws InterruptedException, KeeperException {
        if (currentZnode != null && zooKeeper.exists(currentZnode,false) != null){
            zooKeeper.delete(currentZnode,-1);
        }
    }
    public void createServiceRegisteryZnode() {
        try {
            if (zooKeeper.exists(REGISTERY_ZNODE, false) == null) {
                zooKeeper.create(REGISTERY_ZNODE, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }

    private synchronized void updateAdress() throws InterruptedException, KeeperException {
        List<String> workerZnodes= zooKeeper.getChildren(REGISTERY_ZNODE,this);

        List<String> adresses = new ArrayList<>(workerZnodes.size());
        for (String workerZnode:workerZnodes){
            String workerZnodeFullPath=REGISTERY_ZNODE+"/"+workerZnode;
            Stat stat = zooKeeper.exists(workerZnodeFullPath,false);
            if (stat== null){
                continue;
            }

            byte [] adressesBytes= zooKeeper.getData(workerZnodeFullPath,false,stat);
            String adress=new String(adressesBytes);
            adresses.add(adress);
        }
        this.allServiceAdresses= Collections.unmodifiableList(adresses);
        System.out.println("The cluster addresses are : " + this.allServiceAdresses);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            updateAdress();
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }
}
