package com.yangyang.unmanneddrone.Body;

public class ExcelDemoBean {

    private String serialNum;
    private String distance;
    private String interval;
    private String speed;

    public ExcelDemoBean(String serialNum, String distance, String interval, String speed) {
        this.serialNum = serialNum;
        this.distance = distance;
        this.interval = interval;
        this.speed = speed;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }
}
