package com.esri.natmoapp.model;

public class APIErrorResponse {

    private String httpStatus;

    private String timestamp;

    private String message;

    private String traceIdentifier;

    public String getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(String httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTraceIdentifier() {
        return traceIdentifier;
    }

    public void setTraceIdentifier(String traceIdentifier) {
        this.traceIdentifier = traceIdentifier;
    }
}
