package com.railway.railwayAPI.config.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterConfig {

    private List<Filter> filters;

    @Override
    public String toString() {
        return "FilterConfig{" +
                "filters=" + filters +
                '}';
    }
}
