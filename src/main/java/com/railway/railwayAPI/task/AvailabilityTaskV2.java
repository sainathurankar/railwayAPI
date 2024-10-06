package com.railway.railwayAPI.task;

import com.railway.railwayAPI.model.Availability;
import com.railway.railwayAPI.model.internal.TrainUpdateInput;
import com.railway.railwayAPI.service.impl.SearchServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;


public class AvailabilityTaskV2 extends RecursiveTask<List<Availability>> {
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
    protected List<Availability> compute() {
        List<Availability> availabilityList = new ArrayList<>();

        if (endDay - startDay <= 1) {
            List<Availability> availability = getTrainUpdate(trainUpdateInputs.get(startDay));
            if (availability != null) {
                availabilityList.addAll(availability);
            }
        } else {
            int mid = (startDay + endDay) / 2;

            AvailabilityTaskV2 leftTask = new AvailabilityTaskV2(trainUpdateInputs, startDay, mid, new SearchServiceImpl());
            AvailabilityTaskV2 rightTask = new AvailabilityTaskV2(trainUpdateInputs, mid, endDay, new SearchServiceImpl());

            leftTask.fork();
            List<Availability> rightResult = rightTask.compute();
            List<Availability> leftResult = leftTask.join();

            availabilityList.addAll(leftResult);
            availabilityList.addAll(rightResult);
        }

        return availabilityList;
    }


    private List<Availability> getTrainUpdate(TrainUpdateInput trainUpdateInput) {
        List<Availability> availabilityList = searchService.getTrainUpdateV2(trainUpdateInput);
        return availabilityList;
    }
}
