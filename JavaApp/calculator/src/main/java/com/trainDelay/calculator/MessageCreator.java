package com.trainDelay.calculator;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessageCreator {

    private static final String SERVICE_ATTRIBUTES_OUTBOUND = "../test/files/serviceAttributesOutbound.txt";
    private static final String SERVICE_ATTRIBUTES_INBOUND_TEMPLATE = "../test/files/serviceAttributesInbound%d.json";
    private static final String SERVICE_ATTRIBUTES_OUTBOUND_TESTDATA_TEMPLATE = "../test/files/serviceAttributesOutboundTestdata%d.txt";
    private static final int FILE_COUNT = 40;

    public Object getServiceMessage() throws IOException {
        return readJson(SERVICE_ATTRIBUTES_OUTBOUND);
    }

    public List<Object> getServiceAttributes() throws IOException {
        List<Object> jsonList = new ArrayList<>();
        for (int counter = 1; counter < FILE_COUNT; counter++) {
            System.out.println("Reading file = " + String.format(SERVICE_ATTRIBUTES_INBOUND_TEMPLATE, counter));
            jsonList.add(readJson(String.format(SERVICE_ATTRIBUTES_OUTBOUND_TESTDATA_TEMPLATE, counter)));
        }
        return jsonList;
    }

    private static Object readJson(String fileName) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(fileName), Object.class);
    }
}
