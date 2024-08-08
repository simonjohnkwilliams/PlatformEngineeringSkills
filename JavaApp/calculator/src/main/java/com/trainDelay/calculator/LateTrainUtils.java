package com.trainDelay.calculator;

import java.util.*;
import java.nio.file.Paths;
import java.io.IOException;

public class LateTrainUtils {

    public static List<LateObject> generateLateTrainObject(String departureLocation, String arrivalLocation, Map<String, Object> serviceAttribute) {
        Map<String, Object> serviceAttributesDetails = (Map<String, Object>) serviceAttribute.get("serviceAttributesDetails");
        List<Map<String, String>> locationsList = (List<Map<String, String>>) serviceAttributesDetails.get("locations");
        List<LateObject> lateObjectArray = new ArrayList<>();
        for (Map<String, String> item : locationsList) {
            if (departureLocation.equals(item.get("location")) || arrivalLocation.equals(item.get("location"))) {
                LateObject lateObjectInstance = new LateObject();
                lateObjectInstance.setActualTa(item.get("actual_ta"));
                lateObjectInstance.setActualTd(item.get("actual_td"));
                lateObjectInstance.setGbttPta(item.get("gbtt_pta"));
                lateObjectInstance.setGbttPtd(item.get("gbtt_ptd"));
                lateObjectInstance.setLateCancReason(item.get("late_canc_reason"));
                lateObjectInstance.setLocation(item.get("location"));
                lateObjectInstance.setDateOfService(serviceAttributesDetails.get("date_of_service").toString());
                lateObjectInstance.setDepartureStation(departureLocation.equals(item.get("location")));
                if (!item.get("actual_ta").isEmpty() && !item.get("gbtt_pta").isEmpty()) {
                    lateObjectInstance.setDelayTime(LateObject.calculateDelay(item.get("actual_ta"), item.get("gbtt_pta")));
                } else {
                    lateObjectInstance.setDelayTime(0);
                }
                if (lateObjectInstance.getDelayTime() > 1) {
                    lateObjectArray.add(lateObjectInstance);
                }
            }
        }
        return lateObjectArray;
    }

    public static Map<String, List<LateObject>> getLatestTrainObject(Map<String, List<List<LateObject>>> listOfTrainObjects) {
        Map<String, List<LateObject>> returnTrainObjectList = new HashMap<>();
        for (List<List<LateObject>> dayList : listOfTrainObjects.values()) {
            for (List<LateObject> timeList : dayList) {
                if (timeList.size() == 2) {
                    LateObject currentDeparture = timeList.get(0);
                    LateObject currentArrival = timeList.get(1);
                    if (!returnTrainObjectList.containsKey(currentArrival.getDateOfService())) {
                        returnTrainObjectList.put(currentArrival.getDateOfService(), Arrays.asList(currentDeparture, currentArrival));
                        continue;
                    }
                    for (List<LateObject> trainArray : returnTrainObjectList.values()) {
                        LateObject trainObjectArrival = trainArray.get(1);
                        if (trainObjectArrival.getDelayTime() < currentArrival.getDelayTime()) {
                            returnTrainObjectList.put(currentArrival.getDateOfService(), Arrays.asList(currentDeparture, currentArrival));
                        }
                    }
                }
            }
        }
        return returnTrainObjectList;
    }

    public static void writeLateTrainsToFile(String fileName, Map<String, List<LateObject>> trainObjectList, String arrival) {
        StringBuilder output = new StringBuilder("Outbound Train To " + arrival + "\n");
        output.append("Date, Departure Time, Delay Time\n");
        for (String key : trainObjectList.keySet()) {
            List<LateObject> vars = trainObjectList.get(key);
            LateObject departTrain = vars.get(0).isDepartureStation() ? vars.get(0) : vars.get(1);
            LateObject arrivalTrain = vars.get(0).isDepartureStation() ? vars.get(1) : vars.get(0);
            if (departTrain.getDateOfService() != null) {
                output.append(departTrain.getDateOfService()).append(",").append(departTrain.getGbttPtd()).append(",").append(arrivalTrain.getDelayTime()).append("\n");
            }
        }
        try {
            ServiceMetrics.writeFile(Paths.get(System.getProperty("user.dir"), "Results", fileName + ".csv").toString(), output.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, List<List<LateObject>>> trimToRouteOnlyDictionary(
            List<Object> listOfAllTrainTimes, String departureStation, String arrivalLocation) {
        Map<String, List<List<LateObject>>> lateTrainDictionary = new HashMap<>();

        for (Object serviceAttribute : listOfAllTrainTimes) {
            if (serviceAttribute != null) {
                Map<String, Object> serviceAttributeMap = (Map<String, Object>) serviceAttribute;
                if (serviceAttributeMap.containsKey("SERVICE_ATTRIBUTES_DETAILS")) {
                    Map<String, Object> serviceAttributesDetails = (Map<String, Object>) serviceAttributeMap.get("SERVICE_ATTRIBUTES_DETAILS");
                    if (serviceAttributesDetails.containsKey("DATE_OF_SERVICE") && serviceAttributesDetails.containsKey("LOCATIONS")) {
                        List<LateObject> lateTrainArray = generateLateTrainObject(departureStation, arrivalLocation, serviceAttributeMap);
                        if (lateTrainArray.size() == 2 && lateTrainArray.get(0) != null && lateTrainArray.get(1) != null) {
                            String dateOfService = lateTrainArray.get(0).getDateOfService();
                            if (lateTrainDictionary.containsKey(dateOfService)) {
                                List<List<LateObject>> ltl = lateTrainDictionary.get(dateOfService);
                                ltl.add(lateTrainArray);
                                lateTrainDictionary.put(dateOfService, ltl);
                            } else {
                                List<List<LateObject>> newList = new ArrayList<>();
                                newList.add(lateTrainArray);
                                lateTrainDictionary.put(dateOfService, newList);
                            }
                        }
                    }
                }
            }
        }
        return lateTrainDictionary;
    }
}