package com.railway.railwayAPI.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.railway.railwayAPI.model.SearchInput;
import com.railway.railwayAPI.model.internal.AutoComplete;
import com.railway.railwayAPI.model.internal.TrainUpdateInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

public class Facade {
    private String apiUrl = "https://www.redbus.in/railways/api";
    private Logger logger = LoggerFactory.getLogger(Facade.class);
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
        ResponseEntity<Object> response = null;
        try {
            response = restTemplate.postForEntity(url, trainUpdateInput, Object.class);
        } catch (HttpClientErrorException errorException) {
            logger.error(errorException.getMessage());
            return new LinkedHashMap<>();
        }

        return (Map<String, Object>) response.getBody();
    }

    public AutoComplete getAutoCompleteResults(String query) throws JsonProcessingException {
        String url = apiUrl + "/SolrSearch?search={query}";
        logger.info("GET call to " + url + " to get autocomplete results for " + StringUtils.trimAllWhitespace(query));
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class, StringUtils.trimAllWhitespace(query));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response, AutoComplete.class);
    }
}
