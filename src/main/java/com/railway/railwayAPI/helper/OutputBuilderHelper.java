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

    /**
     * Builds the recommended train (redBus "recommendation") if present.
     * Returns null when the route has no recommendation.
     */
    public static Train getRecommendation(SearchInput searchInput, Map<String, Object> map, String cls, String update) {
        Map<String, Object> details = getDetails(map);
        if (details == null) {
            return null;
        }
        Object recObj = details.get("recommendation");
        if (!(recObj instanceof List) || ((List<?>) recObj).isEmpty()) {
            return null;
        }
        Map<String, Object> recMap = (Map<String, Object>) ((List<?>) recObj).get(0);
        try {
            Train train = buildTrain(searchInput, recMap, cls, update);
            return train;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Extracts the tag list from the first recommendation entry (e.g. "Handpicked for you"). */
    public static List<String> getRecommendationTags(Map<String, Object> map) {
        Map<String, Object> details = getDetails(map);
        if (details == null) {
            return null;
        }
        Object recObj = details.get("recommendation");
        if (!(recObj instanceof List) || ((List<?>) recObj).isEmpty()) {
            return null;
        }
        Map<String, Object> recMap = (Map<String, Object>) ((List<?>) recObj).get(0);
        Object tags = recMap.get("tags");
        return tags instanceof List ? (List<String>) tags : null;
    }

    /** Passes a details-level structure straight through (filters, sort, quotas, offers). */
    public static Object getPassthrough(Map<String, Object> map, String key) {
        Map<String, Object> details = getDetails(map);
        return details != null ? details.get(key) : null;
    }

    /**
     * Maps the raw redBus {@code "Status"} object (e.g. {@code {StatusCode, StatusMsg}})
     * into a typed {@link com.railway.railwayAPI.model.ResponseStatus}. Tolerant of
     * missing/odd shapes — returns null when there is nothing usable.
     */
    public static com.railway.railwayAPI.model.ResponseStatus buildResponseStatus(Object raw) {
        if (!(raw instanceof Map)) {
            return null;
        }
        Map<?, ?> statusMap = (Map<?, ?>) raw;
        com.railway.railwayAPI.model.ResponseStatus status = new com.railway.railwayAPI.model.ResponseStatus();
        Object code = statusMap.get("StatusCode");
        if (code instanceof Number) {
            status.setStatusCode(((Number) code).intValue());
        }
        Object msg = statusMap.get("StatusMsg");
        if (msg != null) {
            status.setStatusMsg(String.valueOf(msg));
        }
        return status;
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
        // --- Journey richness (Feature 6) ---
        train.setDistance(asInteger(trainMap.get("distance")));
        train.setIsFastest((Boolean) trainMap.getOrDefault("isFastest", null));
        train.setIsPopular((Boolean) trainMap.getOrDefault("isPopular", null));
        train.setBoardingHaltTime((String) trainMap.getOrDefault("boardingHaltTime", null));
        train.setDroppingHaltTime((String) trainMap.getOrDefault("droppingHaltTime", null));
        train.setTrainType(trainMap.get("trainType") instanceof List ? (List<String>) trainMap.get("trainType") : null);
        train.setDepartureTimeEpochInSec(asLong(trainMap.get("departureTimeEpochInSec")));
        train.setArrivalTimeEpochInSec(asLong(trainMap.get("arrivalTimeEpochInSec")));
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

        // --- Confirmation prediction (Feature 1) ---
        availablity.setPredictionPercentage(asInteger(availabilityMap.get("predictionPercentage")));
        availablity.setRacCnfPredictionPercentage(asInteger(availabilityMap.get("racCnfPredictionPercentage")));
        availablity.setLbPredictionPercentage(asDouble(availabilityMap.get("lbPredictionPercentage")));
        availablity.setLbPredictionData((String) availabilityMap.getOrDefault("lbPredictionData", null));

        // --- Fare transparency (Feature 2) ---
        availablity.setOriginalFare(asInteger(availabilityMap.get("originalFare")));
        availablity.setFareDifference(asInteger(availabilityMap.get("fareDifference")));

        // --- Boarding / dropping override (Feature 7) ---
        availablity.setFrmStnName((String) availabilityMap.getOrDefault("frmStnName", null));
        availablity.setFrmStnCode((String) availabilityMap.getOrDefault("frmStnCode", null));
        availablity.setFrmStnDepartureTime((String) availabilityMap.getOrDefault("frmStnDepartureTime", null));
        availablity.setFrmStnDepartureDate((String) availabilityMap.getOrDefault("frmStnDepartureDate", null));
        availablity.setToStnName((String) availabilityMap.getOrDefault("toStnName", null));
        availablity.setToStnCode((String) availabilityMap.getOrDefault("toStnCode", null));
        availablity.setToStnArrivalTime((String) availabilityMap.getOrDefault("toStnArrivalTime", null));
        availablity.setToStnArrivalDate((String) availabilityMap.getOrDefault("toSntArrivalDate", null));

        // --- Extra classification / Tatkal context ---
        availablity.setClassType((String) availabilityMap.getOrDefault("classType", null));
        availablity.setCurrentBkgFlag((String) availabilityMap.getOrDefault("currentBkgFlag", null));
        availablity.setWlType(availabilityMap.get("wlType") != null ? String.valueOf(availabilityMap.get("wlType")) : null);
        availablity.setTgType(asInteger(availabilityMap.get("tgType")));
        availablity.setTgPremiumPercentage(asInteger(availabilityMap.get("tgPremiumPercentage")));
        availablity.setIsRacSg((Boolean) availabilityMap.getOrDefault("isRacSg", null));
        return availablity;
    }

    // ---- null-safe numeric coercion (redBus mixes Integer/Double/String) ----
    private static Integer asInteger(Object o) {
        if (o == null) return null;
        if (o instanceof Number) return ((Number) o).intValue();
        try { return Integer.valueOf(String.valueOf(o).trim()); } catch (NumberFormatException e) { return null; }
    }

    private static Long asLong(Object o) {
        if (o == null) return null;
        if (o instanceof Number) return ((Number) o).longValue();
        try { return Long.valueOf(String.valueOf(o).trim()); } catch (NumberFormatException e) { return null; }
    }

    private static Double asDouble(Object o) {
        if (o == null) return null;
        if (o instanceof Number) return ((Number) o).doubleValue();
        try { return Double.valueOf(String.valueOf(o).trim()); } catch (NumberFormatException e) { return null; }
    }
}
