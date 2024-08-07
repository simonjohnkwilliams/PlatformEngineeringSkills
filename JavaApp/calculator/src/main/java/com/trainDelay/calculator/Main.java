package com.trainDelay.calculator;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void newMain(Map<String, Object> serviceDetailsMap) throws IOException {
        for (Map.Entry<String, Object> entry : serviceDetailsMap.entrySet()) {
            Map<String, Object> value = (Map<String, Object>) entry.getValue();
            CalculatorFileUtils.clearOldData((Boolean) value.get("CLEAR_OLD_DATA_STRING"));

            Map<String, Object> params = new HashMap<>();
            params.put("fromStation", value.get("DEPARTURE_STATION"));
            params.put("toStation", value.get("TO_LOCATION_STRING"));
            params.put("fromTime", value.get("START_TIME"));
            params.put("toTime", value.get("END_TIME"));
            params.put("toDate", value.get("START_DATE_STRING"));
            params.put("daysDifference", value.get("DAYS_BACK_STRING"));
            params.put("fileName", value.get("FILE_LOCATION_STRING"));
            ServiceMetrics.writeServiceMetricsTestData(params);

            Map<String, Object> params2 = new HashMap<>();
            params2.put("pidList", JsonUtils.generatePidList((String) value.get("DIR_LOCATION_STRING")));
            params2.put("outboundServiceMessage", ServiceMetrics.OUTBOUND_SERVICE_MESSAGE);
            ServiceMetrics.writeServiceMetricsTestData(params2);

            List<Object> listOfAllTrainTimes = JsonUtils.generateAttributeDictionary(ServiceMetrics.OUTBOUND_SERVICE_MESSAGE_DIR);
            Map<String, List<List<LateObject>>> routeDictionary = LateTrainUtils.trimToRouteOnlyDictionary(
                    listOfAllTrainTimes,
                    (String) value.get("DEPARTURE_STATION"),
                    (String) value.get("TO_LOCATION_STRING")
            );
            Map<String, List<LateObject>> latestTrainDictionary = LateTrainUtils.getLatestTrainObject(routeDictionary);
            LateTrainUtils.writeLateTrainsToFile(
                    (String) value.get("RESULT_FILE_STRING"),
                    latestTrainDictionary,
                    (String) value.get("TO_LOCATION_STRING")
            );
            System.out.println("Completed " + value.get("RESULT_FILE_STRING"));
        }
    }

    public static void main(String[] args) throws IOException {
        //this is a test
        Map<String, Object> serviceDetailsMap = JsonGenerator.getJson();
        newMain(serviceDetailsMap);
        System.out.println("All complete");
    }
}