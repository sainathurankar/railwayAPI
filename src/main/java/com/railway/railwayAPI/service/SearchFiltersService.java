package com.railway.railwayAPI.service;

import com.railway.railwayAPI.model.SearchFilterResponse;
import com.railway.railwayAPI.model.SearchInput;

public interface SearchFiltersService {
    SearchFilterResponse getFilters(SearchInput searchInput);
}
