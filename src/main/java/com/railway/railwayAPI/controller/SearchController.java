package com.railway.railwayAPI.controller;

import com.railway.railwayAPI.model.Availablity;
import com.railway.railwayAPI.model.SearchInput;
import com.railway.railwayAPI.model.SearchResponse;
import com.railway.railwayAPI.model.internal.TrainUpdateInput;
import com.railway.railwayAPI.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class SearchController {
    @Autowired
    private SearchService searchService;

    Logger logger = LoggerFactory.getLogger(SearchController.class);

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ResponseEntity<?> search(@RequestBody SearchInput searchInput, @RequestParam(value = "trainNumber", required = false) String trainNumber, @RequestParam(value = "class", required = false) String cls, @RequestParam(value = "update", required = false, defaultValue = "false") String update) {
        long startTime = System.currentTimeMillis();
        SearchResponse response = null;
        try {
            logger.info("Inside '/search' mapping search() method of SearchController");
             response = searchService.getSearchResults(searchInput, trainNumber, cls, update);
        } catch (Exception e) {
            logger.error("Exception caught in search() method:", e);
            return ResponseEntity.internalServerError().body(Map.of("error", true, "message", e.getMessage()));
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("search() Executed in " + (endTime - startTime) + "ms");
            logger.info("search() method ended");
        }
        return ResponseEntity.ok().body(response);
    }

    @RequestMapping(value = "/search/trainUpdate", method = RequestMethod.POST)
    public ResponseEntity<?> getTrainUpdate(@RequestBody TrainUpdateInput trainUpdateInput) {
        long startTime = System.currentTimeMillis();
        Availablity response = null;
        try {
            logger.info("Inside '/search/trainUpdate' mapping getTrainUpdate() method of SearchController");
            response = searchService.getTrainUpdate(trainUpdateInput);
        } catch (Exception e) {
            logger.error("Exception caught in getTrainUpdate() method:", e);
            return ResponseEntity.internalServerError().body(Map.of("error", true, "message", e.getMessage()));
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("getTrainUpdate() Executed in " + (endTime - startTime) + "ms");
            logger.info("getTrainUpdate() method ended");
        }
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/search/availabilityNearBy", method = RequestMethod.POST)
    public ResponseEntity<?> getAvailabilityNearByDays(@RequestBody TrainUpdateInput trainUpdateInput) {
        long startTime = System.currentTimeMillis();
        List<Availablity> response = null;
        try {
            logger.info("Inside '/search/availabilityNearBy' mapping getAvailabilityNearByDays() method of SearchController");
            response = searchService.getAvailabilityNearByDays(trainUpdateInput);
        } catch (Exception e) {
            logger.error("Exception caught in getAvailabilityNearByDays() method:", e);
            return ResponseEntity.internalServerError().body(Map.of("error", true, "message", e.getMessage()));
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("getAvailabilityNearByDays() Executed in " + (endTime - startTime) + "ms");
            logger.info("getAvailabilityNearByDays() method ended");
        }
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/v3/search/availabilityNearBy", method = RequestMethod.POST)
    public ResponseEntity<?> getAvailabilityNearByDaysV3(@RequestBody TrainUpdateInput trainUpdateInput) {
        long startTime = System.currentTimeMillis();
        List<Availablity> response = null;
        try {
            logger.info("Inside '/search/availabilityNearByV3' mapping getAvailabilityNearByDaysV3() method of SearchController");
            response = searchService.getAvailabilityNearByDaysV3(trainUpdateInput);
        } catch (Exception e) {
            logger.error("Exception caught in getAvailabilityNearByDaysV3() method:", e);
            return ResponseEntity.internalServerError().body(Map.of("error", true, "message", e.getMessage()));
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("getAvailabilityNearByDays() Executed in " + (endTime - startTime) + "ms");
            logger.info("getAvailabilityNearByDays() method ended");
        }
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/v4/search/availabilityNearBy", method = RequestMethod.POST)
    public ResponseEntity<?> getAvailabilityNearByDaysV4(@RequestBody TrainUpdateInput trainUpdateInput) {
        long startTime = System.currentTimeMillis();
        List<Availablity> response = null;
        try {
            logger.info("Inside '/search/availabilityNearByV4' mapping getAvailabilityNearByDaysV4() method of SearchController");
            response = searchService.getAvailabilityNearByDaysV4(trainUpdateInput);
        } catch (Exception e) {
            logger.error("Exception caught in getAvailabilityNearByDaysV4() method:", e);
            return ResponseEntity.internalServerError().body(Map.of("error", true, "message", e.getMessage()));
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("getAvailabilityNearByDaysV4() Executed in " + (endTime - startTime) + "ms");
            logger.info("getAvailabilityNearByDaysV4() method ended");
        }
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/v5/search/availabilityNearBy", method = RequestMethod.POST)
    public ResponseEntity<?> getAvailabilityNearByDaysV5(@RequestBody TrainUpdateInput trainUpdateInput) {
        long startTime = System.currentTimeMillis();
        ArrayList<Object> response = null;
        try {
            logger.info("Inside '/search/availabilityNearByV5' mapping getAvailabilityNearByDaysV5() method of SearchController");
            response = searchService.getAvailabilityNearByDaysV5(trainUpdateInput);
        } catch (Exception e) {
            logger.error("Exception caught in getAvailabilityNearByDaysV5() method:", e);
            return ResponseEntity.internalServerError().body(Map.of("error", true, "message", e.getMessage()));
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("getAvailabilityNearByDaysV5() Executed in " + (endTime - startTime) + "ms");
            logger.info("getAvailabilityNearByDaysV5() method ended");
        }
        return ResponseEntity.ok(response);
    }



}
