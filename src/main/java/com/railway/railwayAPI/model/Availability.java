package com.railway.railwayAPI.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Availability {
    private String quota;
    private String className;
    private String status;
    private String seats;
    private String fare;
    private String lastUpdatedOn;
    private Long lastUpdatedOnRaw;
    private String availabilityDate;
}
