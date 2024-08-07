package com.trainDelay.calculator;

import java.time.Duration;
import java.time.LocalTime;

public class LateObject {

    private String location;
    private String gbttPtd;
    private String gbttPta;
    private String actualTd;
    private String actualTa;
    private String lateCancReason;
    private String dateOfService;
    private int delayTime;
    private boolean departureStation;

    public LateObject() {
    }

    public LateObject(String location, String gbttPtd, String gbttPta, String actualTd, String actualTa, String lateCancReason, String dateOfService, int delayTime, boolean departureStation) {
        this.location = location;
        this.gbttPtd = gbttPtd;
        this.gbttPta = gbttPta;
        this.actualTd = actualTd;
        this.actualTa = actualTa;
        this.lateCancReason = lateCancReason;
        this.dateOfService = dateOfService;
        this.delayTime = delayTime;
        this.departureStation = departureStation;
    }

    @Override
    public String toString() {
        return "The train on - " + dateOfService + " has the following attributes \n late reason - " + lateCancReason + " scheduled " +
                "arrival " + gbttPta + " and the actual arrival = " + actualTa;
    }

    public static int calculateDelay(String actualTimeArrival, String scheduledTimeArrival) {
        LocalTime actualIsoTime = LocalTime.of(Integer.parseInt(actualTimeArrival.substring(0, 2)), Integer.parseInt(actualTimeArrival.substring(2, 4)));
        LocalTime scheduledIsoTime = LocalTime.of(Integer.parseInt(scheduledTimeArrival.substring(0, 2)), Integer.parseInt(scheduledTimeArrival.substring(2, 4)));
        Duration delay = Duration.between(scheduledIsoTime, actualIsoTime);
        long delayMinutes = delay.toMinutes();
        return delayMinutes < 0 ? 0 : (int) delayMinutes;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGbttPtd() {
        return gbttPtd;
    }

    public void setGbttPtd(String gbttPtd) {
        this.gbttPtd = gbttPtd;
    }

    public String getGbttPta() {
        return gbttPta;
    }

    public void setGbttPta(String gbttPta) {
        this.gbttPta = gbttPta;
    }

    public String getActualTd() {
        return actualTd;
    }

    public void setActualTd(String actualTd) {
        this.actualTd = actualTd;
    }

    public String getActualTa() {
        return actualTa;
    }

    public void setActualTa(String actualTa) {
        this.actualTa = actualTa;
    }

    public String getLateCancReason() {
        return lateCancReason;
    }

    public void setLateCancReason(String lateCancReason) {
        this.lateCancReason = lateCancReason;
    }

    public String getDateOfService() {
        return dateOfService;
    }

    public void setDateOfService(String dateOfService) {
        this.dateOfService = dateOfService;
    }

    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }

    public boolean isDepartureStation() {
        return departureStation;
    }

    public void setDepartureStation(boolean departureStation) {
        this.departureStation = departureStation;
    }
}
