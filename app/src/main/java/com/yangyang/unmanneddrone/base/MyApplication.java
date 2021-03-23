package com.yangyang.unmanneddrone.base;

import android.app.Application;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化地图SDK
        SDKInitializer.initialize(this);
        SDKInitializer.setCoordType(CoordType.BD09LL);

        // /home/yangyang/disk_1T/UnmannedDrone/app/libs/BaiduLBS_android.jar
    }
}
