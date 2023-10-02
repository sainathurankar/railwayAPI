package com.railway.railwayAPI.model.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AutoComplete {
    private Map<String, Object> response;
    private Map<String, Object> responseHeader;
}
