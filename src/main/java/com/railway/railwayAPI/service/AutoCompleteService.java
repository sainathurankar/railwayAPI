package com.railway.railwayAPI.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.railway.railwayAPI.model.autocomplete.AutoCompleteResponse;

public interface AutoCompleteService {
    AutoCompleteResponse getResults(String query) throws JsonProcessingException;
}
