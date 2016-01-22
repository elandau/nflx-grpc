package com.netflix.grpc;

import java.util.concurrent.atomic.AtomicInteger;

import io.grpc.ForwardingServerCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

public class BackPressureServerInterceptor implements ServerInterceptor {
    private static final AtomicInteger pendingRequests = new AtomicInteger();
    private static final int maxPendingRequests = 2;
    
    @Override
    public <ReqT, RespT> Listener<ReqT> interceptCall(
            MethodDescriptor<ReqT, RespT> method, ServerCall<RespT> call,
            Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        System.out.println();
        return next.startCall(method, new ForwardingServerCall.SimpleForwardingServerCall<RespT>(call){
            @Override
            public void request(int numMessages) {
                System.out.println("server : request() " + numMessages);
                delegate().request(numMessages);
            }
        }, headers);
    }

}
