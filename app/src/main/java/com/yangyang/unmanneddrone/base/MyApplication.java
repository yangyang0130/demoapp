package com.yangyang.unmanneddrone.base;

import android.app.Application;
import android.os.StrictMode;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.yangyang.tools.db.SQLiteHelper;
import com.yangyang.unmanneddrone.body.LocationMsgBody;
import com.yangyang.unmanneddrone.body.SelectionBody;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
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
        //excel数据表
        // SQLiteHelper.with(this).dropTable(SelectionBody.class.getSimpleName());
        SQLiteHelper.with(this).createTable(SelectionBody.class);
//         SQLiteHelper.with(this).dropTable(LocationMsgBody.class.getSimpleName());
        //航线库数据表
        SQLiteHelper.with(this).createTable(LocationMsgBody.class);
    }
}
