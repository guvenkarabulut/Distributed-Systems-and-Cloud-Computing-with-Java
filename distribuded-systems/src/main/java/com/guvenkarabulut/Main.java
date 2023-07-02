package com.guvenkarabulut;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        LeaderEelction leaderEelction = new LeaderEelction();
        leaderEelction.connectToZookeeper();
        leaderEelction.run();
        leaderEelction.close();
        System.out.println("Disconnected from Zookeeper");
    }
}