package com.railway.railwayAPI.facade;

import com.railway.railwayAPI.model.SearchInput;
import com.railway.railwayAPI.model.internal.TrainUpdateInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class SearchFacade {
    private String apiUrl = "https://www.redbus.in/railways/api";
    private Logger logger = LoggerFactory.getLogger(SearchFacade.class);
    public Map<String, Object> getSearchResults(SearchInput searchInput) {
        String url = apiUrl + "/searchCall";
        RestTemplate restTemplate = new RestTemplate();
        logger.info("Post call to " + url + " to get trains for journey on : "+ searchInput.getDoj() + " with source: " + searchInput.getSrc() + " and destination: " + searchInput.getDst());
        ResponseEntity<Object> response = restTemplate.postForEntity(url, searchInput, Object.class);
        return (Map<String, Object>) response.getBody();
    }

    public Map<String, Object> getTrainUpdates(TrainUpdateInput trainUpdateInput) {
        String url = apiUrl + "/tapToUpdate";
        logger.info("Post call to " + url + " for train number: " + trainUpdateInput.getTrainNumber() + " and class: "+ trainUpdateInput.getclass());
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object> response = restTemplate.postForEntity(url, trainUpdateInput, Object.class);
        return (Map<String, Object>) response.getBody();
    }
}
