package com.railway.railwayAPI.model.autocomplete;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AutoCompleteResponse {
    private int start;
    private boolean numFoundExact;
    private int numFound;
    private List<Map<String, Object>> results;
}
