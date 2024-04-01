package com.railway.railwayAPI.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Filter {
    private String name;
    private List<FilterOption> options;
}
