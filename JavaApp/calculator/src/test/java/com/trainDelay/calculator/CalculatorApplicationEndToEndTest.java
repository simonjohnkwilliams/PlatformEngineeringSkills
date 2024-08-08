package com.trainDelay.calculator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CalculatorApplicationEndToEndTest {

    @Autowired
    private MockMvc mockMvc;
    @Test
    public void testEndToEndRequestGodalmingToLondon() throws Exception {

        //create the ticket objet from the incoming Json from the front end.
        String ticketRequestJson = createJsonString();

        MvcResult result = mockMvc.perform(post("/api/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ticketRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        //this posts to the spring service and should return a json with all the details for the corresponding journeys to claim.
        String responseJson = result.getResponse().getContentAsString();

        // We should also have the results written to disk in the first instance. This will be a DB in later runs.
        String resultFilePath = "path/to/result/file.txt";
        File resultFile = new File(resultFilePath);
        assertThat(resultFile.exists()).isTrue();

        String fileContent = new String(Files.readAllBytes(Paths.get(resultFilePath)));
        // Add assertions to verify the file content
        // For example, check if the file contains specific strings
    }
    public String createJsonString() throws JsonProcessingException {
        final String fromStation = "GOD";
        final String toStation = "WAT";
        final String fromTime = "0600";
        final String toTime = "0900";
        final String fileName = "serviceMetrics";
        final LocalDate toDate = LocalDate.now().minusDays(1);

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("fromStation", fromStation);
        jsonMap.put("toStation", toStation);
        jsonMap.put("fromTime", fromTime);
        jsonMap.put("toTime", toTime);
        jsonMap.put("fileName", fileName);
        jsonMap.put("toDate", toDate.toString());

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.writeValueAsString(jsonMap);
    }

}