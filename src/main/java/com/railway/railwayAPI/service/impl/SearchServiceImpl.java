package com.railway.railwayAPI.service.impl;

import com.railway.railwayAPI.common.Utils;
import com.railway.railwayAPI.facade.Facade;
import com.railway.railwayAPI.helper.OutputBuilderHelper;
import com.railway.railwayAPI.model.Availablity;
import com.railway.railwayAPI.model.SearchInput;
import com.railway.railwayAPI.model.SearchResponse;
import com.railway.railwayAPI.model.internal.TrainUpdateInput;
import com.railway.railwayAPI.service.SearchService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {

    private Facade facade = new Facade();
//    private OutputBuilderHelper outputBuilderHelper = new OutputBuilderHelper();

    @Override
    public SearchResponse getSearchResults(SearchInput searchInput, String trainNumber, String cls, String update) {
        Map<String, Object> map = facade.getSearchResults(searchInput);
        SearchResponse searchResponse = buildSearchResponse(searchInput, map, trainNumber, cls, update);
        return searchResponse;
    }

    @Override
    public Availablity getTrainUpdate(TrainUpdateInput trainUpdateInput) {
        Map<String, Object> map = facade.getTrainUpdates(trainUpdateInput);
        Availablity response = null;
        if (map.containsKey("Response")) {
            response = OutputBuilderHelper.buildAvailabilty((Map<String, Object>) (
                    (Map<String, Object>) ((Map<String, Object>)
                            map.get("Response")).get("Data")).get("details"));
        }
        return response;
    }

    @Override
    public List<Availablity> getAvailabilityNearByDays(TrainUpdateInput trainUpdateInput) {
        List<Availablity> availablityList = new ArrayList<>();
        for (int i = 0; i< trainUpdateInput.getNumberOfDays(); i++) {
            Availablity availablity = getTrainUpdate(trainUpdateInput);
            if (availablity != null) {
                availablityList.add(availablity);
            }
            trainUpdateInput.setDoj(Utils.getNextDayDate(trainUpdateInput.getDoj()));
        }
        return availablityList;
    }

    private SearchResponse buildSearchResponse(SearchInput searchInput, Map<String, Object> map, String trainNumber, String cls, String update) {
        SearchResponse searchResponse = new SearchResponse();
//        searchResponse.setError((String) map.get("Error"));
//        searchResponse.setResponse(map.get("Response"));
        searchResponse.setStatus(map.get("Status"));
        searchResponse.setTrainList(OutputBuilderHelper.getTrainList(searchInput, (Map<String, Object>) map.get("Response"), trainNumber, cls));
        searchResponse.setTrains(OutputBuilderHelper.getTrainListV2(searchInput, (Map<String, Object>) map.get("Response"),trainNumber, cls, update));
        searchResponse.setErrorCode((String) OutputBuilderHelper.getDetails((Map<String, Object>) map.get("Response")).get("errorcode"));
        searchResponse.setDetailedMsg((String) OutputBuilderHelper.getDetails((Map<String, Object>) map.get("Response")).get("detailedmsg"));
        return searchResponse;
    }
}
