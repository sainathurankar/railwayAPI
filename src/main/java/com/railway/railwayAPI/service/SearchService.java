package com.railway.railwayAPI.service;

import com.railway.railwayAPI.model.SearchInput;
import com.railway.railwayAPI.model.SearchResponse;

public interface SearchService {
    SearchResponse getSearchResults(SearchInput searchInput, String trainNumber, String cls);
}
