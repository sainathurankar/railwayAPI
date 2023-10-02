package com.railway.railwayAPI.model.internal;

public class TrainUpdateInput {
    private String quota;
    private String destination;
    private String source;

    public TrainUpdateInput() {
    }

    public TrainUpdateInput(String quota, String destination, String source, String trainNumber, String aClass, String doj) {
        this.quota = quota;
        this.destination = destination;
        this.source = source;
        this.trainNumber = trainNumber;
        Class = aClass;
        this.doj = doj;
    }

    private String trainNumber;
    private String Class;
    private String doj;

    public String getQuota() {
        return quota;
    }

    public void setQuota(String quota) {
        this.quota = quota;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getclass() {
        return Class;
    }

    public void setClass(String aClass) {
        Class = aClass;
    }

    public String getDoj() {
        return doj;
    }

    public void setDoj(String doj) {
        this.doj = doj;
    }
}