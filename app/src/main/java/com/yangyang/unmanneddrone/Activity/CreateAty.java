package com.yangyang.unmanneddrone.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;
import android.widget.ZoomControls;

import androidx.annotation.BoolRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.yangyang.unmanneddrone.R;
import com.yangyang.unmanneddrone.base.MyActivity;
import com.yangyang.unmanneddrone.helper.AnimationHelper;

import java.util.ArrayList;
import java.util.List;

public class CreateAty extends MyActivity implements
        View.OnClickListener, View.OnTouchListener {

    private MapView mMapView;     // 定义百度地图组件
    private BaiduMap mBaiduMap;   // 定义百度地图对象
    private LocationClient mLocationClient;  //定义LocationClient
    private boolean isFirstLoc = true;  //定义第一次启动
    private MyLocationConfiguration.LocationMode mCurrentMode;  //定义当前定位模式
    private LatLng latLng;
    private ImageView arrowMapView;
    //轮盘按钮
    private View roundView_one;
    private View roundView_two;
    private LinearLayout drawerRootView;
    private ViewFlipper viewFlipper;
    private BitmapDescriptor bitmap;
    private String address = "";

    //当前UI界面在父控件的起点X坐标
    private int maxX;
    //当前UI界面在父控件的终点X坐标
    private int minX;
    //动画
    Animation leftInAnimation;
    Animation leftOutAnimation;
    Animation rightInAnimation;
    Animation rightOutAnimation;

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.aty_create);
        initView();
        setInit();
//        setMapMarker();


    }


    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        roundView_one = findViewById(R.id.roundView_one);
        roundView_two = findViewById(R.id.roundView_two);
        //获取地图组件
        mMapView = findViewById(R.id.mv_map);
        //定义定位到当前位置按钮
        ImageButton mLocation = findViewById(R.id.ib_location);
        mLocation.setOnClickListener(this);

        ///others
        arrowMapView = findViewById(R.id.iv_arrow_map);
        //
        // arrowMapView.setRotation();

        drawerRootView = findViewById(R.id.ll_drawer_root);
        viewFlipper = findViewById(R.id.viewFlipper);
        drawerRootView.setOnTouchListener(this);
        //动画效果
        leftInAnimation = AnimationUtils.loadAnimation(this, R.anim.left_in);
        leftOutAnimation = AnimationUtils.loadAnimation(this, R.anim.left_out);
        rightInAnimation = AnimationUtils.loadAnimation(this, R.anim.right_in);
        rightOutAnimation = AnimationUtils.loadAnimation(this, R.anim.right_out);

        minX = drawerRootView.getMinimumWidth();

    }

    private void setInit() {
        // other init
        // map init
        mBaiduMap = mMapView.getMap();  //获取百度地图对象
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(this);  //创建LocationClient类
        mLocationClient.registerLocationListener(new MyLocationListener());   //注册监听函数
        initLocation();  //调用initLocation()方法，实现初始化定位
    }

    private void initLocation() {  //该方法实现初始化定位
        //创建LocationClientOption对象，用于设置定位方式
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");  //设置坐标类型
        option.setScanSpan(1000);      //1秒定位一次
        option.setOpenGps(true);      //打开GPS
        mLocationClient.setLocOption(option);  //保存定位参数与信息
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;  //设置定位模式

        // 隐藏logo
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
        // 开启定位
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_location:
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(latLng).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                break;
//            case R.id.iv_arrow_map:
//                int width = View.MeasureSpec.makeMeasureSpec(0,
//                        View.MeasureSpec.UNSPECIFIED);
//                int height = View.MeasureSpec.makeMeasureSpec(0,
//                        View.MeasureSpec.UNSPECIFIED);
//                drawerRootView.measure(width, height);
//                drawerRootView.getMeasuredWidth(); // 获取宽度
//                drawerRootView.getMeasuredHeight(); // 获取高度
//                switch (width) {
//                    case 180:
//                        Log.e(TAG, "=======drawerRootView.getMeasuredWidth()----------" + drawerRootView.getMeasuredWidth());
//                        ViewGroup.LayoutParams lp2;
//                        lp2 = drawerRootView.getLayoutParams();
//                        lp2.width = 800;
//                        lp2.height = LinearLayout.LayoutParams.MATCH_PARENT;
//                        drawerRootView.setLayoutParams(lp2);
//                        arrowMapView.setRotation(180);
//                        viewFlipper.setInAnimation(this, R.anim.left_in);
//                        viewFlipper.showNext();
//                        break;
//                    case 800:
//                        Log.e(TAG, "=======drawerRootView.getMeasuredWidth()----------" + drawerRootView.getMeasuredWidth());
//                        ViewGroup.LayoutParams lp;
//                        lp = drawerRootView.getLayoutParams();
//                        lp.width = 180;
//                        lp.height = LinearLayout.LayoutParams.MATCH_PARENT;
//                        drawerRootView.setLayoutParams(lp);
//                        arrowMapView.setRotation(0);
//                        break;
//
//                }
            default:
                break;
        }
    }

    //手势滑动
    private float downX;    //按下时 的X坐标
    private float downY;    //按下时 的Y坐标


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        String action = "";
        //在触发时回去到起始坐标
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //将按下时的坐标存储
                downX = x;
                downY = y;
                Log.e(TAG, "=======按下时X：" + x);
                Log.e(TAG, "=======按下时Y：" + y);
                //
                //获取到距离差
                float dx_down = x - downX;
                float dy_down = y - downY;

                Log.d(TAG, "-   dx_down-->" + dx_down);
                Log.d(TAG, "-   dy_down-->" + dy_down);
                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "=======抬起时X：" + x);
                Log.e(TAG, "=======抬起时Y：" + y);

                //获取到距离差
                float dx = x - downX;
                float dy = y - downY;
                //防止是按下也判断
                if (Math.abs(dx) > 8 && Math.abs(dy) > 8) {
                    Log.d(TAG, "----->" + dx);
                    //通过距离差判断方向
                    int orientation = getOrientation(dx, dy);
                    // TODO: 得到滑动的方向
                    switch (orientation) {
                        case 'r':
                            action = "右";
                            ViewGroup.LayoutParams lp;
                            lp = drawerRootView.getLayoutParams();
                            lp.width = 180;
                            lp.height = LinearLayout.LayoutParams.MATCH_PARENT;
                            drawerRootView.setLayoutParams(lp);
                            arrowMapView.setRotation(0);
                            roundView_one.setVisibility(View.GONE);
                            roundView_two.setVisibility(View.GONE);
                            break;
                        case 'l':
                            action = "左";
                            ViewGroup.LayoutParams lp2;
                            lp2 = drawerRootView.getLayoutParams();
                            lp2.width = 800;
                            lp2.height = LinearLayout.LayoutParams.MATCH_PARENT;
                            drawerRootView.setLayoutParams(lp2);
                            arrowMapView.setRotation(180);
                            viewFlipper.setInAnimation(this, R.anim.left_in);
                            viewFlipper.showNext();
                            roundView_one.setVisibility(View.VISIBLE);
                            roundView_two.setVisibility(View.VISIBLE);
                            break;
                        case 't':
                            action = "上";
                            break;
                        case 'b':
                            action = "下";
                            break;
                    }
                    Log.d(TAG, "-----------> " + action);
                }
                break;
        }
        return super.onTouchEvent(event);
    }


    /**
     * 根据距离差判断 滑动方向 * @param dx X轴的距离差 * @param dy Y轴的距离差 * @return 滑动的方向
     */
    private int getOrientation(float dx, float dy) {
        Log.e("Tag", "========X轴距离差：" + dx);
        Log.e("Tag", "========Y轴距离差：" + dy);
        if (Math.abs(dx) > Math.abs(dy)) {
            //X轴移动
            return dx > 0 ? 'r' : 'l';
        } else {
            //Y轴移动
            return dy > 0 ? 'b' : 't';
        }
    }

    //设置定位监听器
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //销毁后不再处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            //
            if (location.getLatitude() == 4.9E-324 || location.getLongitude() == 4.9E-324) {
                return;
            }

            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(180)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();

            // 设置定位数据
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            // Log.d(TAG, "-----------" + location.getLatitude() + "-------" + location.getLongitude());
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {  //如果是第一次定位,就定位到以自己为中心
                isFirstLoc = false;
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(latLng).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

    }

