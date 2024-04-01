package com.railway.railwayAPI.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.railway.railwayAPI.config.model.FilterConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SearchConfigLoader {

    private FilterConfig filterConfig;

    @PostConstruct
    public void init() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Load JSON config file from resources
            filterConfig = objectMapper.readValue(getClass().getResourceAsStream("/filtersConfig.json"), FilterConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FilterConfig getSearchConfig() {
        return filterConfig;
    }
}
