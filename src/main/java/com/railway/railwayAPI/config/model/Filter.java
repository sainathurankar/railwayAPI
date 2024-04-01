package com.railway.railwayAPI.config.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Filter {

    private String name;
    private String value;
    private String displayName;
    private String fieldType;

    @Override
    public String toString() {
        return "Filter{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", displayName='" + displayName + '\'' +
                ", fieldType='" + fieldType + '\'' +
                '}';
    }
}
