package com.railway.railwayAPI.helper;

import com.railway.railwayAPI.facade.Facade;
import com.railway.railwayAPI.model.Availablity;
import com.railway.railwayAPI.model.SearchInput;
import com.railway.railwayAPI.model.Train;
import com.railway.railwayAPI.model.internal.TrainUpdateInput;

import java.util.*;
import java.util.stream.Collectors;

public class OutputBuilderHelper {
    private static Map<String, String> trainStatusMap = Map.of("Available", "AVBL");

    private static Facade facade = new Facade();
    public static List<String> getTrainList(SearchInput searchInput, Map<String, Object> map, String trainNumber, String cls) {
        List<Map<String, Object>> trains = getTrainsBetweenStations(map, trainNumber);
        List<String> trainList = buildTrainList(trains);
        return trainList;
    }

    private static List<String> removeDuplicates(List<String> trainList) {
        List<String> list = null;
        HashSet<String> set = new HashSet(trainList);
        trainList = set.stream().collect(Collectors.toList());
        return trainList;
    }

    private static List<String> buildTrainList(List<Map<String, Object>> trains) {
        List<String> trainList = new ArrayList<>();
        if (trains != null) {
            trains.stream().forEach(train -> {
                StringBuilder sb = new StringBuilder();
                if (train.containsKey("departureTime")) {
                    sb.append((String) train.get("departureTime") + " ");
                }
                if (train.containsKey("trainName")) {
                    sb.append((String) train.get("trainName") + " ");
                }
                if (train.containsKey("arrivalTime")) {
                    sb.append((String) train.get("arrivalTime") + " ");
                }
                trainList.add(sb.toString());
            });
        }
        return removeDuplicates(trainList);
    }

    private static List<Map<String, Object>> getTrainsBetweenStations(Map<String, Object> map, String trainNumber) {
        List<Map<String, Object>> trains = null;
        if (map.containsKey("Data")) {
            Map<String, Object> data = (Map<String, Object>) map.get("Data");
            if (data.containsKey("details")) {
                Map<String, Object> details = (Map<String, Object>) data.get("details");
                if (details.containsKey("trainBtwnStnsList")) {
                    trains = (List<Map<String, Object>>) details.get("trainBtwnStnsList");
                    if (trainNumber != null) {
                        List<String> trainNumberList = List.of(trainNumber.split(","));
                        trains = trains.stream().filter(t -> trainNumberList.contains((String) t.get("trainNumber"))).collect(Collectors.toList());
                    }
                    trains = trains.stream().filter(t -> !(Boolean) t.get("clusterTrain")).collect(Collectors.toList());
                }
            }
        }
        return trains;
    }

    public static Map<String, Object> getDetails(Map<String, Object> map) {
        if (map.containsKey("Data")) {
            Map<String, Object> data = (Map<String, Object>) map.get("Data");
            if (data.containsKey("details")) {
                return (Map<String, Object>) data.get("details");
            }
        }
        return null;
    }

    public static List<Train> getTrainListV2(SearchInput searchInput, Map<String, Object> map, String trainNumber, String cls, String update) {
        List<Map<String, Object>> trains = getTrainsBetweenStations(map, trainNumber);
        List<Train> trainList = buildTrainListV2(searchInput, trains, cls, update);
        return trainList;
    }

