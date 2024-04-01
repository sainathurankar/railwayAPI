package com.railway.railwayAPI.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.railway.railwayAPI.facade.Facade;
import com.railway.railwayAPI.model.autocomplete.AutoCompleteResponse;
import com.railway.railwayAPI.model.internal.AutoComplete;
import com.railway.railwayAPI.service.AutoCompleteService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@CacheConfig(cacheNames = "cache")
public class AutoCompleteServiceImpl implements AutoCompleteService {

    private Facade facade = new Facade();

    @Cacheable(key = "#query", unless = "#result == null")
    @Override
    public AutoCompleteResponse getResults(String query) throws JsonProcessingException {
        AutoComplete result = facade.getAutoCompleteResults(query);
        AutoCompleteResponse response = buildAutoCompletResponse(result.getResponse());
        return response;
    }

    private AutoCompleteResponse buildAutoCompletResponse(Map<String, Object> response) {
        AutoCompleteResponse autoCompleteResponse = new AutoCompleteResponse();
        autoCompleteResponse.setStart((Integer) response.get("start"));
        autoCompleteResponse.setNumFoundExact((Boolean) response.get("numFoundExact"));
        autoCompleteResponse.setNumFound((Integer) response.get("numFound"));
        autoCompleteResponse.setResults((List<Map<String, Object>>) response.get("docs"));
        return autoCompleteResponse;
    }
}
