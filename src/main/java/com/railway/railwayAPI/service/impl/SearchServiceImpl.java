package com.railway.railwayAPI.service.impl;

import com.railway.railwayAPI.common.Utils;
import com.railway.railwayAPI.facade.Facade;
import com.railway.railwayAPI.helper.OutputBuilderHelper;
import com.railway.railwayAPI.model.Availability;
import com.railway.railwayAPI.model.SearchInput;
import com.railway.railwayAPI.model.SearchResponse;
import com.railway.railwayAPI.model.Train;
import com.railway.railwayAPI.model.internal.TrainUpdateInput;
import com.railway.railwayAPI.service.SearchService;
import com.railway.railwayAPI.task.AvailabilityTask;
import com.railway.railwayAPI.task.AvailabilityTaskV2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
    public Availability getTrainUpdate(TrainUpdateInput trainUpdateInput) {
        Map<String, Object> map = facade.getTrainUpdates(trainUpdateInput);
        Availability response = null;
        if (map.containsKey("Response")) {
            response = OutputBuilderHelper.buildAvailability((Map<String, Object>) (
                    (Map<String, Object>) ((Map<String, Object>)
                            map.get("Response")).get("Data")).get("details"));
        }
        return response;
    }

    @Override
    public List<Availability> getAvailabilityNearByDays(TrainUpdateInput trainUpdateInput) {
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
    public List<Availability> getAvailabilityNearByDaysV3(TrainUpdateInput trainUpdateInput) {
        List<Availability> availabilityList = new ArrayList<>();
        for (int i = 0; i < trainUpdateInput.getNumberOfDays(); i++) {
            List<Availability> availability = getTrainUpdateV2(trainUpdateInput);
            if (availability != null) {
                availabilityList.addAll(availability);
            }
            trainUpdateInput.setDoj(Utils.getNextDayDate(trainUpdateInput.getDoj()));
        }
        return availabilityList;
    }

    @Override
    public List<Availability> getAvailabilityNearByDaysV4(TrainUpdateInput trainUpdateInput) {
        List<TrainUpdateInput> trainUpdateInputs = new ArrayList<>();

        TrainUpdateInput trainUpdateInp = new TrainUpdateInput(trainUpdateInput);
        for (int i = 0; i < trainUpdateInput.getNumberOfDays(); i++) {
            trainUpdateInputs.add(trainUpdateInp);
            trainUpdateInp = new TrainUpdateInput(trainUpdateInp);
            trainUpdateInp.setDoj(Utils.getNextDayDate(trainUpdateInp.getDoj()));
        }
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        return forkJoinPool.invoke(new AvailabilityTaskV2(trainUpdateInputs, 0, trainUpdateInput.getNumberOfDays(), new SearchServiceImpl()));
    }

    /*
    * Async
    */
    @Override
    public ArrayList<Object> getAvailabilityNearByDaysV5(TrainUpdateInput trainUpdateInput) {
        int numberOfDays = trainUpdateInput.getNumberOfDays();
        List<CompletableFuture<List<Availability>>> futures = new ArrayList<>();

        for (int i = 0; i < numberOfDays; i++) {
            int dayIndex = i;
            CompletableFuture<List<Availability>> future = CompletableFuture.supplyAsync(() -> {
                try {
                    List<Availability> availabilityList = getTrainUpdateV3(trainUpdateInput, dayIndex);
                    if (availabilityList != null) {
                        return availabilityList;
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

    private List<Availability> getTrainUpdateV3(TrainUpdateInput trainUpdateInput, int day) {
        SearchResponse searchResponse = getSearchResults(new SearchInput(trainUpdateInput.getSource(), trainUpdateInput.getDestination(), Utils.addDate(trainUpdateInput.getDoj(), day)), trainUpdateInput.getTrainNumber(), trainUpdateInput.getclass(), "false");
        List<Train> trains = searchResponse.getTrains();
        if (!trains.isEmpty()) {
            return trains.get(0).getAvailabilitiesList();
        }
        return null;
    }

    public List<Availability> getTrainUpdateV2(TrainUpdateInput trainUpdateInput) {
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
