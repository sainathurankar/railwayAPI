package com.railway.railwayAPI.service;

import com.railway.railwayAPI.model.Availablity;
import com.railway.railwayAPI.model.SearchInput;
import com.railway.railwayAPI.model.SearchResponse;
import com.railway.railwayAPI.model.internal.TrainUpdateInput;

import java.util.Map;

public interface SearchService {
    SearchResponse getSearchResults(SearchInput searchInput, String trainNumber, String cls, String update);

    Availablity getTrainUpdate(TrainUpdateInput trainUpdateInput);
}
