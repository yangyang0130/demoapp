package com.yangyang.unmanneddrone.body;

import java.io.Serializable;

//航线库
public class VoluntarilyBody implements Serializable {

    private String id;
    private String map;
    private String title;
    private String updateTime;
    private String location;
    private int flag = 0;               // 0== flag: button else --> data

    public VoluntarilyBody() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "VoluntarilyBody{" +
                "map='" + map + '\'' +
                ", title='" + title + '\'' +
                ", update_time='" + updateTime + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
