package com.guvenkarabulut;

import com.guvenkarabulut.networking.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Aggregator {
    private WebClient webClient;

    public Aggregator() {
        this.webClient = new WebClient();
    }

    public List<String> sendTaskToWorkers(List<String> workerAdresses,List<String> tasks){
        CompletableFuture<String>[] futures=new CompletableFuture[workerAdresses.size()];
        for (int i=0;i<workerAdresses.size();i++){
            String workerAdress=workerAdresses.get(i);
            String task=tasks.get(i);

            byte[] requestPayload=task.getBytes();

            futures[i]=webClient.sendTask(workerAdress,requestPayload);
        }
        return Stream.of(futures).map(CompletableFuture::join).collect(Collectors.toList());
    }
}
