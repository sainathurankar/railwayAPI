package com.railway.railwayAPI.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Train {
    private String trainName;
    private String trainNumber;
    private String departureTime;
    private String arrivalTime;
    private String duration;
    private String departureDate;
    private String arrivalDate;
    private List<Availablity> availabilitiesList;
    private String fromStation;
    private String toStation;
    private String fromStationCode;
    private String toStationCode;
    private List<String> availableClasses;
    private Boolean isAlternate;
    private String runningMon;
    private String runningTue;
    private String runningWed;
    private String runningThu;
    private String runningFri;
    private String runningSat;
    private String runningSun;
}

