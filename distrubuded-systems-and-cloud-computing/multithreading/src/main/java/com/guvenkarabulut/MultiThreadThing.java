package com.guvenkarabulut;

public class MultiThreadThing extends Thread{
    private int threadNumber;
    public MultiThreadThing(int threadNumber){
        this.threadNumber=threadNumber;
    }
    @Override
    public void run(){
        for (int i=1;i<=5;i++){
            System.out.println(i + "from MultiThreadThing ThreadNumber: "+this.threadNumber);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
