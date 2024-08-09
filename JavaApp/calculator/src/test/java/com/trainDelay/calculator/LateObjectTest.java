package com.trainDelay.calculator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LateObjectTest {

    @Test
    public void testCalculateDelay() {
        int delay = LateObject.calculateDelay("1230", "1200");
        assertEquals(30, delay);

        delay = LateObject.calculateDelay("1200", "1230");
        assertEquals(0, delay);

        delay = LateObject.calculateDelay("1200", "1200");
        assertEquals(0, delay);
    }

    @Test
    public void testToString() {
        LateObject lateObject = new LateObject("Station A", "1200", "1230", "1210", "1240", "Technical issue", "2023-10-01", 30, true);
        String expected = "The train on - 2023-10-01 has the following attributes \n late reason - Technical issue scheduled arrival 1230 and the actual arrival = 1240";
        assertEquals(expected, lateObject.toString());
    }

    @Test
    public void testGettersAndSetters() {
        LateObject lateObject = new LateObject();
        lateObject.setLocation("Station A");
        lateObject.setGbttPtd("1200");
        lateObject.setGbttPta("1230");
        lateObject.setActualTd("1210");
        lateObject.setActualTa("1240");
        lateObject.setLateCancReason("Technical issue");
        lateObject.setDateOfService("2023-10-01");
        lateObject.setDelayTime(30);
        lateObject.setDepartureStation(true);

        assertEquals("Station A", lateObject.getLocation());
        assertEquals("1200", lateObject.getGbttPtd());
        assertEquals("1230", lateObject.getGbttPta());
        assertEquals("1210", lateObject.getActualTd());
        assertEquals("1240", lateObject.getActualTa());
        assertEquals("Technical issue", lateObject.getLateCancReason());
        assertEquals("2023-10-01", lateObject.getDateOfService());
        assertEquals(30, lateObject.getDelayTime());
        assertTrue(lateObject.isDepartureStation());
    }
}