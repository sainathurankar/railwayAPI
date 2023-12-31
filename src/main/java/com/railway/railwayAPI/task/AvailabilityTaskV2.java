package com.railway.railwayAPI.task;

import com.railway.railwayAPI.facade.Facade;
import com.railway.railwayAPI.helper.OutputBuilderHelper;
import com.railway.railwayAPI.model.Availablity;
import com.railway.railwayAPI.model.internal.TrainUpdateInput;
import com.railway.railwayAPI.service.SearchService;
import com.railway.railwayAPI.service.impl.SearchServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;


public class AvailabilityTaskV2 extends RecursiveTask<List<Availablity>> {
    private int startDay;
    private int endDay;

    private SearchServiceImpl searchService;

    private List<TrainUpdateInput> trainUpdateInputs;

    public AvailabilityTaskV2(List<TrainUpdateInput> trainUpdateInputs, int startDay, int endDay, SearchServiceImpl searchService) {
        this.trainUpdateInputs = trainUpdateInputs;
        this.startDay = startDay;
        this.endDay = endDay;
        this.searchService = searchService;
    }

    @Override
    protected List<Availablity> compute() {
        List<Availablity> availablityList = new ArrayList<>();

        if (endDay - startDay <= 1) {
            List<Availablity> availablity = getTrainUpdate(trainUpdateInputs.get(startDay));
            if (availablity != null) {
                availablityList.addAll(availablity);
            }
        } else {
            int mid = (startDay + endDay) / 2;

            AvailabilityTaskV2 leftTask = new AvailabilityTaskV2(trainUpdateInputs, startDay, mid, new SearchServiceImpl());
            AvailabilityTaskV2 rightTask = new AvailabilityTaskV2(trainUpdateInputs, mid, endDay, new SearchServiceImpl());

            leftTask.fork();
            List<Availablity> rightResult = rightTask.compute();
            List<Availablity> leftResult = leftTask.join();

            availablityList.addAll(leftResult);
            availablityList.addAll(rightResult);
        }

        return availablityList;
    }


    private List<Availablity> getTrainUpdate(TrainUpdateInput trainUpdateInput) {
        List<Availablity> availablityList = searchService.getTrainUpdateV2(trainUpdateInput);
        return availablityList;
    }
}
