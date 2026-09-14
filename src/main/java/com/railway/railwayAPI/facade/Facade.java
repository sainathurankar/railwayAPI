package com.railway.railwayAPI.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.railway.railwayAPI.model.SearchInput;
import com.railway.railwayAPI.model.internal.AutoComplete;
import com.railway.railwayAPI.model.internal.TrainUpdateInput;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Thin proxy over the redBus railways API.
 *
 * <p>All outbound calls share a single {@link RestTemplate} backed by a pooled
 * Apache HttpClient 5 connection manager with explicit connect / read timeouts,
 * so a slow or hung redBus response can no longer exhaust threads or block a
 * request indefinitely (the old code created a fresh {@code new RestTemplate()}
 * per call, which has no timeouts and no connection reuse).
 *
 * <p>The redBus base URL is read from configuration (system property or the
 * {@code REDBUS_API_URL} environment variable) so it is no longer hard-coded;
 * the historical value remains the default.
 */
public class Facade {

    private static final Logger logger = LoggerFactory.getLogger(Facade.class);

    /** Historical hard-coded value, kept as the default. */
    private static final String DEFAULT_API_URL = "https://www.redbus.in/railways/api";

    /** Connect / connection-request / read timeouts (ms) — overridable via system properties. */
    private static final int CONNECT_TIMEOUT_MS =
            Integer.getInteger("redbus.http.connectTimeoutMs", 5_000);
    private static final int READ_TIMEOUT_MS =
            Integer.getInteger("redbus.http.readTimeoutMs", 15_000);
    private static final int MAX_TOTAL_CONNECTIONS =
            Integer.getInteger("redbus.http.maxTotal", 50);
    private static final int MAX_CONNECTIONS_PER_ROUTE =
            Integer.getInteger("redbus.http.maxPerRoute", 20);

    /** Shared, thread-safe, pooled RestTemplate for every redBus call. */
    private static final RestTemplate REST_TEMPLATE = buildRestTemplate();

    private final String apiUrl = resolveApiUrl();

    private static String resolveApiUrl() {
        String configured = System.getProperty("redbus.api.url");
        if (!StringUtils.hasText(configured)) {
            configured = System.getenv("REDBUS_API_URL");
        }
        return StringUtils.hasText(configured) ? configured : DEFAULT_API_URL;
    }

    private static RestTemplate buildRestTemplate() {
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(CONNECT_TIMEOUT_MS))
                .setSocketTimeout(Timeout.ofMilliseconds(READ_TIMEOUT_MS))
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        connectionManager.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);
        connectionManager.setDefaultConnectionConfig(connectionConfig);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(CONNECT_TIMEOUT_MS))
                .setResponseTimeout(Timeout.ofMilliseconds(READ_TIMEOUT_MS))
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .evictIdleConnections(TimeValue.ofSeconds(30))
                .evictExpiredConnections()
                .build();

        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(factory);
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
