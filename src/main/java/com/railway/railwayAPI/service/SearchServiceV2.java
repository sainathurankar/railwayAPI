package com.railway.railwayAPI.service;

import com.railway.railwayAPI.model.Availability;
import com.railway.railwayAPI.model.internal.TrainUpdateInput;

import java.util.List;

public interface SearchServiceV2 {
    List<Availability> getAvailabilityNearByDays(TrainUpdateInput trainUpdateInput);
}
