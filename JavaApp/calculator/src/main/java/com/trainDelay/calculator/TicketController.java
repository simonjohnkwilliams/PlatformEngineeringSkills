package com.trainDelay.calculator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

// This class is a controller for handling ticket requests
@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    /**
     *
     * @param ticketRequest
     * @return
     *
     * This class is the entry point for the ticket request. It takes in a JSON object and returns a map of late trains.
     */
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, List<LateObject>>> handleTicketRequest(@RequestBody Map<String, Object> ticketRequest) {
        List <String> pidList = ServiceMetrics.getServiceMetricsDetailsForJourney(ticketRequest);
        List<String> listOfAllTrainTimes = ServiceMetrics.writeAttributeMessageTestData(pidList);

        // Trim all the data for the route
        Map<String, List<List<LateObject>>> spcificRouteMap = LateTrainUtils.trimToRouteOnlyDictionary(listOfAllTrainTimes, (String) ticketRequest.get("fromStation"), (String) ticketRequest.get("toStation"));
        Map<String, List<LateObject>> lateTrainsMap = LateTrainUtils.getLatestTrainObject(spcificRouteMap);

        LateTrainUtils.writeLateTrainsToFile((String) ticketRequest.get("fileName"), lateTrainsMap, (String) ticketRequest.get("toStation"));

        // Return the map as the response
        return new ResponseEntity<>(lateTrainsMap, HttpStatus.OK);
    }
}
