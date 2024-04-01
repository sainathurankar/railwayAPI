package com.railway.railwayAPI.controller;

import com.railway.railwayAPI.model.SearchFilterResponse;
import com.railway.railwayAPI.model.SearchInput;
import com.railway.railwayAPI.service.SearchFiltersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@Slf4j
public class SearchFiltersController {

    @Autowired
    SearchFiltersService searchFiltersService;

    @RequestMapping(value = "/search/filters", method = RequestMethod.POST)
    public ResponseEntity<?> getFilters(@RequestBody SearchInput searchInput) {
        long startTime = System.currentTimeMillis();
        SearchFilterResponse response = null;
        try {
            log.info("Inside '/search/filters' mapping getFilters() method of SearchFiltersController");
            response = searchFiltersService.getFilters(searchInput);
        } catch (Exception e) {
            log.error("Exception caught in getFilters() method:", e);
            return ResponseEntity.internalServerError().body(Map.of("error", true, "message", e.getMessage()));
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("getFilters() Executed in " + (endTime - startTime) + "ms");
            log.info("getFilters() method ended");
        }
        return ResponseEntity.ok().body(response);
    }
}
