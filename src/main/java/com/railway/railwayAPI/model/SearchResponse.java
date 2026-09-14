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
public class SearchResponse {
    private String errorCode;
    private String detailedMsg;
    private ResponseStatus status;
    private List<String> trainList;
    private List<Train> trains;

    // --- Recommended train (Feature 5) ---
    private Train recommendation;
    private List<String> recommendationTags;

    // --- Offers strip (Feature 8) ---
    private List<Object> offers;

    // --- Route confidence (redBus compositeAvailability, 0..1 as string) ---
    private String compositeAvailability;

    // --- Server-driven catalog passthrough (Feature 3 / 4) ---
    private Object filters;
    private Object sort;
    private Object quotas;
}
