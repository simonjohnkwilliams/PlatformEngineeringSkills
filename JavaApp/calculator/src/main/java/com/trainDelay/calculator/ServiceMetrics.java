package com.trainDelay.calculator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
        validateParams(params);
        String fromStation = (String) params.get("fromStation");
        String toStation = (String) params.get("toStation");
        String fromTime = (String) params.get("fromTime");
        String toTime = (String) params.get("toTime");
        LocalDate toDate = LocalDate.parse((String) params.get("toDate"));
        int daysDifference = (int) params.get("daysDifference");
        String fileName = (String) params.get("fileName");

        RestTemplate restTemplate = new RestTemplate();
        for (int counter = 0; counter < daysDifference; counter++) {
            LocalDate date = toDate.minusDays(counter);
            String formattedDate = date.format(DATE_FORMAT);
            String fname = fileName + formattedDate + ".json";
            if (!new File(fname).exists()  && new File(fname).length()!=0) {
                try {
                    Map<String, String> requestBody = createRequestBody(fromStation, toStation, fromTime, toTime, formattedDate);
                    //String response = restTemplate.postForObject(, requestBody, String.class);
                    HttpEntity<Map<String, String>> entity = createHttpEntity(requestBody);
                    ObjectMapper objectMapper = new ObjectMapper();
                    String url = "https://hsp-prod.rockshore.net/api/v1/serviceMetrics";
                    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
                    // Step 7: Handle the response
                    if (response.getStatusCode().is2xxSuccessful()) {
                        writeFile(fname, response.getBody());
                    } else {
                        System.out.println("Request failed with status code: " + response.getStatusCode());
                    }

                } catch (HttpClientErrorException httpErr) {
                    System.out.println("HTTP error occurred: " + httpErr.getMessage());
                } catch (Exception err) {
                    System.out.println("Other error occurred: " + err.getMessage());
                }
            }
        }
    }

    private static HttpEntity<Map<String, String>> createHttpEntity(Map<String, String> requestBody) {

        return new HttpEntity<>(requestBody, createHttpHeaders());
    }

    private static HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String email = "simonjohnkwilliams@gmail.com";
        String password = "942tjgVAhWv@rSX";
        String auth = email + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);
        headers.set("Content-Type", "application/json");
        headers.set("Host", "hsp-prod.rockshore.net");
        return headers;
    }
    private static void validateParams(Map<String, Object> params) {
        if (!params.containsKey("fromStation") || !params.containsKey("toStation") || !params.containsKey("fromTime") || !params.containsKey("toTime") || !params.containsKey("toDate")) {
            throw new IllegalArgumentException("Missing required parameters");
        }
        if(!params.containsKey("daysDifference")){
           params.put("daysDifference", 1);
        }
        if(!params.containsKey("fileName")){
            params.put("fileName", "serviceMetricsData"+ LocalDate.now().format(DATE_FORMAT) + "_" + UUID.randomUUID().toString());
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

    public static void writeAttributeMessageTestData(List<String> pidList, String fileName) {
        for (String pid : pidList) {
            String fname = fileName + pid + ".json";
            if (!new File(fname).exists()) {
                try {

                    String email = "simonjohnkwilliams@gmail.com";
                    String password = "942tjgVAhWv@rSX";
                    String auth = email + ":" + password;
                    String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

                    HttpHeaders headers = new HttpHeaders();
                    headers.set("Authorization", "Basic " + encodedAuth);
                    headers.set("Content-Type", "application/json");

                    Map<String, String> requestBody = new HashMap<>();
                    requestBody.put("rid", pid);

                    HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);
                    RestTemplate restTemplate = new RestTemplate();
                    String url = "https://hsp-prod.rockshore.net/api/v1/serviceDetails";
                    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

                    if (response.getStatusCode().is2xxSuccessful()) {
                        writeFile(fname, response.getBody());
                    } else {
                        System.out.println("Request failed with status code: " + response.getStatusCode());
                    }
                } catch (HttpClientErrorException httpErr) {
                    System.out.println("HTTP error occurred: " + httpErr.getMessage());
                } catch (Exception err) {
                    System.out.println("Other error occurred: " + err.getMessage());
                }
            }
        }
    }
}