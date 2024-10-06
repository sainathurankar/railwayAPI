package com.railway.railwayAPI.service.impl;

import com.railway.railwayAPI.common.Utils;
import com.railway.railwayAPI.facade.Facade;
import com.railway.railwayAPI.helper.OutputBuilderHelper;
import com.railway.railwayAPI.model.Availability;
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
    public List<Availability> getAvailabilityNearByDays(TrainUpdateInput trainUpdateInput) {
        List<Availability> availabilityList = new ArrayList<>();
        for (int i = 0; i < trainUpdateInput.getNumberOfDays(); i++) {
            Availability availability = getTrainUpdate(trainUpdateInput);
            if (availability != null) {
                availabilityList.add(availability);
            }
            trainUpdateInput.setDoj(Utils.getNextDayDate(trainUpdateInput.getDoj()));
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
