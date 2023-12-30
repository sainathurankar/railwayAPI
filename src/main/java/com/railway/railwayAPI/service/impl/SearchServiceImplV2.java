package com.railway.railwayAPI.service.impl;

import com.railway.railwayAPI.common.Utils;
import com.railway.railwayAPI.facade.Facade;
import com.railway.railwayAPI.helper.OutputBuilderHelper;
import com.railway.railwayAPI.model.Availablity;
import com.railway.railwayAPI.model.internal.TrainUpdateInput;
import com.railway.railwayAPI.service.SearchServiceV2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SearchServiceImplV2 implements SearchServiceV2 {

    Facade facade = new Facade();

    @Override
    public List<Availablity> getAvailabilityNearByDays(TrainUpdateInput trainUpdateInput) {
        List<Availablity> availablityList = new ArrayList<>();
        for (int i = 0; i < trainUpdateInput.getNumberOfDays(); i++) {
            Availablity availablity = getTrainUpdate(trainUpdateInput);
            if (availablity != null) {
                availablityList.add(availablity);
            }
            trainUpdateInput.setDoj(Utils.getNextDayDate(trainUpdateInput.getDoj()));
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
