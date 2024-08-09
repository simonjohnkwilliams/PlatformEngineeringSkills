package com.trainDelay.calculator;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ServiceMetrics {

    protected static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    protected static final String SERVICE_MESSAGE_DIR = Paths.get(System.getProperty("user.dir"), "downloaded", "inbound", "service").toString();
    protected static final String ATTRIBUTE_MESSAGE_DIR = Paths.get(System.getProperty("user.dir"), "downloaded", "outbound", "attribute").toString();
    protected static final String SERVICE_METRICS = Paths.get(SERVICE_MESSAGE_DIR, "serviceMetrics").toString();
    protected static final String SERVICE_ATTRIBUTE = Paths.get(ATTRIBUTE_MESSAGE_DIR, "serviceAttributes").toString();
    protected static final String FROM_STATION = "fromStation";
    protected static final String TO_STATION = "toStation";
    protected static final String FROM_TIME = "fromTime";
    protected static final String TO_TIME = "toTime";
    protected static final String TO_DATE = "toDate";
    protected static final String DAYS_DIFFERENCE = "daysDifference";

    public static List<String> getServiceMetricsDetailsForJourney(Map<String, Object> params) {
        validateParams(params);
        String fromStation = (String) params.get(FROM_STATION);
        String toStation = (String) params.get(TO_STATION);
        String fromTime = (String) params.get(FROM_TIME);
        String toTime = (String) params.get(TO_TIME);
        LocalDate toDate = LocalDate.parse((String) params.get(TO_DATE), DATE_FORMAT);
        int daysDifference = (int) params.get(DAYS_DIFFERENCE);

        RestTemplate restTemplate = new RestTemplate();
        //write a file for each of the days
        List<String> pidList = new ArrayList<>();
        for (int counter = 0; counter < daysDifference; counter++) {
            LocalDate date = toDate.minusDays(counter);
            String formattedDate = date.format(DATE_FORMAT);
            String fname = SERVICE_METRICS + formattedDate + ".json";
            if (!new File(fname).exists()  ||  (new File(fname).exists() && new File(fname).length()==0)) {
                try {
                    Map<String, String> requestBody = createRequestBody(fromStation, toStation, fromTime, toTime, formattedDate);
                    HttpEntity<Map<String, String>> entity = createHttpEntity(requestBody);
                    String url = "https://hsp-prod.rockshore.net/api/v1/serviceMetrics";
                    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
                    if (response.getStatusCode().is2xxSuccessful()) {
                        writeFile(fname, response.getBody());
                        pidList.addAll(JsonUtils.generatePidListFromJson(response.getBody()));

                    } else {
                        System.out.println("Request failed with status code: " + response.getStatusCode());
                    }

                } catch (HttpClientErrorException httpErr) {
                    System.out.println("HTTP error occurred: " + httpErr.getMessage());
                } catch (Exception err) {
                    System.out.println("Other error occurred: " + err.getMessage());
                }
            }
            else{
                pidList.addAll(JsonUtils.generatePidList(SERVICE_MESSAGE_DIR));
            }

        }
        return pidList;
    }

    static HttpEntity<Map<String, String>> createHttpEntity(Map<String, String> requestBody) {
        return new HttpEntity<>(requestBody, createHttpHeaders());
    }

    static HttpHeaders createHttpHeaders() {
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
    static void validateParams(Map<String, Object> params) {
        if (!params.containsKey("fromStation") || !params.containsKey("toStation") || !params.containsKey("fromTime") || !params.containsKey("toTime") || !params.containsKey("toDate")) {
            throw new IllegalArgumentException("Missing required parameters");
        }
        if(!params.containsKey("daysDifference")){
           params.put("daysDifference", 1);
        }
        if(!params.containsKey("fileName")){
            params.put("fileName", "serviceMetricsData"+ LocalDate.now().format(DATE_FORMAT) + "_" + UUID.randomUUID());
        }
    }
    static Map<String, String> createRequestBody(String fromStation, String toStation, String fromTime, String toTime, String formattedDate) {
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
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(data);
            System.out.println("File written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> writeAttributeMessageTestData(List<String> pidList) {
        List <String> listOfAllTrainTimes = new ArrayList<>();
        for (String pid : pidList) {
            String fname = SERVICE_ATTRIBUTE + pid + ".json";
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
                        listOfAllTrainTimes.add(response.getBody());
                    } else {
                        System.out.println("Request failed with status code: " + response.getStatusCode());
                    }
                } catch (HttpClientErrorException httpErr) {
                    System.out.println("HTTP error occurred: " + httpErr.getMessage());
                } catch (Exception err) {
                    System.out.println("Other error occurred: " + err.getMessage());
                }
            }
            else{
                System.out.println("File already exists");
                Map<String, Object> JsonMap = JsonUtils.readJsonFromFile(fname);
                listOfAllTrainTimes.add(JsonUtils.convertMapToJsonString(JsonMap));
            }
        }
        return listOfAllTrainTimes;
    }
}