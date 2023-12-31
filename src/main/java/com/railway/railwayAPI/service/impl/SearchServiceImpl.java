package com.railway.railwayAPI.service.impl;

import com.railway.railwayAPI.common.Utils;
import com.railway.railwayAPI.facade.Facade;
import com.railway.railwayAPI.helper.OutputBuilderHelper;
import com.railway.railwayAPI.model.Availablity;
import com.railway.railwayAPI.model.SearchInput;
import com.railway.railwayAPI.model.SearchResponse;
import com.railway.railwayAPI.model.Train;
import com.railway.railwayAPI.model.internal.TrainUpdateInput;
import com.railway.railwayAPI.service.SearchService;
import com.railway.railwayAPI.task.AvailabilityTask;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

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
        List<TrainUpdateInput> trainUpdateInputs = new ArrayList<>();

        TrainUpdateInput trainUpdateInp = new TrainUpdateInput(trainUpdateInput);
        for (int i = 0; i < trainUpdateInput.getNumberOfDays(); i++) {
            trainUpdateInputs.add(trainUpdateInp);
            trainUpdateInp = new TrainUpdateInput(trainUpdateInp);
            trainUpdateInp.setDoj(Utils.getNextDayDate(trainUpdateInp.getDoj()));
        }
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        return forkJoinPool.invoke(new AvailabilityTask(trainUpdateInputs, 0, trainUpdateInput.getNumberOfDays()));
    }

    @Override
    public List<Availablity> getAvailabilityNearByDaysV3(TrainUpdateInput trainUpdateInput) {
        List<Availablity> availablityList = new ArrayList<>();
        for (int i = 0; i < trainUpdateInput.getNumberOfDays(); i++) {
            List<Availablity> availablity = getTrainUpdateV2(trainUpdateInput);
            if (availablity != null) {
                availablityList.addAll(availablity);
            }
            trainUpdateInput.setDoj(Utils.getNextDayDate(trainUpdateInput.getDoj()));
        }
        return availablityList;
    }

    private List<Availablity> getTrainUpdateV2(TrainUpdateInput trainUpdateInput) {
        SearchResponse searchResponse = getSearchResults(new SearchInput(trainUpdateInput.getSource(), trainUpdateInput.getDestination(), trainUpdateInput.getDoj()), trainUpdateInput.getTrainNumber(), trainUpdateInput.getclass(), "false");
        List<Train> trains = searchResponse.getTrains();
        if (!trains.isEmpty()) {
            return trains.get(0).getAvailabilitiesList();
        }
        return null;
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
