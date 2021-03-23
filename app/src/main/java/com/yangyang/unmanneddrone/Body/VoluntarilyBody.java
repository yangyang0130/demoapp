package com.yangyang.unmanneddrone.Body;

import android.app.ApplicationErrorReport;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

//航线库
public class VoluntarilyBody {

    private int map;
    private String title;
    private String update_time;
    private String location;
    private int flag = 0;               // 0== flag: button else --> data
    
    public VoluntarilyBody(){
        
    }

    public int getMap() {
        return map;
    }

    public void setMap(int map) {
        this.map = map;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
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
                ", update_time='" + update_time + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
