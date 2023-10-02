package com.railway.railwayAPI.service.impl;

import com.railway.railwayAPI.facade.SearchFacade;
import com.railway.railwayAPI.helper.OutputBuilderHelper;
import com.railway.railwayAPI.model.SearchInput;
import com.railway.railwayAPI.model.SearchResponse;
import com.railway.railwayAPI.service.SearchService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {

    private SearchFacade searchFacade = new SearchFacade();
//    private OutputBuilderHelper outputBuilderHelper = new OutputBuilderHelper();

    @Override
    public SearchResponse getSearchResults(SearchInput searchInput, String trainNumber, String cls) {
        Map<String, Object> map = searchFacade.getSearchResults(searchInput);
        SearchResponse searchResponse = buildSearchResponse(searchInput, map, trainNumber, cls);
        return searchResponse;
    }

    private SearchResponse buildSearchResponse(SearchInput searchInput, Map<String, Object> map, String trainNumber, String cls) {
        SearchResponse searchResponse = new SearchResponse();
//        searchResponse.setError((String) map.get("Error"));
//        searchResponse.setResponse(map.get("Response"));
        searchResponse.setStatus(map.get("Status"));
        searchResponse.setTrainList(OutputBuilderHelper.getTrainList(searchInput, (Map<String, Object>) map.get("Response"), trainNumber, cls));
        searchResponse.setTrains(OutputBuilderHelper.getTrainListV2(searchInput, (Map<String, Object>) map.get("Response"),trainNumber, cls));
        return searchResponse;
    }
}
