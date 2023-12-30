package com.railway.railwayAPI.service;

import com.railway.railwayAPI.model.Availablity;
import com.railway.railwayAPI.model.internal.TrainUpdateInput;

import java.util.List;

public interface SearchServiceV2 {
    List<Availablity> getAvailabilityNearByDays(TrainUpdateInput trainUpdateInput);
}
