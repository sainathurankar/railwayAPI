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
    private List<Availablity> availabilitiesList;
}

