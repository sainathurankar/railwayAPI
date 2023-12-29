package com.railway.railwayAPI.task;

import com.railway.railwayAPI.facade.Facade;
import com.railway.railwayAPI.helper.OutputBuilderHelper;
import com.railway.railwayAPI.model.Availablity;
import com.railway.railwayAPI.model.internal.TrainUpdateInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;


public class AvailabilityTask extends RecursiveTask<List<Availablity>> {
    private int startDay;
    private int endDay;

    private Facade facade = new Facade();

    private List<TrainUpdateInput> trainUpdateInputs;

    public AvailabilityTask(List<TrainUpdateInput> trainUpdateInputs, int startDay, int endDay) {
        this.trainUpdateInputs = trainUpdateInputs;
        this.startDay = startDay;
        this.endDay = endDay;
    }

    @Override
    protected List<Availablity> compute() {
        List<Availablity> availablityList = new ArrayList<>();

        if (endDay - startDay <= 1) {
            Availablity availablity = getTrainUpdate(trainUpdateInputs.get(startDay));
            if (availablity != null) {
                availablityList.add(availablity);
            }
        } else {
            int mid = (startDay + endDay) / 2;

            AvailabilityTask leftTask = new AvailabilityTask(trainUpdateInputs, startDay, mid);
            AvailabilityTask rightTask = new AvailabilityTask(trainUpdateInputs, mid, endDay);

            leftTask.fork();
            List<Availablity> rightResult = rightTask.compute();
            List<Availablity> leftResult = leftTask.join();

            availablityList.addAll(leftResult);
            availablityList.addAll(rightResult);
        }

        return availablityList;
    }


    private Availablity getTrainUpdate(TrainUpdateInput trainUpdateInput) {
        Map<String, Object> map = facade.getTrainUpdates(trainUpdateInput);
        Availablity response = null;
        if (map.containsKey("Response")) {
            response = OutputBuilderHelper.buildAvailabilty((Map<String, Object>) (
                    (Map<String, Object>) ((Map<String, Object>)
                            map.get("Response")).get("Data")).get("details"));
        }
        return response;
    }
}
