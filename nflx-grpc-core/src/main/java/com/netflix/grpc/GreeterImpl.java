package com.netflix.grpc;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.grpc.stub.StreamObserver;

import com.netflix.karyon.helloworld.GreeterGrpc.Greeter;
import com.netflix.karyon.helloworld.HelloRequest;
import com.netflix.karyon.helloworld.HelloResponse;

public class GreeterImpl implements Greeter{
    
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);
    
    @Override
    public void sayHello(final HelloRequest request, final StreamObserver<HelloResponse> responseObserver) {
        System.out.println("receive: " + request.getName());
//        executor.schedule(new Runnable() {
//            @Override
//            public void run() {
//                responseObserver.onNext(HelloResponse.newBuilder().setMessage("hello " + request.getName()).build());
//                responseObserver.onCompleted();
//            }
//        }, 1, TimeUnit.SECONDS);
    }

    @Override
    public void noHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
    }

    @Override
    public void errorHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
    }

}
