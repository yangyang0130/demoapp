package com.yangyang.unmanneddrone.base;

import android.app.Application;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.yangyang.tools.db.SQLiteHelper;
import com.yangyang.unmanneddrone.Body.LocationMsgBody;
import com.yangyang.unmanneddrone.Body.SelectionBody;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initSDK();
        initDB();
    }

    /**
     * 初始化三方SDK
     */
    private void initSDK() {
        //初始化地图SDK
        SDKInitializer.initialize(this);
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }

    /**
     * 初始化数据库
     */
    private void initDB() {
        //
        SQLiteHelper.with(this).createTable(SelectionBody.class);
        SQLiteHelper.with(this).createTable(LocationMsgBody.class);
    }
}
