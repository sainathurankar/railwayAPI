package com.railway.railwayAPI.controller;

import com.railway.railwayAPI.model.Availability;
import com.railway.railwayAPI.model.internal.TrainUpdateInput;
import com.railway.railwayAPI.service.SearchServiceV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/v2")
@Slf4j
public class SearchControllerV2 {

    @Autowired
    SearchServiceV2 searchServiceV2;

    @RequestMapping(value = "/search/availabilityNearBy", method = RequestMethod.POST)
    public ResponseEntity<?> getAvailabilityNearByDays(@RequestBody TrainUpdateInput trainUpdateInput) {
        long startTime = System.currentTimeMillis();
        List<Availability> response = null;
        try {
            log.info("Inside '/search/availabilityNearBy' mapping getAvailabilityNearByDays() method of SearchController");
            response = searchServiceV2.getAvailabilityNearByDays(trainUpdateInput);
        } catch (Exception e) {
            log.error("Exception caught in getAvailabilityNearByDays() method:", e);
            return ResponseEntity.internalServerError().body(Map.of("error", true, "message", e.getMessage()));
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("getAvailabilityNearByDays() Executed in " + (endTime - startTime) + "ms");
            log.info("getAvailabilityNearByDays() method ended");
        }
        return ResponseEntity.ok(response);
    }

}
