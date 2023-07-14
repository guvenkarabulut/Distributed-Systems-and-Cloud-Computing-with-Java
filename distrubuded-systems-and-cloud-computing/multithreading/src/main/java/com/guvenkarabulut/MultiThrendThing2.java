package com.guvenkarabulut;

public class MultiThrendThing2  implements Runnable{
    private int threadNumber;
    public MultiThrendThing2(int threadNumber){
        this.threadNumber=threadNumber;
    }
    @Override
    public void run(){
        for (int i=1;i<=5;i++){
            System.out.println(i + "from MultiThreadThing2 ThreadNumber: "+this.threadNumber + "**************************");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}