package com.railway.railwayAPI.service;

import com.railway.railwayAPI.model.Availability;
import com.railway.railwayAPI.model.SearchInput;
import com.railway.railwayAPI.model.SearchResponse;
import com.railway.railwayAPI.model.internal.TrainUpdateInput;

import java.util.ArrayList;
import java.util.List;

public interface SearchService {
    SearchResponse getSearchResults(SearchInput searchInput, String trainNumber, String cls, String update);

    Availability getTrainUpdate(TrainUpdateInput trainUpdateInput);

    List<Availability> getAvailabilityNearByDays(TrainUpdateInput trainUpdateInput);

    List<Availability> getAvailabilityNearByDaysV3(TrainUpdateInput trainUpdateInput);

    List<Availability> getAvailabilityNearByDaysV4(TrainUpdateInput trainUpdateInput);

    ArrayList<Object> getAvailabilityNearByDaysV5(TrainUpdateInput trainUpdateInput);
}
