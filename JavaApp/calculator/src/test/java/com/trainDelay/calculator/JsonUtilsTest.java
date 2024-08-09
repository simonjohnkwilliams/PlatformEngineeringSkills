package com.trainDelay.calculator;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JsonUtilsTest {

    @TempDir
    Path tempDir;

    @Test
    public void testReadJsonFromFile() throws IOException {
        // Create a temporary JSON file
        Path tempFile = Files.createFile(tempDir.resolve("test.json"));
        String jsonContent = "{\"key\":\"value\"}";
        Files.write(tempFile, jsonContent.getBytes());

        // Read JSON from file
        Map<String, Object> result = JsonUtils.readJsonFromFile(tempFile.toString());

        // Assertions
        assertNotNull(result);
        assertEquals("value", result.get("key"));
    }

    @Test
    public void testReadJsonAsMap() throws JsonProcessingException {
        // JSON string
        String jsonString = "{\"key\":\"value\"}";

        // Read JSON as Map
        Map<String, Object> result = JsonUtils.readJsonAsMap(jsonString);

        // Assertions
        assertNotNull(result);
        assertEquals("value", result.get("key"));
    }

    @Test
    public void testConvertMapToJsonString() throws JsonProcessingException {
        // Map to convert
        Map<String, Object> map = new HashMap<>();
        map.put("key", "value");

        // Convert Map to JSON string
        String jsonString = JsonUtils.convertMapToJsonString(map);

        // Assertions
        assertNotNull(jsonString);
        assertTrue(jsonString.contains("\"key\":\"value\""));
    }

    @Test
    public void testGenerateAttributeDictionary() throws IOException {
        // Create temporary JSON files
        Path tempFile1 = Files.createFile(tempDir.resolve("test1.json"));
        Path tempFile2 = Files.createFile(tempDir.resolve("test2.json"));
        String jsonContent = "{\"key\":\"value\"}";
        Files.write(tempFile1, jsonContent.getBytes());
        Files.write(tempFile2, jsonContent.getBytes());

        // Generate attribute dictionary
        List<String> result = JsonUtils.generateAttributeDictionary(tempDir.toString());

        // Assertions
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(jsonContent));
    }

    @Test
    public void testGenerateJsonStringFromFile() throws IOException {
        // Create a temporary JSON file
        Path tempFile = Files.createFile(tempDir.resolve("test.json"));
        String jsonContent = "{\"key\":\"value\"}";
        Files.write(tempFile, jsonContent.getBytes());

        // Generate JSON string from file
        String result = JsonUtils.generateJsonStringFromFile(tempFile.toFile());

        // Assertions
        assertNotNull(result);
        assertEquals(jsonContent, result);
    }

    @Test
    public void testGeneratePidList() throws IOException {
        // Create temporary JSON files
        Path tempFile1 = Files.createFile(tempDir.resolve("test1.json"));
        Path tempFile2 = Files.createFile(tempDir.resolve("test2.json"));
        String jsonContent1 = "{\"Services\":[{\"serviceAttributesMetrics\":{\"rids\":[\"rid1\"]}}]}";
        String jsonContent2 = "{\"Services\":[{\"serviceAttributesMetrics\":{\"rids\":[\"rid2\"]}}]}";
        Files.write(tempFile1, jsonContent1.getBytes());
        Files.write(tempFile2, jsonContent2.getBytes());

        // Generate PID list
        List<String> result = JsonUtils.generatePidList(tempDir.toString());

        // Assertions
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("rid1"));
        assertTrue(result.contains("rid2"));
    }

    @Test
    public void testGeneratePidListFromJson() {
        // JSON string
        String jsonString = "{\"Services\":[{\"serviceAttributesMetrics\":{\"rids\":[\"rid1\"]}}]}";

        // Generate PID list from JSON string
        List<String> result = JsonUtils.generatePidListFromJson(jsonString);

        // Assertions
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains("rid1"));
    }

    @Test
    public void testRemoveDuplicates() {
        // List with duplicates
        List<String> listWithDuplicates = List.of("rid1", "rid1", "rid2");

        // Remove duplicates
        List<String> result = JsonUtils.removeDuplicates(listWithDuplicates);

        // Assertions
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("rid1"));
        assertTrue(result.contains("rid2"));
    }
}
