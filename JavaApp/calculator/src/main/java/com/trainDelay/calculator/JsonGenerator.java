package com.trainDelay.calculator;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class JsonGenerator {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final int DAYS_BACK = 9;
    private static final String MESSAGE_DIR = Paths.get(System.getProperty("user.dir"), "../test/files").toString();
    private static final String OUTBOUND_ATTRIBUTE_MESSAGE_DIR = Paths.get(System.getProperty("user.dir"), "downloaded/outbound/sao").toString();
    private static final String INBOUND_ATTRIBUTE_MESSAGE_DIR = Paths.get(System.getProperty("user.dir"), "downloaded/inbound/sao").toString();
    private static final String OUTBOUND_SERVICE_MESSAGE_DIR = Paths.get(System.getProperty("user.dir"), "downloaded/outbound/saopid").toString();
    private static final String INBOUND_SERVICE_MESSAGE_DIR = Paths.get(System.getProperty("user.dir"), "downloaded/inbound/saopid").toString();
    private static final String OUTBOUND_ATTRIBUTE_MESSAGE = Paths.get(OUTBOUND_ATTRIBUTE_MESSAGE_DIR, "serviceAttributesOutbound").toString();
    private static final String INBOUND_ATTRIBUTE_MESSAGE = Paths.get(INBOUND_ATTRIBUTE_MESSAGE_DIR, "serviceAttributesInbound").toString();
    private static final String OUTBOUND_SERVICE_MESSAGE = Paths.get(OUTBOUND_SERVICE_MESSAGE_DIR, "serviceAttributesOutboundTestdata").toString();
    private static final String INBOUND_SERVICE_MESSAGE = Paths.get(INBOUND_SERVICE_MESSAGE_DIR, "serviceAttributesInboundTestdata").toString();
    private static final String LOCATION = "location";
    private static final String LOCATIONS = "locations";
    private static final String GBTT_PTD = "gbtt_ptd";
    private static final String GBTT_PTA = "gbtt_pta";
    private static final String ACTUAL_TD = "actual_td";
    private static final String ACTUAL_TA = "actual_ta";
    private static final String LATE_CANC_REASON = "late_canc_reason";
    private static final String DATE_OF_SERVICE = "date_of_service";
    private static final String SERVICE_ATTRIBUTES_DETAILS = "serviceAttributesDetails";
    private static final String DEPARTURE_STATION = "departureStation";
    private static final String INBOUND_JOURNEY = "INBOUND_JOURNEY";
    private static final String OUTBOUND_JOURNEY = "OUTBOUND_JOURNEY";
    private static final String START_DATE_STRING = "START_DATE";
    private static final String DAYS_BACK_STRING = "DAYS_BACK";
    private static final String FILE_LOCATION_STRING = "FILE_LOCATION_STRING";
    private static final String DIR_LOCATION_STRING = "DIR_LOCATION_STRING";
    private static final String SERVICE_FILE_LOCATION_STRING = "SERVICE_FILE_LOCATION_STRING";
    private static final String SERVICE_DIR_LOCATION_STRING = "SERVICE_DIR_LOCATION_STRING";
    private static final String START_LOCATION_STRING = "START_LOCATION";
    private static final String TO_LOCATION_STRING = "TO_LOCATION";
    private static final String START_TIME = "START_TIME";
    private static final String END_TIME = "END_TIME";
    private static final String RESULT_FILE_STRING = "LATE_TRAIN_STRING";
    private static final String RESULT_FILE_OUTBOUND = "outboundLateTrains";
    private static final String RESULT_FILE_INBOUND = "inboundLateTrains";
    private static final String CLEAR_OLD_DATA_STRING = "CLEAR_OLD_DATA_STRING";

    public static Map<String, Object> getJson() {

            Map<String, Object> outboundJourney = new HashMap<>();
            outboundJourney.put(DEPARTURE_STATION, "GOD");
            outboundJourney.put(TO_LOCATION_STRING, "WAT");
            outboundJourney.put(START_TIME, "0400");
            outboundJourney.put(END_TIME, "1300");
            outboundJourney.put(START_DATE_STRING, LocalDate.now().minus(2, ChronoUnit.DAYS));
            outboundJourney.put(DAYS_BACK_STRING, DAYS_BACK);
            outboundJourney.put(FILE_LOCATION_STRING, OUTBOUND_ATTRIBUTE_MESSAGE);
            outboundJourney.put(DIR_LOCATION_STRING, OUTBOUND_ATTRIBUTE_MESSAGE_DIR);
            outboundJourney.put(SERVICE_FILE_LOCATION_STRING, OUTBOUND_SERVICE_MESSAGE);
            outboundJourney.put(RESULT_FILE_STRING, RESULT_FILE_OUTBOUND);
            outboundJourney.put(CLEAR_OLD_DATA_STRING, true);

            Map<String, Object> inboundJourney = new HashMap<>();
            inboundJourney.put(DEPARTURE_STATION, "WAT");
            inboundJourney.put(TO_LOCATION_STRING, "GOD");
            inboundJourney.put(START_TIME, "1200");
            inboundJourney.put(END_TIME, "2359");
            inboundJourney.put(START_DATE_STRING, LocalDate.now().minus(2, ChronoUnit.DAYS));
            inboundJourney.put(DAYS_BACK_STRING, DAYS_BACK);
            inboundJourney.put(FILE_LOCATION_STRING, INBOUND_ATTRIBUTE_MESSAGE);
            inboundJourney.put(DIR_LOCATION_STRING, INBOUND_ATTRIBUTE_MESSAGE_DIR);
            inboundJourney.put(SERVICE_FILE_LOCATION_STRING, INBOUND_SERVICE_MESSAGE);
            inboundJourney.put(RESULT_FILE_STRING, RESULT_FILE_INBOUND);
            inboundJourney.put(CLEAR_OLD_DATA_STRING, true);

            Map<String, Object> result = new HashMap<>();
            result.put(OUTBOUND_JOURNEY, outboundJourney);
            result.put(INBOUND_JOURNEY, inboundJourney);
            return result;
        /**
        if (argsJson.get(START_DATE_STRING) == null) {
            argsJson.put(START_DATE_STRING, LocalDate.now());
        }
        if (argsJson.get(DAYS_BACK_STRING) == null) {
            argsJson.put(DAYS_BACK_STRING, DAYS_BACK);
        }
        return argsJson;*/
    }
}
