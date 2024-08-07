package com.trainDelay.calculator;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ServiceMetrics {

    protected static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    protected static final String OUTBOUND_ATTRIBUTE_MESSAGE_DIR = Paths.get(System.getProperty("user.dir"), "downloaded", "outbound", "sao").toString();
    protected static final String INBOUND_ATTRIBUTE_MESSAGE_DIR = Paths.get(System.getProperty("user.dir"), "downloaded", "inbound", "sao").toString();
    protected static final String OUTBOUND_SERVICE_MESSAGE_DIR = Paths.get(System.getProperty("user.dir"), "downloaded", "outbound", "saopid").toString();
    protected static final String INBOUND_SERVICE_MESSAGE_DIR = Paths.get(System.getProperty("user.dir"), "downloaded", "inbound", "saopid").toString();
    protected static final String OUTBOUND_ATTRIBUTE_MESSAGE = Paths.get(OUTBOUND_ATTRIBUTE_MESSAGE_DIR, "serviceAttributesOutbound").toString();
    protected static final String INBOUND_ATTRIBUTE_MESSAGE = Paths.get(INBOUND_ATTRIBUTE_MESSAGE_DIR, "serviceAttributesInbound").toString();
    protected static final String OUTBOUND_SERVICE_MESSAGE = Paths.get(OUTBOUND_SERVICE_MESSAGE_DIR, "serviceAttributesOutboundTestdata").toString();
    protected static final String INBOUND_SERVICE_MESSAGE = Paths.get(INBOUND_SERVICE_MESSAGE_DIR, "serviceAttributesInboundTestdata").toString();

    public static void writeServiceMetricsTestData(Map<String, Object> params) {
        String fromStation = (String) params.get("fromStation");
        String toStation = (String) params.get("toStation");
        String fromTime = (String) params.get("fromTime");
        String toTime = (String) params.get("toTime");
        LocalDate toDate = (LocalDate) params.get("toDate");
        int daysDifference = (int) params.get("daysDifference");
        String fileName = (String) params.get("fileName");

        RestTemplate restTemplate = new RestTemplate();
        for (int counter = 0; counter < daysDifference; counter++) {
            LocalDate date = toDate.minusDays(counter);
            String formattedDate = date.format(DATE_FORMAT);
            String fname = fileName + formattedDate + ".json";
            if (!new File(fname).exists()) {
                try {
                    List<String> creds = getCredentials("/Users/simonwilliams/Documents/trainApp/trainConfig.txt");
                    Map<String, String> requestBody = createRequestBody(fromStation, toStation, fromTime, toTime, formattedDate);
                    String response = restTemplate.postForObject("https://hsp-prod.rockshore.net/api/v1/serviceMetrics", requestBody, String.class);
                    writeFile(fname, response);
                } catch (HttpClientErrorException httpErr) {
                    System.out.println("HTTP error occurred: " + httpErr.getMessage());
                } catch (Exception err) {
                    System.out.println("Other error occurred: " + err.getMessage());
                }
            }
        }
    }

    private static Map<String, String> createRequestBody(String fromStation, String toStation, String fromTime, String toTime, String formattedDate) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("from_loc", fromStation);
        requestBody.put("to_loc", toStation);
        requestBody.put("from_time", fromTime);
        requestBody.put("to_time", toTime);
        requestBody.put("from_date", formattedDate);
        requestBody.put("to_date", formattedDate);
        requestBody.put("days", "WEEKDAY");
        return requestBody;
    }

    public static void writeFile(String fileName, String data) throws IOException {
        try {
            CalculatorFileUtils.writeFile(fileName, data);
            System.out.println("File written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getCredentials(String credentialsFile) throws IOException {
        try {
            Properties properties = ConfigLoader.loadConfig(credentialsFile);
            String username = properties.getProperty("configuration.username");
            String password = properties.getProperty("configuration.password");
            return Arrays.asList(username, password);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return Arrays.asList("", "");
    }
}