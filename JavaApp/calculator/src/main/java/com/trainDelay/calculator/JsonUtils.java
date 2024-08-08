package com.trainDelay.calculator;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    public static Object readJson(String fileName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(fileName), Object.class);
    }

    public static Map <String, Object> readJsonAsMap(String ticketJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(ticketJson, Map.class);
    }

    public static List<Object> generateAttributeDictionary(String directory) throws IOException {
        try{
            File dir = new File(directory);
            File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
            ObjectMapper objectMapper = new ObjectMapper();
            List<Object> jsonList = new ArrayList<>();
            if (files != null) {
                for (File file : files) {
                    jsonList.add(objectMapper.readValue(file, Object.class));
                }
            }
            return jsonList;
        }
        catch (IOException e){
            System.out.println("Error in generating attribute dictionary");
        }
        return null;
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
                List<Map<String, Object>> services = (List<Map<String, Object>>) jsonMap.get("Services")==null? (List<Map<String, Object>>) jsonMap.get("Services") : new ArrayList<Map<String, Object>>();
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

    private static List<String> removeDuplicates(List<String> ridList) {
        return new ArrayList<String>(new HashSet<String>(ridList));
    }
}
