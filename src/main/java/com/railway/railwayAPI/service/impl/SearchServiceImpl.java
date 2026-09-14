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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

    /*
    * Async
    */
    @Override
    public ArrayList<Object> getAvailabilityNearByDaysV5(TrainUpdateInput trainUpdateInput) {
        int numberOfDays = trainUpdateInput.getNumberOfDays();
        List<CompletableFuture<List<Availablity>>> futures = new ArrayList<>();

        for (int i = 0; i < numberOfDays; i++) {
            int dayIndex = i;
            CompletableFuture<List<Availablity>> future = CompletableFuture.supplyAsync(() -> {
                try {
                    List<Availablity> availablityList = getTrainUpdateV3(trainUpdateInput, dayIndex);
                    if (availablityList != null) {
                        return availablityList;
                    }
                    return new ArrayList<>();
                } catch (Exception e) {
                    // Log the exception or perform error handling
                    e.printStackTrace();
                    return new ArrayList<>();  // Return an empty list or handle the error case accordingly
                }
            });
            futures.add(future);
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );

        CompletableFuture<ArrayList<Object>> combinedFuture = allOf.thenApply(v ->
                futures.stream()
                        .map(f -> {
                            try {
                                return f.join();
                            } catch (Exception e) {
                                // Log the exception for debugging purposes
                                e.printStackTrace();
                                return new ArrayList<>();
                            }
                        })
                        .flatMap(List::stream)
                        .collect(ArrayList::new, List::add, List::addAll)
        );

        try {
            return combinedFuture.get(); // This will block until all futures are completed
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Availablity> getTrainUpdateV3(TrainUpdateInput trainUpdateInput, int day) {
        SearchResponse searchResponse = getSearchResults(new SearchInput(trainUpdateInput.getSource(), trainUpdateInput.getDestination(), Utils.addDate(trainUpdateInput.getDoj(), day)), trainUpdateInput.getTrainNumber(), trainUpdateInput.getclass(), "false");
        List<Train> trains = searchResponse.getTrains();
        if (!trains.isEmpty()) {
            return trains.get(0).getAvailabilitiesList();
        }
        return null;
    }


    private SearchResponse buildSearchResponse(SearchInput searchInput, Map<String, Object> map, String trainNumber, String cls, String update) {
        SearchResponse searchResponse = new SearchResponse();
        Map<String, Object> response = (Map<String, Object>) map.get("Response");
        searchResponse.setStatus(OutputBuilderHelper.buildResponseStatus(map.get("Status")));
        searchResponse.setTrainList(OutputBuilderHelper.getTrainList(searchInput, response, trainNumber, cls));
        searchResponse.setTrains(OutputBuilderHelper.getTrainListV2(searchInput, response, trainNumber, cls, update));
        searchResponse.setErrorCode((String) OutputBuilderHelper.getDetails(response).get("errorcode"));
        searchResponse.setDetailedMsg((String) OutputBuilderHelper.getDetails(response).get("detailedmsg"));
        // --- New: recommendation, offers, catalog passthrough, route confidence ---
        try {
            searchResponse.setRecommendation(OutputBuilderHelper.getRecommendation(searchInput, response, cls, "false"));
            searchResponse.setRecommendationTags(OutputBuilderHelper.getRecommendationTags(response));
            Object offers = OutputBuilderHelper.getPassthrough(response, "offers");
            searchResponse.setOffers(offers instanceof List ? (List<Object>) offers : null);
            Object composite = OutputBuilderHelper.getPassthrough(response, "compositeAvailability");
            searchResponse.setCompositeAvailability(composite != null ? String.valueOf(composite) : null);
            searchResponse.setFilters(OutputBuilderHelper.getPassthrough(response, "filters"));
            searchResponse.setSort(OutputBuilderHelper.getPassthrough(response, "sort"));
            searchResponse.setQuotas(OutputBuilderHelper.getPassthrough(response, "quotas"));
        } catch (Exception e) {
            // Enrichment is best-effort; never fail the core search on optional fields.
            e.printStackTrace();
        }
        return searchResponse;
    }
}
