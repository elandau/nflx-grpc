package com.netflix.grpc;

import io.grpc.ClientInterceptors;
import io.grpc.ServerInterceptors;
import io.grpc.internal.ServerImpl;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.netflix.karyon.helloworld.GreeterGrpc;
import com.netflix.karyon.helloworld.GreeterGrpc.GreeterStub;
import com.netflix.karyon.helloworld.HelloRequest;
import com.netflix.karyon.helloworld.HelloResponse;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerImpl server = NettyServerBuilder
            .forPort(8080)
            .addService(ServerInterceptors.intercept(GreeterGrpc.bindService(new GreeterImpl()), new BackPressureServerInterceptor()))
            .flowControlWindow(10)
            .build()
            .start();
        
        
        GreeterStub client = GreeterGrpc.newStub(
                    NettyChannelBuilder
                        .forAddress("localhost", 8080)
                        .negotiationType(NegotiationType.PLAINTEXT)
                        .flowControlWindow(10)
    //                    .intercept(new BackPressureClientInterceptor())
                        .build());

        for (int i = 0; i < 100; i++) {
            TimeUnit.MILLISECONDS.sleep(100);
            final String name = "request-" + i;
            System.out.println("Sending : " + name);
            client.sayHello(HelloRequest.newBuilder().setName(name).build(), new StreamObserver<HelloResponse>() {
                @Override
                public void onCompleted() {
                    System.out.println("send: " + name + " complete");
                }
    
                @Override
                public void onError(Throwable t) {
                    System.out.println("send: " + name + " error " + t.getMessage());
                }
    
                @Override
                public void onNext(HelloResponse value) {
                    System.out.println("send: " + name + " response");
                }
            });
        }
        
        TimeUnit.SECONDS.sleep(1000);
    }
}
