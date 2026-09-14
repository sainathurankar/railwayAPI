package com.railway.railwayAPI.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.railway.railwayAPI.facade.RisFacade;
import com.railway.railwayAPI.service.RisService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

@Service
public class RisServiceImpl implements RisService {

    private final RisFacade risFacade = new RisFacade();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> getPnrStatus(String pnr, String mobile) {
        // redBus returns 4xx with a JSON error body (e.g. {errorcode, errormsg,
        // detailedmsg}) for an invalid/expired PNR — surface that as-is rather
        // than a 500, so the frontend can show the real message.
        try {
            return risFacade.getPnrStatus(pnr, mobile);
        } catch (RestClientResponseException e) {
            return errorBody(e);
        }
    }

    @Override
    public Map<String, Object> getTrainSchedule(String trainNo) {
        try {
            return risFacade.getTrainSchedule(trainNo);
        } catch (RestClientResponseException e) {
            return errorBody(e);
        }
    }

    @Override
    public Map<String, Object> getLiveTrainStatus(String trainNo) {
        try {
            return risFacade.getLiveTrainStatus(trainNo);
        } catch (RestClientResponseException e) {
            return errorBody(e);
        }
    }

    @Override
    public Map<String, Object> getCoachPosition(String trainNo, String stn) {
        try {
            return risFacade.getCoachPosition(trainNo, stn);
        } catch (RestClientResponseException e) {
            return errorBody(e);
        }
    }

    /** Parse an upstream error body into a Map, falling back to a generic shape. */
    @SuppressWarnings("unchecked")
    private Map<String, Object> errorBody(RestClientResponseException e) {
        String raw = e.getResponseBodyAsString();
        try {
            if (raw != null && !raw.isBlank()) {
                return objectMapper.readValue(raw, Map.class);
            }
        } catch (Exception ignore) {
            // fall through to generic shape
        }
        return Map.of(
                "errorcode", String.valueOf(e.getRawStatusCode()),
                "errormsg", "Upstream error",
                "detailedmsg", raw == null ? "" : raw);
    }
}
