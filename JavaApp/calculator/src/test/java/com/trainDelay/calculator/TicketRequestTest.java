package com.trainDelay.calculator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TicketRequestTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHandleTicketRequest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        TicketRequest ticketRequest = new TicketRequest();
        ticketRequest.setTicketType("Standard");
        ticketRequest.setToStation("StationA");
        ticketRequest.setFromStation("StationB");
        ticketRequest.setDate("2023-10-01");

        String ticketRequestJson = objectMapper.writeValueAsString(ticketRequest);

        MvcResult result = mockMvc.perform(post("/api/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ticketRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        // Add assertions to verify the response contains a list of LateObject as a map
    }
}
