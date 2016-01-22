package com.netflix.grpc;

import java.util.concurrent.atomic.AtomicInteger;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.Metadata;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.MethodDescriptor;
import io.grpc.Status;

public class BackPressureClientInterceptor implements ClientInterceptor {
    private final AtomicInteger pendingRequests = new AtomicInteger(0);
    private final int maxPendingRequests = 100000;
    
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method, CallOptions callOptions,
            Channel next) {
        if (pendingRequests.incrementAndGet() > maxPendingRequests) {
            pendingRequests.decrementAndGet();
            return new ThrottledClientCall<ReqT, RespT>();
        }
        else {
            return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
                @Override
                public void start(Listener<RespT> responseListener, Metadata headers) {
                    super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                        @Override
                        public void onHeaders(Metadata headers) {
                            super.onHeaders(headers);
                        }
                        
                        public void onClose(Status status, Metadata trailers) {
                            System.out.println("client : onClose : " + status);
                            pendingRequests.decrementAndGet();
                            super.onClose(status, trailers);
                        }

                    }, headers);
                }
    
                @Override
                public void request(int numMessages) {
                    System.out.println("**** client : request() " + numMessages);
//                    super.request(numMessages);
                }
            };
        }
    }

}
