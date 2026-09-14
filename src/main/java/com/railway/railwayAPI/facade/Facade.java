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

/**
 * Thin proxy over the redBus railways search API.
 *
 * <p>All outbound calls share a single pooled {@link RestTemplate} (see
 * {@link HttpClientFactory}) with explicit connect / read timeouts, so a slow
 * or hung redBus response can no longer exhaust threads or block a request
 * indefinitely (the old code created a fresh {@code new RestTemplate()} per
 * call, which had no timeouts and no connection reuse).
 *
 * <p>The redBus base URL is read from configuration (system property or the
 * {@code REDBUS_API_URL} environment variable) so it is no longer hard-coded;
 * the historical value remains the default.
 */
public class Facade {

    private static final Logger logger = LoggerFactory.getLogger(Facade.class);

    /** Historical hard-coded value, kept as the default. */
    private static final String DEFAULT_API_URL = "https://www.redbus.in/railways/api";

    /** Shared, thread-safe, pooled RestTemplate for every redBus call. */
    private static final RestTemplate REST_TEMPLATE = HttpClientFactory.pooledRestTemplate();

    private final String apiUrl = resolveApiUrl();

    private static String resolveApiUrl() {
        String configured = System.getProperty("redbus.api.url");
        if (!StringUtils.hasText(configured)) {
            configured = System.getenv("REDBUS_API_URL");
        }
        return StringUtils.hasText(configured) ? configured : DEFAULT_API_URL;
    }

    public Map<String, Object> getSearchResults(SearchInput searchInput) {
        String url = apiUrl + "/searchCall";
        logger.info("Post call to " + url + " to get trains for journey on : " + searchInput.getDoj()
                + " with source: " + searchInput.getSrc() + " and destination: " + searchInput.getDst());
        ResponseEntity<Object> response = REST_TEMPLATE.postForEntity(url, searchInput, Object.class);
        return (Map<String, Object>) response.getBody();
    }

    public Map<String, Object> getTrainUpdates(TrainUpdateInput trainUpdateInput) {
        String url = apiUrl + "/tapToUpdate";
        logger.info("Post call to " + url + " for train number: " + trainUpdateInput.getTrainNumber()
                + " and class: " + trainUpdateInput.getclass());
        ResponseEntity<Object> response;
        try {
            response = REST_TEMPLATE.postForEntity(url, trainUpdateInput, Object.class);
        } catch (HttpClientErrorException errorException) {
            logger.error(errorException.getMessage());
            return new LinkedHashMap<>();
        }
        return (Map<String, Object>) response.getBody();
    }

    public AutoComplete getAutoCompleteResults(String query) throws JsonProcessingException {
        String url = apiUrl + "/SolrSearch?search={query}";
        logger.info("GET call to " + url + " to get autocomplete results for " + StringUtils.trimAllWhitespace(query));
        String response = REST_TEMPLATE.getForObject(url, String.class, StringUtils.trimAllWhitespace(query));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response, AutoComplete.class);
    }
}