//描绘点
//    private void setMapMarker() {
//        bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.first);
//        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
//
//            @Override
//            public void onMapPoiClick(MapPoi arg0) {
//                // TODO Auto-generated method stub
//
//            }
//
//            //此方法就是点击地图监听
//            @Override
//            public void onMapClick(LatLng latLng) {
//                //获取经纬度
//                double latitude = latLng.latitude;
//                double longitude = latLng.longitude;
//                System.out.println("latitude=" + latitude + ",longitude=" + longitude);
//                //先清除图层
//                mBaiduMap.clear();
//                // 定义Maker坐标点
//                LatLng point = new LatLng(latitude, longitude);
//                // 构建MarkerOption，用于在地图上添加Marker
//                MarkerOptions options = new MarkerOptions().position(point)
//                        .icon(bitmap);
//                // 在地图上添加Marker，并显示
//                mBaiduMap.addOverlay(options);
//                //实例化一个地理编码查询对象
//                GeoCoder geoCoder = GeoCoder.newInstance();
//                //设置反地理编码位置坐标
//                ReverseGeoCodeOption op = new ReverseGeoCodeOption();
//                op.location(latLng);
//                //发起反地理编码请求(经纬度->地址信息)
//                geoCoder.reverseGeoCode(op);
//                geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
//
//                    @Override
//                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
//                        //获取点击的坐标地址
//                        address = arg0.getAddress();
//                        System.out.println("address=" + address);
//                    }
//
//                    @Override
//                    public void onGetGeoCodeResult(GeoCodeResult arg0) {
//                    }
//                });
//            }
//        });
//    }


    @Override
    protected void onResume() {
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }
}