    private static List<Train> buildTrainListV2(SearchInput searchInput, List<Map<String, Object>> trains, String cls, String update) {
        List<Train> trainList = new ArrayList<>();
        if (trains != null) {
            trains.stream().forEach(train -> {
                try {
                    trainList.add(buildTrain(searchInput, train, cls, update));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return trainList;
    }

    private static Train buildTrain(SearchInput searchInput, Map<String, Object> trainMap, String cls, String update) throws Exception {
        Train train = new Train();
        train.setTrainName((String) trainMap.get("trainName"));
        train.setTrainNumber((String) trainMap.get("trainNumber"));
        train.setDepartureTime((String) trainMap.get("departureTime"));
        train.setArrivalTime((String) trainMap.get("arrivalTime"));
        train.setFromStation((String) trainMap.get("fromStnName"));
        train.setToStation((String) trainMap.get("toStnName"));
        train.setFromStationCode((String) trainMap.get("fromStnCode"));
        train.setToStationCode((String) trainMap.get("toStnCode"));
        train.setDepartureDate((String) trainMap.get("departureDate"));
        train.setArrivalDate((String) trainMap.get("arrivalDate"));
        train.setDuration((String) trainMap.get("duration"));
        train.setAvailableClasses((List<String>) trainMap.get("avlClasses"));
        train.setIsAlternate((Boolean) trainMap.get("isAlternate"));
        train.setAvailabilitiesList(buildAvailabiltyList(searchInput, trainMap, (List<Map<String, Object>>) trainMap.get("tbsAvailability"), cls, update));
        train.setRunningMon((String) trainMap.get("runningMon"));
        train.setRunningTue((String) trainMap.get("runningTue"));
        train.setRunningWed((String) trainMap.get("runningWed"));
        train.setRunningThu((String) trainMap.get("runningThu"));
        train.setRunningFri((String) trainMap.get("runningFri"));
        train.setRunningSat((String) trainMap.get("runningSat"));
        train.setRunningSun((String) trainMap.get("runningSun"));
        return train;
    }

    private static List<Availablity> buildAvailabiltyList(SearchInput searchInput, Map<String, Object> trainMap, List<Map<String, Object>> tbsAvailability, String cls, String update) throws Exception {
        List<Availablity> availablities = new ArrayList<>();
        try {
            tbsAvailability.stream().filter(availabilityMap -> {
                Object className = availabilityMap.get("className");
                return cls == null || (className != null && cls.equalsIgnoreCase(className.toString()));
            }).forEach(availabilityMap -> {
                TrainUpdateInput trainUpdateInput = buildTrainUpdateInput(searchInput, trainMap, availabilityMap);
                Map<String, Object> avail = new LinkedHashMap<>();
                if (Boolean.valueOf(update)) {
                    avail = getDetails((Map<String, Object>) facade.getTrainUpdates(trainUpdateInput).get("Response"));
                } else {
                    avail = availabilityMap;
                }
                availablities.add(buildAvailabilty(avail));
            });
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return availablities;
    }

    private static TrainUpdateInput buildTrainUpdateInput(SearchInput searchInput, Map<String, Object> trainMap, Map<String, Object> availabilityMap) {
        return new TrainUpdateInput((String) availabilityMap.get("quota"), (String) trainMap.get("toStnCode"), (String) trainMap.get("fromStnCode"), (String) trainMap.get("trainNumber"), (String) availabilityMap.get("className"), searchInput.getDoj());
    }

    public static Availablity buildAvailabilty(Map<String, Object> availabilityMap) {
        Availablity availablity = new Availablity();
        availablity.setQuota((String) availabilityMap.get("quota"));
        availablity.setClassName((String) availabilityMap.get("className"));
        availablity.setStatus(trainStatusMap.getOrDefault((String) availabilityMap.get("availablityStatus"), (String) availabilityMap.get("availablityStatus")));
        availablity.setSeats((String) availabilityMap.get("availablityNumber"));
        availablity.setFare(String.valueOf(availabilityMap.get("totalFare")));
        availablity.setLastUpdatedOn((String) availabilityMap.get("lastUpdatedOn"));
        availablity.setAvailablityDate((String) availabilityMap.get("availablityDate"));
        if (availabilityMap.get("lastUpdatedOnRaw") != null) {
            availablity.setLastUpdatedOnRaw(Long.valueOf(String.valueOf(availabilityMap.get("lastUpdatedOnRaw"))));
        }
        availablity.setAvailablityType((String) availabilityMap.getOrDefault("availablityType", null));
        availablity.setTG((boolean) availabilityMap.getOrDefault("isTG", false));
        availablity.setReasonType((String) availabilityMap.getOrDefault("reasonType", null));
        return availablity;
    }
}
