package com.railway.railwayAPI.controller;

import com.railway.railwayAPI.model.autocomplete.AutoCompleteResponse;
import com.railway.railwayAPI.service.AutoCompleteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class AutoCompleteController {

    @Autowired
    private AutoCompleteService autoCompleteService;

    Logger logger = LoggerFactory.getLogger(AutoCompleteController.class);

    @RequestMapping(value = "/autocomplete", method = RequestMethod.GET)
    public AutoCompleteResponse autocomplete(@RequestParam(value = "query", required = true) String query){
        long startTime = System.currentTimeMillis();
        AutoCompleteResponse response = null;
        try {
            logger.info("Inside '/autocomplete' mapping autocomplete() method of AutoCompleteController");
            response = autoCompleteService.getResults(query);
        } catch (Exception e) {
            logger.error("Exception caught in autocomplete() method:", e);
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("autocomplete() Executed in " + (endTime - startTime) + "ms");
            logger.info("autocomplete() method ended");
        }
        return response;
    }
}
