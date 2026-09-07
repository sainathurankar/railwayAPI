package com.railway.railwayAPI.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Availablity {
    private String quota;
    private String className;
    private String status;
    private String seats;
    private String fare;
    private String lastUpdatedOn;
    private Long lastUpdatedOnRaw;
    private String availablityDate;
    private String availablityType;
    private boolean isTG;
    private String reasonType;

    // --- Confirmation prediction (Feature 1) ---
    private Integer predictionPercentage;        // overall confirm chance %
    private Integer racCnfPredictionPercentage;  // RAC -> CNF chance %
    private Double lbPredictionPercentage;        // lower-berth probability (0..1)
    private String lbPredictionData;              // e.g. "22% lower berth chance"

    // --- Fare transparency (Feature 2) ---
    private Integer originalFare;                 // pre-discount fare
    private Integer fareDifference;               // savings vs originalFare

    // --- Boarding / dropping override (Feature 7) ---
    private String frmStnName;
    private String frmStnCode;
    private String frmStnDepartureTime;
    private String frmStnDepartureDate;
    private String toStnName;
    private String toStnCode;
    private String toStnArrivalTime;
    private String toStnArrivalDate;

    // --- Extra classification / Tatkal-guarantee context ---
    private String classType;
    private String currentBkgFlag;
    private String wlType;
    private Integer tgType;
    private Integer tgPremiumPercentage;
    private Boolean isRacSg;
}
