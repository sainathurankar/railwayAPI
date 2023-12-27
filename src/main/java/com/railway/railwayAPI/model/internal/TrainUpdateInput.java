package com.railway.railwayAPI.model.internal;

import lombok.Builder;

@Builder
public class TrainUpdateInput {
    private String quota;
    private String destination;
    private String source;
    private String trainNumber;
    private String Class;
    private String doj;
    private int numberOfDays = 6;

    public int getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(int numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public TrainUpdateInput() {
    }
    public TrainUpdateInput(String quota, String destination, String source, String trainNumber, String aClass, String doj) {
        this.quota = quota;
        this.destination = destination;
        this.source = source;
        this.trainNumber = trainNumber;
        this.Class = aClass;
        this.doj = doj;
    }

    public TrainUpdateInput(String quota, String destination, String source, String trainNumber, String aClass, String doj, int numberOfDays) {
        this.quota = quota;
        this.destination = destination;
        this.source = source;
        this.trainNumber = trainNumber;
        this.Class = aClass;
        this.doj = doj;
        this.numberOfDays = numberOfDays;
    }

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
