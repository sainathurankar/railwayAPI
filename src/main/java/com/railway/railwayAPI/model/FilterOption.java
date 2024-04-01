package com.railway.railwayAPI.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterOption {
    private String id;
    private String displayName;
    private String value;
}
