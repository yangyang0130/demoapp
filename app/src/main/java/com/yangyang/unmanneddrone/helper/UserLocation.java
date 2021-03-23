package com.yangyang.unmanneddrone.helper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public final class UserLocation extends BMapLocationHelper.LocationCallBack {

    private Context context;
    private boolean isFirstLoc = true;
    private BaiduMap map;
    private int mCurrentDirection = 0;
    private BMapLocationHelper helper;
    private static final String TAG = "UserLocation";

    public UserLocation(@NonNull Context context, @NonNull BaiduMap map) {
        this.context = context;
        this.map = map;
        init();
    }

    private void init() {
        map.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //开启定位图层
        map.setMyLocationEnabled(true);
    }

    /**
     * 在地图上显示用户的当前位置
     */
    public void showUserLocationOnMap() {
        if (helper == null) {
            LocationClientOption option = LocationClientOptionBuilder
                    .builder()
                    .setCoorType()
                    .bulid();
            helper = BMapLocationHelper.create(this.context, option, this);
        }
        helper.locStart();
    }

    @Override
    public void onReceiveLocation(int statusCode, BDLocation bdLocation, String errMsg) {
        if (statusCode == BMapLocationHelper.LOCATION_FAIL) {
            Toast.makeText(context, errMsg, Toast.LENGTH_SHORT);
            Log.i(TAG, "onReceiveLocation: " + errMsg);
            return;
        }
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(bdLocation.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(mCurrentDirection).latitude(bdLocation.getLatitude())
                .longitude(bdLocation.getLongitude()).build();
        map.setMyLocationData(locData);
        if (isFirstLoc) {
            isFirstLoc = false;
            LatLng centerPoint = new LatLng(bdLocation.getLatitude(),
                    bdLocation.getLongitude());
            MapStatus mapStatus = new MapStatus.Builder()
                    .target(centerPoint) //设置中心点
                    .zoom(18f)//设置缩放级别
                    .build();
            map.animateMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus));
        }
        if (helper != null) helper.locStop();
    }

    @Override
    public void onLocDiagnosticMessage(int locType, int diagnosticType, String diagnosticMessage) {
        super.onLocDiagnosticMessage(locType, diagnosticType, diagnosticMessage);
    }

    public BMapLocationHelper getHelper() {
        return helper;
    }

    public BaiduMap getMap() {
        return map;
    }

    public int getmCurrentDirection() {
        return mCurrentDirection;
    }
}
