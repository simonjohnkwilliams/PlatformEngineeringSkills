package com.trainDelay.calculator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, List<LateObject>>> handleTicketRequest(@RequestBody TicketRequest ticketRequest) {
        // Create a list of LateObject for demonstration purposes
        List<LateObject> lateObjects = new ArrayList<>();
        lateObjects.add(new LateObject("Location1", "0800", "0830", "0815", "0845", "Delay Reason 1", "2023-10-01", 15, true));
        lateObjects.add(new LateObject("Location2", "0900", "0930", "0915", "0945", "Delay Reason 2", "2023-10-01", 15, false));

        // Create a map to hold the list of LateObject
        Map<String, List<LateObject>> responseMap = new HashMap<>();
        responseMap.put("lateObjects", lateObjects);

        // Return the map as the response
        return new ResponseEntity<>(responseMap, HttpStatus.OK);
    }
}
