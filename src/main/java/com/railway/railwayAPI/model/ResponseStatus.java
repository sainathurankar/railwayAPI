package com.railway.railwayAPI.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Top-level status block returned by redBus (its {@code "Status"} object),
 * e.g. {@code {"StatusCode": 200, "StatusMsg": "OK"}}.
 *
 * <p>Replaces the previous raw {@code Object status} field so the contract is
 * explicit and matches the frontend's {@code SearchResponse.status} type.
 * Unknown properties are ignored to stay forward-compatible with redBus.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseStatus {
    @JsonProperty("StatusCode")
    private Integer StatusCode;
    @JsonProperty("StatusMsg")
    private String StatusMsg;
}
