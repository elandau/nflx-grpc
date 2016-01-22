package com.netflix.grpc;

import io.grpc.ClientCall;
import io.grpc.Metadata;
import io.grpc.Status;

public class ThrottledClientCall<ReqT, RespT> extends ClientCall<ReqT, RespT> {
    @Override
    public void cancel() {
    }

    @Override
    public void halfClose() {
    }

    @Override
    public void request(int numMessages) {
    }

    @Override
    public void sendMessage(ReqT message) {
    }

    @Override
    public void start(
            io.grpc.ClientCall.Listener<RespT> responseListener,
            Metadata headers) {
        responseListener.onClose(Status.UNAVAILABLE, null);
    }
}
