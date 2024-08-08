package com.trainDelay.calculator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// This class is a controller for handling ticket requests
@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, List<LateObject>>> handleTicketRequest(@RequestBody Map<String, Object> ticketRequest) {
        // Create a list of LateObject for demonstration purposes
        //query the darwin endpoint to pull down data for all the trains.

        ServiceMetrics.writeServiceMetricsTestData(ticketRequest);
        List <String> pidList = JsonUtils.generatePidList(Paths.get("").toAbsolutePath().toString());

        ServiceMetrics.writeAttributeMessageTestData(pidList, (String) ticketRequest.get("fileName"));
        List<Object> listOfAllTrainTimes = new ArrayList<>();
        try {
            listOfAllTrainTimes = JsonUtils.generateAttributeDictionary(Paths.get("").toAbsolutePath().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Trim all the data for the route
        Map<String, List<List<LateObject>>> spcificRouteMap = LateTrainUtils.trimToRouteOnlyDictionary(listOfAllTrainTimes, (String) ticketRequest.get("fromStation"), (String) ticketRequest.get("toStation"));
        Map<String, List<LateObject>> lateTrainsMap = LateTrainUtils.getLatestTrainObject(spcificRouteMap);

        LateTrainUtils.writeLateTrainsToFile((String) ticketRequest.get("fileName"), lateTrainsMap, (String) ticketRequest.get("toStation"));

        // Return the map as the response
        return new ResponseEntity<>(lateTrainsMap, HttpStatus.OK);
    }
}
