package com.trainDelay.calculator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LateTrainUtilsTest {

    @Test
    public void testToString_ValidObject() {
        LateObject lateObject = new LateObject("Station A", "1200", "1230", "1210", "1240", "Technical issue", "2023-10-01", 30, true);
        String expected = "The train on - 2023-10-01 has the following attributes \n late reason - Technical issue scheduled arrival 1230 and the actual arrival = 1240";
        assertEquals(expected, lateObject.toString());
    }

    @Test
    public void testToString_EmptyObject() {
        LateObject lateObject = new LateObject();
        String expected = "The train on - null has the following attributes \n late reason - null scheduled arrival null and the actual arrival = null";
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

    @Test
    public void testGettersAndSetters_NullValues() {
        LateObject lateObject = new LateObject();
        lateObject.setLocation(null);
        lateObject.setGbttPtd(null);
        lateObject.setGbttPta(null);
        lateObject.setActualTd(null);
        lateObject.setActualTa(null);
        lateObject.setLateCancReason(null);
        lateObject.setDateOfService(null);
        lateObject.setDelayTime(0);
        lateObject.setDepartureStation(false);

        assertNull(lateObject.getLocation());
        assertNull(lateObject.getGbttPtd());
        assertNull(lateObject.getGbttPta());
        assertNull(lateObject.getActualTd());
        assertNull(lateObject.getActualTa());
        assertNull(lateObject.getLateCancReason());
        assertNull(lateObject.getDateOfService());
        assertEquals(0, lateObject.getDelayTime());
        assertFalse(lateObject.isDepartureStation());
    }

    @Test
    public void testEquals_SameObject() {
        LateObject lateObject = new LateObject("Station A", "1200", "1230", "1210", "1240", "Technical issue", "2023-10-01", 30, true);
        assertTrue(lateObject.equals(lateObject));
    }

    @Test
    public void testEquals_DifferentObject() {
        LateObject lateObject1 = new LateObject("Station A", "1200", "1230", "1210", "1240", "Technical issue", "2023-10-01", 30, true);
        LateObject lateObject2 = new LateObject("Station B", "1300", "1330", "1310", "1340", "Weather issue", "2023-10-02", 40, false);
        assertFalse(lateObject1.equals(lateObject2));
    }

    @Test
    public void testHashCode_DifferentObject() {
        LateObject lateObject1 = new LateObject("Station A", "1200", "1230", "1210", "1240", "Technical issue", "2023-10-01", 30, true);
        LateObject lateObject2 = new LateObject("Station B", "1300", "1330", "1310", "1340", "Weather issue", "2023-10-02", 40, false);
        assertNotEquals(lateObject1.hashCode(), lateObject2.hashCode());
    }

    @Test
    public void testConstructor_AllArgs() {
        LateObject lateObject = new LateObject("Station A", "1200", "1230", "1210", "1240", "Technical issue", "2023-10-01", 30, true);
        assertNotNull(lateObject);
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

    @Test
    public void testConstructor_NoArgs() {
        LateObject lateObject = new LateObject();
        assertNotNull(lateObject);
    }

    @Test
    public void testSetLocation() {
        LateObject lateObject = new LateObject();
        lateObject.setLocation("Station A");
        assertEquals("Station A", lateObject.getLocation());
    }

    @Test
    public void testSetGbttPtd() {
        LateObject lateObject = new LateObject();
        lateObject.setGbttPtd("1200");
        assertEquals("1200", lateObject.getGbttPtd());
    }

    @Test
    public void testSetGbttPta() {
        LateObject lateObject = new LateObject();
        lateObject.setGbttPta("1230");
        assertEquals("1230", lateObject.getGbttPta());
    }

    @Test
    public void testSetActualTd() {
        LateObject lateObject = new LateObject();
        lateObject.setActualTd("1210");
        assertEquals("1210", lateObject.getActualTd());
    }

    @Test
    public void testSetActualTa() {
        LateObject lateObject = new LateObject();
        lateObject.setActualTa("1240");
        assertEquals("1240", lateObject.getActualTa());
    }

    @Test
    public void testSetLateCancReason() {
        LateObject lateObject = new LateObject();
        lateObject.setLateCancReason("Technical issue");
        assertEquals("Technical issue", lateObject.getLateCancReason());
    }

    @Test
    public void testSetDateOfService() {
        LateObject lateObject = new LateObject();
        lateObject.setDateOfService("2023-10-01");
        assertEquals("2023-10-01", lateObject.getDateOfService());
    }

    @Test
    public void testSetDelayTime() {
        LateObject lateObject = new LateObject();
        lateObject.setDelayTime(30);
        assertEquals(30, lateObject.getDelayTime());
    }

    @Test
    public void testSetDepartureStation() {
        LateObject lateObject = new LateObject();
        lateObject.setDepartureStation(true);
        assertTrue(lateObject.isDepartureStation());
    }
}