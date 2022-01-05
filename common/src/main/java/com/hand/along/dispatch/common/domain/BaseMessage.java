package com.hand.along.dispatch.common.domain;

public class BaseMessage {
    private String messageType;

    public BaseMessage(String messageType) {
        this.messageType = messageType;
    }

    public BaseMessage() {
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
