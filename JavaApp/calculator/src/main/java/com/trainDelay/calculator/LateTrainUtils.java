package com.trainDelay.calculator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class LateTrainUtils {

    private static final String SERVICE_ATTRIBUTES_DETAILS = "serviceAttributesDetails";
    private static final String LOCATIONS = "locations";
    private static final String DATE_OF_SERVICE = "date_of_service";



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
                if (timeList.size() == 1) {
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
                //for return journeys
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

    public static void writeLateTrainsToFile(String fileName, Map<String, List<List<LateObject>>> trainObjectList, String arrival) {
        StringBuilder output = new StringBuilder("Outbound Train To " + arrival + "\n");
        output.append("Date, Departure Time, Delay Time\n");
        for (String dateOfService : trainObjectList.keySet()) {
            for(List<LateObject> vars : trainObjectList.get(dateOfService)){
                for(LateObject lateObject : vars){
                    if(lateObject == null){
                        continue;
                    }
                    output.append(dateOfService).append(",").append(lateObject.getGbttPta()).append(",").append(lateObject.getDelayTime()).append("\n");
                }
            }
        }
        ServiceMetrics.writeFile(Paths.get(System.getProperty("user.dir"), "Results", fileName + ".csv").toString(), output.toString());
    }

    public static void flushAllFiles(){
        // Define directories to clear
        Path[] directories = {
                Paths.get(System.getProperty("user.dir"), "Results"),
                Paths.get(System.getProperty("user.dir"), "downloaded", "inbound", "service"),
                Paths.get(System.getProperty("user.dir"), "downloaded", "outbound", "attribute")
        };

        // Iterate through each directory and delete all files
        for (Path dir : directories) {
            try {
                Files.walk(dir)
                        .filter(Files::isRegularFile)
                        .forEach(file -> {
                            try {
                                Files.delete(file);
                                System.out.println("Deleted file: " + file);
                            } catch (IOException e) {
                                System.err.println("Failed to delete file: " + file + " due to " + e.getMessage());
                            }
                        });
            } catch (IOException e) {
                System.err.println("Failed to clear directory: " + dir + " due to " + e.getMessage());
            }
        }
    }

    public static Map<String, List<List<LateObject>>> trimToRouteOnlyDictionary(
            List<String> listOfAllTrainTimes, String departureStation, String arrivalLocation) {
        Map<String, List<List<LateObject>>> lateTrainDictionary = new HashMap<>();

        for (Object serviceAttribute : listOfAllTrainTimes) {
            if (serviceAttribute != null) {
                Map<String, Object> serviceAttributeMap = JsonUtils.readJsonAsMap(serviceAttribute.toString());
                if (serviceAttributeMap.containsKey(SERVICE_ATTRIBUTES_DETAILS)) {
                    Map<String, Object> serviceAttributesDetails = (Map<String, Object>) serviceAttributeMap.get(SERVICE_ATTRIBUTES_DETAILS);
                    if (serviceAttributesDetails.containsKey(DATE_OF_SERVICE) && serviceAttributesDetails.containsKey(LOCATIONS)) {
                        List<LateObject> lateTrainArray = generateLateTrainObject(departureStation, arrivalLocation, serviceAttributeMap);
                        if (lateTrainArray.size() == 1 && lateTrainArray.get(0) != null) {
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