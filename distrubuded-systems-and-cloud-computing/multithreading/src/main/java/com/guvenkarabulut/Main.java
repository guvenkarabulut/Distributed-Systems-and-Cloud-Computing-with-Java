package com.guvenkarabulut;

public class Main {
    public static void main(String[] args) {
        for(int i= 0; i<5 ;i++){
            MultiThreadThing multiThreadThing=new MultiThreadThing(i);
            MultiThrendThing2 multiThrendThing2 = new MultiThrendThing2(i);
            Thread myThread = new Thread(multiThrendThing2);
            myThread.start();
            try {
                myThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            multiThreadThing.start();
        }

    }
}