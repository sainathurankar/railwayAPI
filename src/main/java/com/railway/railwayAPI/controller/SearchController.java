package com.railway.railwayAPI.controller;

import com.railway.railwayAPI.model.Availablity;
import com.railway.railwayAPI.model.SearchInput;
import com.railway.railwayAPI.model.SearchResponse;
import com.railway.railwayAPI.model.internal.TrainUpdateInput;
import com.railway.railwayAPI.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
public class SearchController {
    @Autowired
    private SearchService searchService;

    Logger logger = LoggerFactory.getLogger(SearchController.class);

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public SearchResponse search(@RequestBody SearchInput searchInput, @RequestParam(value = "trainNumber", required = false) String trainNumber, @RequestParam(value = "class", required = false) String cls, @RequestParam(value = "update", required = false, defaultValue = "false") String update) {
        long startTime = System.currentTimeMillis();
        SearchResponse response = null;
        try {
            logger.info("Inside '/search' mapping search() method of SearchController");
             response = searchService.getSearchResults(searchInput, trainNumber, cls, update);
        } catch (Exception e) {
            logger.error("Exception caught in search() method:", e);
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("search() Executed in " + (endTime - startTime) + "ms");
            logger.info("search() method ended");
        }
        return response;
    }

    @RequestMapping(value = "/search/trainUpdate", method = RequestMethod.POST)
    public Availablity getTrainUpdate(@RequestBody TrainUpdateInput trainUpdateInput) {
        long startTime = System.currentTimeMillis();
        Availablity response = null;
        try {
            logger.info("Inside '/search/trainUpdate' mapping getTrainUpdate() method of SearchController");
            response = searchService.getTrainUpdate(trainUpdateInput);
        } catch (Exception e) {
            logger.error("Exception caught in search() method:", e);
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("getTrainUpdate() Executed in " + (endTime - startTime) + "ms");
            logger.info("getTrainUpdate() method ended");
        }
        return response;
    }


}
