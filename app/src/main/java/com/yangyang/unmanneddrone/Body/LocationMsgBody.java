package com.yangyang.unmanneddrone.Body;

public class LocationMsgBody {

    private String routeName;
    private String createTime;
    private String hoverTime;
    private String hoverHeight;
    private String hover_direction;
    private String location;
    private String thumbnail_path;

    private String startLocation;
    private String startLat;
    private String startLng;
    private String endLocation;
    private String endLat;
    private String endLng;


    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getHoverTime() {
        return hoverTime;
    }

    public void setHoverTime(String hoverTime) {
        this.hoverTime = hoverTime;
    }

    public String getHoverHeight() {
        return hoverHeight;
    }

    public void setHoverHeight(String hoverHeight) {
        this.hoverHeight = hoverHeight;
    }

    public String getHover_direction() {
        return hover_direction;
    }

    public void setHover_direction(String hover_direction) {
        this.hover_direction = hover_direction;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getThumbnail_path() {
        return thumbnail_path;
    }

    public void setThumbnail_path(String thumbnail_path) {
        this.thumbnail_path = thumbnail_path;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getStartLat() {
        return startLat;
    }

    public void setStartLat(String startLat) {
        this.startLat = startLat;
    }

    public String getStartLng() {
        return startLng;
    }

    public void setStartLng(String startLng) {
        this.startLng = startLng;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public String getEndLat() {
        return endLat;
    }

    public void setEndLat(String endLat) {
        this.endLat = endLat;
    }

    public String getEndLng() {
        return endLng;
    }

    public void setEndLng(String endLng) {
        this.endLng = endLng;
    }

    @Override
    public String toString() {
        return "LocationMsgBody{" +
                "routeName='" + routeName + '\'' +
                ", createTime='" + createTime + '\'' +
                ", hoverTime='" + hoverTime + '\'' +
                ", hoverHeight='" + hoverHeight + '\'' +
                ", hover_direction='" + hover_direction + '\'' +
                ", location='" + location + '\'' +
                ", Thumbnail_path='" + thumbnail_path + '\'' +
                ", startLocation='" + startLocation + '\'' +
                ", startLat='" + startLat + '\'' +
                ", startLng='" + startLng + '\'' +
                ", endLocation='" + endLocation + '\'' +
                ", endLat='" + endLat + '\'' +
                ", endLng='" + endLng + '\'' +
                '}';
    }
}
