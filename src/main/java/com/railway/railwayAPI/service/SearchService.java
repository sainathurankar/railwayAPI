package com.railway.railwayAPI.service;

import com.railway.railwayAPI.model.Availablity;
import com.railway.railwayAPI.model.SearchInput;
import com.railway.railwayAPI.model.SearchResponse;
import com.railway.railwayAPI.model.internal.TrainUpdateInput;

import java.util.List;
import java.util.Map;

public interface SearchService {
    SearchResponse getSearchResults(SearchInput searchInput, String trainNumber, String cls, String update);

    Availablity getTrainUpdate(TrainUpdateInput trainUpdateInput);

    List<Availablity> getAvailabilityNearByDays(TrainUpdateInput trainUpdateInput);

    List<Availablity> getAvailabilityNearByDaysV3(TrainUpdateInput trainUpdateInput);

    List<Availablity> getAvailabilityNearByDaysV4(TrainUpdateInput trainUpdateInput);

    List<Availablity> getAvailabilityNearByDaysV5(TrainUpdateInput trainUpdateInput);
}
