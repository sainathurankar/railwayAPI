package com.railway.railwayAPI.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Train {
    private String trainName;
    private String trainNumber;
    private String departureTime;
    private String arrivalTime;
    private String duration;
    private String departureDate;
    private String arrivalDate;
    private List<Availability> availabilitiesList;
    private String fromStation;
    private String toStation;
    private String fromStationCode;
    private String toStationCode;
    private List<String> availableClasses;
    private Boolean isAlternate;
    private String runningMon;
    private String runningTue;
    private String runningWed;
    private String runningThu;
    private String runningFri;
    private String runningSat;
    private String runningSun;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = this.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                map.put(field.getName(), (Object) field.get(this));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return map;
    }
}

