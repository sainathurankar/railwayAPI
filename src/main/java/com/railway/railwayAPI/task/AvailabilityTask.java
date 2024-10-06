package com.railway.railwayAPI.task;

import com.railway.railwayAPI.facade.Facade;
import com.railway.railwayAPI.helper.OutputBuilderHelper;
import com.railway.railwayAPI.model.Availability;
import com.railway.railwayAPI.model.internal.TrainUpdateInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;


public class AvailabilityTask extends RecursiveTask<List<Availability>> {
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
    protected List<Availability> compute() {
        List<Availability> availabilityList = new ArrayList<>();

        if (endDay - startDay <= 1) {
            Availability availability = getTrainUpdate(trainUpdateInputs.get(startDay));
            if (availability != null) {
                availabilityList.add(availability);
            }
        } else {
            int mid = (startDay + endDay) / 2;

            AvailabilityTask leftTask = new AvailabilityTask(trainUpdateInputs, startDay, mid);
            AvailabilityTask rightTask = new AvailabilityTask(trainUpdateInputs, mid, endDay);

            leftTask.fork();
            List<Availability> rightResult = rightTask.compute();
            List<Availability> leftResult = leftTask.join();

            availabilityList.addAll(leftResult);
            availabilityList.addAll(rightResult);
        }

        return availabilityList;
    }


    private Availability getTrainUpdate(TrainUpdateInput trainUpdateInput) {
        Map<String, Object> map = facade.getTrainUpdates(trainUpdateInput);
        Availability response = null;
        if (map.containsKey("Response")) {
            response = OutputBuilderHelper.buildAvailability((Map<String, Object>) (
                    (Map<String, Object>) ((Map<String, Object>)
                            map.get("Response")).get("Data")).get("details"));
        }
        return response;
    }
}
