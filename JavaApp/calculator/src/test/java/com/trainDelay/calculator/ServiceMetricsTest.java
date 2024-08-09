package com.trainDelay.calculator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class ServiceMetricsTest {

    @TempDir
    Path tempDir;

    @Test
    public void testWriteFile_InvalidPath() {
        String invalidPath = "invalid:/path/test.csv";
        String content = "Date, Departure Time, Delay Time\n2023-10-01,1200,30\n";
        assertThrows(IOException.class, () -> {
            ServiceMetrics.writeFile(invalidPath, content);
        });
    }

    @Test
    public void testGetServiceMetricsDetailsForJourney_ValidParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("fromStation", "StationA");
        params.put("toStation", "StationB");
        params.put("fromTime", "1200");
        params.put("toTime", "1300");
        params.put("toDate", "2023-10-01");
        params.put("daysDifference", 1);

        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        ResponseEntity<String> responseEntity = Mockito.mock(ResponseEntity.class);
        Mockito.when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        Mockito.when(responseEntity.getBody()).thenReturn("{\"pidList\": [\"pid1\", \"pid2\"]}");
        Mockito.when(restTemplate.exchange(any(String.class), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        List<String> result = ServiceMetrics.getServiceMetricsDetailsForJourney(params);
        assertNotNull(result);
        assertTrue(result.contains("pid1"));
        assertTrue(result.contains("pid2"));
    }

    @Test
    public void testGetServiceMetricsDetailsForJourney_MissingParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("fromStation", "StationA");

        assertThrows(IllegalArgumentException.class, () -> {
            ServiceMetrics.getServiceMetricsDetailsForJourney(params);
        });
    }

    @Test
    public void testCreateHttpEntity() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("key", "value");

        HttpEntity<Map<String, String>> entity = ServiceMetrics.createHttpEntity(requestBody);
        assertNotNull(entity);
        assertEquals("value", entity.getBody().get("key"));
    }

    @Test
    public void testCreateHttpHeaders() {
        HttpHeaders headers = ServiceMetrics.createHttpHeaders();
        assertNotNull(headers);
        assertTrue(headers.containsKey("Authorization"));
        assertTrue(headers.containsKey("Content-Type"));
        assertTrue(headers.containsKey("Host"));
    }

    @Test
    public void testValidateParams_ValidParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("fromStation", "StationA");
        params.put("toStation", "StationB");
        params.put("fromTime", "1200");
        params.put("toTime", "1300");
        params.put("toDate", "2023-10-01");

        assertDoesNotThrow(() -> ServiceMetrics.validateParams(params));
    }

    @Test
    public void testValidateParams_MissingParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("fromStation", "StationA");

        assertThrows(IllegalArgumentException.class, () -> ServiceMetrics.validateParams(params));
    }

    @Test
    public void testCreateRequestBody() {
        Map<String, String> requestBody = ServiceMetrics.createRequestBody("StationA", "StationB", "1200", "1300", "2023-10-01");
        assertNotNull(requestBody);
        assertEquals("StationA", requestBody.get("from_loc"));
        assertEquals("StationB", requestBody.get("to_loc"));
        assertEquals("1200", requestBody.get("from_time"));
        assertEquals("1300", requestBody.get("to_time"));
        assertEquals("2023-10-01", requestBody.get("from_date"));
        assertEquals("2023-10-01", requestBody.get("to_date"));
    }

    @Test
    public void testWriteFile_ValidInput() throws IOException {
        Path tempFile = tempDir.resolve("test.json");
        String content = "{\"key\": \"value\"}";
        ServiceMetrics.writeFile(tempFile.toString(), content);

        String result = Files.readString(tempFile);
        assertEquals(content, result);
    }

    @Test
    public void testWriteFile_EmptyContent() throws IOException {
        Path tempFile = tempDir.resolve("test.json");
        String content = "";
        ServiceMetrics.writeFile(tempFile.toString(), content);

        String result = Files.readString(tempFile);
        assertEquals(content, result);
    }

    @Test
    public void testWriteFile_NullContent() {
        Path tempFile = tempDir.resolve("test.json");
        assertThrows(NullPointerException.class, () -> {
            ServiceMetrics.writeFile(tempFile.toString(), null);
        });
    }

    @Test
    public void testWriteAttributeMessageTestData_ValidInput() {
        List<String> pidList = List.of("pid1", "pid2");

        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        ResponseEntity<String> responseEntity = Mockito.mock(ResponseEntity.class);
        Mockito.when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        Mockito.when(responseEntity.getBody()).thenReturn("{\"key\": \"value\"}");
        Mockito.when(restTemplate.exchange(any(String.class), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        List<String> result = ServiceMetrics.writeAttributeMessageTestData(pidList);
        assertNotNull(result);
        assertTrue(result.contains("{\"key\": \"value\"}"));
    }

    @Test
    public void testWriteAttributeMessageTestData_EmptyList() {
        List<String> pidList = List.of();
        List<String> result = ServiceMetrics.writeAttributeMessageTestData(pidList);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
