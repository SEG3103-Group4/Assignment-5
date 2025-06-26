package com.easymock;

public interface EmailGateway {
    boolean isEmailQueueEmpty();
    void sendAsync(String userName, String message, long timeoutMs) throws EmailException;
}