package com.yangyang.unmanneddrone.Body;

//数据库
public class LocationMsgBody {

    private String id;

    private String voluntarilyData;

    /**
     * 航线名称
     */
    private String routeName;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 悬停时间
     */
    private String hoverTime;
    /**
     * 悬停高度
     */
    private String hoverHeight;
    /**
     * 悬停朝向
     */
    private String hover_direction;
    /**
     * 位置信息
     */
    private String location;
    /**
     * 地图缩略图
     */
    private String thumbnail_path;
    /**
     * 起点坐标值
     */

    private String startLocation;
    /**
     * 起点经度
     */
    private String startLat;
    /**
     * 起点纬度
     */
    private String startLng;
    /**
     * 终点坐标值
     */
    private String endLocation;
    /**
     * 终点经度
     */
    private String endLat;
    /**
     * 终点纬度
     */
    private String endLng;

    public String getVoluntarilyData() {
        return voluntarilyData;
    }

    public void setVoluntarilyData(String voluntarilyData) {
        this.voluntarilyData = voluntarilyData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
                "id='" + id + '\'' +
                ", voluntarilyData='" + voluntarilyData + '\'' +
                ", routeName='" + routeName + '\'' +
                ", createTime='" + createTime + '\'' +
                ", hoverTime='" + hoverTime + '\'' +
                ", hoverHeight='" + hoverHeight + '\'' +
                ", hover_direction='" + hover_direction + '\'' +
                ", location='" + location + '\'' +
                //", thumbnail_path='" + thumbnail_path + '\'' +
                ", startLocation='" + startLocation + '\'' +
                ", startLat='" + startLat + '\'' +
                ", startLng='" + startLng + '\'' +
                ", endLocation='" + endLocation + '\'' +
                ", endLat='" + endLat + '\'' +
                ", endLng='" + endLng + '\'' +
                '}';
    }
}
