package com.railway.railwayAPI.service.impl;

import com.railway.railwayAPI.config.SearchConfigLoader;
import com.railway.railwayAPI.config.model.FilterConfig;
import com.railway.railwayAPI.model.*;
import com.railway.railwayAPI.service.SearchFiltersService;
import com.railway.railwayAPI.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@CacheConfig(cacheNames = "cache")
public class SearchFilterServiceimpl implements SearchFiltersService {

    @Autowired
    private SearchService searchService;

    @Autowired
    private SearchConfigLoader searchConfigLoader;

    @Cacheable(key = "#searchInput", unless = "#result == null")
    @Override
    public SearchFilterResponse getFilters(SearchInput searchInput) {
        SearchResponse response = searchService.getSearchResults(searchInput, null, null, null);
        FilterConfig filterConfig = searchConfigLoader.getSearchConfig();
        SearchFilterResponse searchFilterResponse = SearchFilterResponse.builder().src(searchInput.getSrc()).dst(searchInput.getDst())
                .doj(searchInput.getDoj()).filters(extractFilters(filterConfig, response)).build();
        return searchFilterResponse;
    }

    private List<Filter> extractFilters(FilterConfig filterConfig, SearchResponse response) {
        List<Filter> filters = new ArrayList<>();
        for (com.railway.railwayAPI.config.model.Filter filter : filterConfig.getFilters()) {
            Filter filterObj = buildFilter(filter, response);
            filters.add(filterObj);
        }
        return filters;
    }


    private Filter buildFilter(com.railway.railwayAPI.config.model.Filter filter, SearchResponse response) {
        Filter filterObj = Filter.builder().name(filter.getName()).options(buildFilterOptions(filter, response)).build();
        return filterObj;

    }

    private List<FilterOption> buildFilterOptions(com.railway.railwayAPI.config.model.Filter filter, SearchResponse response) {
        List<FilterOption> filterOptions = new ArrayList<>();
        response.getTrains().forEach(train -> {
            Map<String, Object> trainMap = train.toMap();
            FilterOption filterOption = FilterOption.builder().id((String) trainMap.get(filter.getValue())).value((String) trainMap.get(filter.getValue())).displayName((String) trainMap.get(filter.getDisplayName())).build();
            filterOptions.add(filterOption);
        });
        return filterOptions;
    }
}
