package com.codingPower.framework.net;

public class NetworkException extends Exception {

    private int statusCode;

    public NetworkException(int statusCode) {
        super("statusCode : " + statusCode);
        this.statusCode = statusCode;
    }

    public NetworkException(int statusCode, Throwable throwable) {
        super(throwable);
        this.statusCode = statusCode;
    }

    public NetworkException(String detailMessage) {
        super(detailMessage);
    }

    public NetworkException(Throwable throwable) {
        super(throwable);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

}
