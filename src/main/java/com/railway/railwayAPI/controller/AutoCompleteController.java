package com.railway.railwayAPI.controller;

import com.railway.railwayAPI.model.autocomplete.AutoCompleteResponse;
import com.railway.railwayAPI.service.AutoCompleteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
public class AutoCompleteController {

    @Autowired
    private AutoCompleteService autoCompleteService;

    Logger logger = LoggerFactory.getLogger(AutoCompleteController.class);

     @Operation(summary = "Autocomplete search", description = "Fetches autocomplete suggestions based on the query parameter.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of autocomplete results"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(
            @Parameter(description = "The search query for autocomplete suggestions", required = true, example = "Hub")
            @RequestParam(value = "query") String query) {
        
        long startTime = System.currentTimeMillis();
        AutoCompleteResponse response;
        
        try {
            logger.info("Inside '/autocomplete' mapping autocomplete() method of AutoCompleteController");
            response = autoCompleteService.getResults(query);
        } catch (Exception e) {
            logger.error("Exception caught in autocomplete() method:", e);
            return ResponseEntity.internalServerError().body(Map.of("error", true, "message", e.getMessage()));
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("autocomplete() Executed in " + (endTime - startTime) + "ms");
            logger.info("autocomplete() method ended");
        }
        
        return ResponseEntity.ok(response);
    }
}
