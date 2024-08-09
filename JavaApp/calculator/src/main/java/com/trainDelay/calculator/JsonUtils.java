package com.trainDelay.calculator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class JsonUtils {

    public static Map <String, Object> readJsonFromFile(String fileName) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(new File(fileName), Map.class);
        } catch (IOException e) {
            return new HashMap<String, Object>();
        }
    }

    public static Map <String, Object> readJsonAsMap(String ticketJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(ticketJson, Map.class);
        } catch (JsonProcessingException e) {
            return new HashMap<String, Object>();
        }
    }

    public static String convertMapToJsonString(Map<String, Object> map) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> generateAttributeDictionary(String directory) {
        try{
            File dir = new File(directory);
            File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
            ObjectMapper objectMapper = new ObjectMapper();
            List<String> jsonList = new ArrayList<>();
            if (files != null) {
                for (File file : files) {
                    jsonList.add(generateJsonStringFromFile(file));
                }
            }
            return jsonList;
        }
        catch (Exception e){
            System.out.println("Error in generating attribute dictionary");
        }
        return null;
    }

    public static String generateJsonStringFromFile(File file) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return convertMapToJsonString(objectMapper.readValue(file, Map.class));
        } catch (IOException e) {
            return "";
        }
    }

    public static List<String> generatePidList(String dir)  {
        List<String> ridList = new ArrayList<>();
        File folder = new File(dir);
        File[] files = folder.listFiles((d, name) -> name.endsWith(".json"));

        if (files != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            for (File file : files) {
                Map<String, Object> jsonMap = null;
                try {
                    jsonMap = objectMapper.readValue(file, Map.class);
                } catch (Exception e) {
                    System.out.println("Error in reading json file" + file.getName() + " skipping");
                    continue;
                }
                List<Map<String, Object>> services = (List<Map<String, Object>>) jsonMap.get("Services");
                if (services==null || services.isEmpty()) {
                    continue;
                }
                for (Map<String, Object> service : services) {
                    if (service.containsKey("serviceAttributesMetrics")) {
                        Map<String, Object> ridsDict = (Map<String, Object>) service.get("serviceAttributesMetrics");
                        if (ridsDict.containsKey("rids")) {
                            List<String> rList = (List<String>) ridsDict.get("rids");
                            if (!rList.isEmpty()) {
                                ridList.add(rList.get(0));
                            }
                        }
                    }
                }
            }
        }
        ridList = removeDuplicates(ridList);
        return ridList;
    }

    public static List<String> generatePidListFromJson(String json) {
    List<String> ridList = new ArrayList<>();
    ObjectMapper objectMapper = new ObjectMapper();
    try {
        Map<String, Object> jsonMap = objectMapper.readValue(json, Map.class);
        List<Map<String, Object>> services = (List<Map<String, Object>>) jsonMap.get("Services");
        if (services != null) {
            for (Map<String, Object> service : services) {
                Map<String, Object> serviceAttributesMetrics = (Map<String, Object>) service.get("serviceAttributesMetrics");
                if (serviceAttributesMetrics != null) {
                    List<String> rids = (List<String>) serviceAttributesMetrics.get("rids");
                    if (rids != null && !rids.isEmpty()) {
                        ridList.add(rids.get(0));
                    }
                }
            }
        }
    } catch (IOException e) {
        System.out.println("Error in reading json file: " + e.getMessage());
    }
    return removeDuplicates(ridList);
}
    static List<String> removeDuplicates(List<String> ridList) {
        return new ArrayList<String>(new HashSet<String>(ridList));
    }
}
