package com.railway.railwayAPI.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponse {
    private String error;
    private Object response;
    private Object status;
    private List<String> trainList;
    private List<Train> trains;
}
