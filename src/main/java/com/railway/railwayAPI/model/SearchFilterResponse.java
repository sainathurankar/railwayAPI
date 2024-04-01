package com.railway.railwayAPI.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchFilterResponse {

    private String src;
    private String dst;
    private String doj;

    private List<Filter> filters;
}
