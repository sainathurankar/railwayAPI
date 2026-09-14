package com.railway.railwayAPI.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Proxy over the redBus "RIS" (rail information services) API hosted at
 * {@code loco.redbus.com}, which backs PNR status, live running status, train
 * schedule and coach position. This is a different host from the search API
 * ({@link Facade}) and requires the redBus mobile-app headers to respond.
 *
 * <p>Endpoint paths were confirmed against live traffic; responses are returned
 * as parsed JSON (Map) passthrough so we never silently drop fields redBus adds
 * — the typed shaping happens at the frontend rendering layer.
 *
 * <p>Base URL and the {@code Auth_key} header are configurable
 * ({@code redbus.ris.url} / {@code REDBUS_RIS_URL},
 * {@code redbus.ris.authKey} / {@code REDBUS_RIS_AUTH_KEY}); the values observed
 * on the live mobile app are the defaults.
 */
public class RisFacade {

    private static final Logger logger = LoggerFactory.getLogger(RisFacade.class);

    private static final String DEFAULT_BASE_URL = "https://loco.redbus.com";
    private static final String DEFAULT_AUTH_KEY = "1";

    private static final RestTemplate REST_TEMPLATE = HttpClientFactory.pooledRestTemplate();

    private final String baseUrl = resolve("redbus.ris.url", "REDBUS_RIS_URL", DEFAULT_BASE_URL);
    private final String authKey = resolve("redbus.ris.authKey", "REDBUS_RIS_AUTH_KEY", DEFAULT_AUTH_KEY);

    private static String resolve(String prop, String env, String def) {
        String v = System.getProperty(prop);
        if (!StringUtils.hasText(v)) {
            v = System.getenv(env);
        }
        return StringUtils.hasText(v) ? v : def;
    }

    private HttpHeaders risHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.set("Channel_name", "MOBILE_APP");
        h.set("Os", "Android");
        h.set("Appversion", "5.5.1");
        h.set("Auth_key", authKey);
        h.set("Appversioncode", "505010");
        h.set("Language", "en");
        h.set("Businessunit", "REDRAIL");
        h.set("Currency", "INR");
        h.set("Country", "India");
        h.set("Country_name", "IND");
        h.set("User-Agent", "okhttp/4.11.0");
        h.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        return h;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> get(String path) {
        String url = baseUrl + path;
        logger.info("GET RIS call to {}", url);
        ResponseEntity<Object> resp = REST_TEMPLATE.exchange(
                url, HttpMethod.GET, new HttpEntity<>(risHeaders()), Object.class);
        return asMap(resp.getBody());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> post(String path, Object body) {
        String url = baseUrl + path;
        logger.info("POST RIS call to {}", url);
        HttpHeaders headers = risHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Object> resp = REST_TEMPLATE.exchange(
                url, HttpMethod.POST, new HttpEntity<>(body, headers), Object.class);
        return asMap(resp.getBody());
    }

    private static Map<String, Object> asMap(Object body) {
        if (body instanceof Map) {
            return (Map<String, Object>) body;
        }
        Map<String, Object> wrapper = new LinkedHashMap<>();
        wrapper.put("data", body);
        return wrapper;
    }

    /** PNR status. redBus returns 4xx with an error body for an invalid PNR. */
    public Map<String, Object> getPnrStatus(String pnr, String mobile) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("pnr", pnr);
        body.put("mobile", mobile == null ? "" : mobile);
        return post("/api/Rails/v1/RIS/PnrToolkit", body);
    }

    /** Full station-by-station schedule for a train number. */
    public Map<String, Object> getTrainSchedule(String trainNo) {
        return get("/api/Rails/v1/RIS/GetTrainSchedule/" + trainNo);
    }

    /** Live running status (current position, delays, per-station timeline). */
    public Map<String, Object> getLiveTrainStatus(String trainNo) {
        String path = UriComponentsBuilder.fromPath("/api/Rails/v2/RIS/GetLiveTrainStatus/")
                .queryParam("trainNo", trainNo)
                .toUriString();
        return get(path);
    }

    /** Coach position (rake layout) for a train at a station. */
    public Map<String, Object> getCoachPosition(String trainNo, String stn) {
        String path = UriComponentsBuilder.fromPath("/api/Rails/v1/RIS/GetCoachPosition")
                .queryParam("trainNo", trainNo)
                .queryParam("stn", stn)
                .toUriString();
        return get(path);
    }
}
