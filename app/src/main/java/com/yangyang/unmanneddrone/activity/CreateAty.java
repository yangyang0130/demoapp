package com.yangyang.unmanneddrone.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.ZoomControls;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

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
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.yangyang.tools.db.SQLiteHelper;
import com.yangyang.tools.permission.OnPermission;
import com.yangyang.tools.permission.Permission;
import com.yangyang.tools.permission.XXPermissions;

import com.yangyang.unmanneddrone.R;

import com.yangyang.unmanneddrone.base.MyActivity;
import com.yangyang.unmanneddrone.body.LocationMsgBody;
import com.yangyang.unmanneddrone.body.SelectionBody;
import com.yangyang.unmanneddrone.helper.Constants;
import com.yangyang.unmanneddrone.helper.DoubleClickHelper;
import com.yangyang.unmanneddrone.helper.ExcelUtils;
import com.yangyang.unmanneddrone.helper.IdHelper;
import com.yangyang.unmanneddrone.helper.ImageHelper;
import com.yangyang.unmanneddrone.helper.ToastHelper;
import com.yangyang.unmanneddrone.view.RoundMenuView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.yangyang.unmanneddrone.R.string.cancel;
import static com.yangyang.unmanneddrone.R.string.ethover_height;
import static com.yangyang.unmanneddrone.R.string.no_open;
import static com.yangyang.unmanneddrone.R.string.time;
import static com.yangyang.unmanneddrone.R.string.yes;

/**
 * 创建航线界面
 */
public class CreateAty extends MyActivity
        implements View.OnClickListener, View.OnTouchListener,
        RoundMenuView.OnViewClickListener, BaiduMap.OnMapClickListener, BaiduMap.OnMarkerClickListener,
        BaiduMap.SnapshotReadyCallback, RadioGroup.OnCheckedChangeListener {
    /**
     * 选择的轮盘 默认选择起点
     * 1; 起点位置
     * 2： 终点位置
     */
    private int Wheel_FLAG = 0x001;
    // 设置微调 的间距，默认为0.000001
    private final double SPACE_SCROLL_SIZE = 0.000005;
    /**
     * 百度SDK
     */
    private BaiduMap mBaiduMap;   // 定义百度地图对象
    private LocationClient mLocationClient;  //定义LocationClient
    private boolean isFirstLoc = true;  //定义第一次启动
    private MyLocationConfiguration.LocationMode mCurrentMode;  //定义当前定位模式
    private LatLng mLatLng;
    private LocationManager mLocationManager;
    private static final int PRIVATE_CODE = 1315;//开启GPS权限
    private final String mStartStr = "起点";
    private final String mEndStr = "终点";
    /**
     * xml控件View定义
     */
    private MapView mMapView;     // 定义百度地图组件
    private ImageView mArrowMapView;
    //轮盘按钮
    private RoundMenuView mRoundView_one;
    private RoundMenuView mRoundView_two;
    private LinearLayout mDrawerRootView;
    private ViewFlipper mViewFlipper;
    private ImageButton mIbBack;
    private EditText mStartLatView;
    private EditText mStartLngView;
    private EditText mEndLatView;
    private EditText mEndLngView;
    private ImageButton mLocation;
    private LinearLayout mInputDataRootView;
    private LinearLayout mSaveDataRootView;
    private Button mNextButtonView;
    private ImageButton mIbSave, mIbStart;
    //拖动条
    private SeekBar mSbHoverTime, mSbHoverHeight;
    private EditText mEtHoverTime, mEtHoverHeight;
    private EditText mEtTitle;
    //手势滑动
    private float mDownX;    //按下时 的X坐标
    private float mDownY;    //按下时 的Y坐标
    private Button mImportDataButtonView;
    private RadioButton mLeftRadioButtonView;
    private ImageView mImageView;
    private RelativeLayout mMapParentView;
    private ImageView mIvExcel;
    private RadioButton mCbLeft, mCbRight;
    private RadioGroup mRgDirection;
    private EditText mInterval;
    /**
     * 设置marker
     */
    private int mLastLocationIcon;
    //
    private OverlayOptions mStartOption = null;
    private OverlayOptions mEndOption = null;
    private boolean mEndExitsFlag = false;


    //数据库
    private LocationMsgBody mLocationMsgBody;
    private String mCurrentAddress;
    private String mSgId;


    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.aty_create);
        initView();
        setInit();
        initListener();
        showContacts();
        showGPSContacts();
        initData();

        // setOnClickListener(R.mipmap.first, R.mipmap.end);
        mBaiduMap.setOnMarkerClickListener(this);
    }

    /**
     * 初始化信息数据
     */
    private void initData() {
        String transId = getIntent().getStringExtra("transId");
        if (!TextUtils.isEmpty(transId)) {
            List<LocationMsgBody> msgBodyList = SQLiteHelper.with(this).query(LocationMsgBody.class,
                    "select * from " + LocationMsgBody.class.getSimpleName() + " where id=" + transId);
            Log.d(TAG, "-----传递过来的数据------>" + msgBodyList);
            // 把数据填充到页面上
            if (msgBodyList.size() != 0) {
                LocationMsgBody msgBody = msgBodyList.get(0);
                mSgId = msgBody.getId();
                mEtTitle.setText(msgBody.getRouteName());
                mStartLatView.setText(msgBody.getStartLat());
                mStartLngView.setText(msgBody.getStartLng());
                mEndLngView.setText(msgBody.getEndLng());
                mEndLatView.setText(msgBody.getEndLat());
                mEtHoverTime.setText(msgBody.getHoverTime());
                mEtHoverHeight.setText(msgBody.getHoverHeight());
                mInterval.setText(msgBody.getNumInterval());
                //起点
                LatLng startLatLng = new LatLng(Double.parseDouble(mStartLatView.getText().toString()),
                        Double.parseDouble(mStartLngView.getText().toString()));
                // 终点
                LatLng endLatLng = new LatLng(Double.parseDouble(mEndLatView.getText().toString()),
                        Double.parseDouble(mEndLngView.getText().toString()));
                List<LatLng> lineList = new ArrayList<LatLng>();
                lineList.add(startLatLng);
                lineList.add(endLatLng);
                //设置折线的属性
                OverlayOptions mOverlayOptions = new PolylineOptions()
                        .width(10)
                        .color(0xAAFF0000)
                        .points(lineList);
                //在地图上绘制折线
                //mPloyline 折线对象
                Overlay mPolyline = mBaiduMap.addOverlay(mOverlayOptions);
                if ("right".equals(msgBody.getHoverDirection())) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCbRight.setChecked(true);
                        }
                    });
                }
                if ("left".equals(msgBody.getHoverDirection())) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCbLeft.setChecked(true);
                        }
                    });
                }
                // 设置导入可见
                mIvExcel.setVisibility(TextUtils.isEmpty(msgBody.getVoluntarilyData()) ? View.GONE : View.VISIBLE);
                String[] data = msgBody.getVoluntarilyData().split(",");


                List<SelectionBody> selectionBodyList = new ArrayList<>();
                for (int i = 0; i < data.length; i++) {
                    String datum = data[i];
                    List<SelectionBody> bodyList = SQLiteHelper.with(this).query(SelectionBody.class,
                            "select * from " + SelectionBody.class.getSimpleName() + " where id='" + datum + "'");
                    //
                    selectionBodyList.addAll(bodyList);
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        mRoundView_one = findViewById(R.id.roundView_one);
        mRoundView_two = findViewById(R.id.roundView_two);
        //获取地图组件
        mMapView = findViewById(R.id.mv_map);
        //定义定位到当前位置按钮
        mLocation = findViewById(R.id.ib_location);
        mIbBack = findViewById(R.id.ib_back);
        mImageView = findViewById(R.id.iv_image);
        mMapParentView = findViewById(R.id.rl_map_parent);
        ///others
        mArrowMapView = findViewById(R.id.iv_arrow_map);
        mStartLatView = findViewById(R.id.et_start_Latitude);
        mStartLngView = findViewById(R.id.et_start_Longitude);

        mEndLatView = findViewById(R.id.et_end_Latitude);
        mEndLngView = findViewById(R.id.et_end_Longitude);

        mDrawerRootView = findViewById(R.id.ll_drawer_root);
        mViewFlipper = findViewById(R.id.viewFlipper);
        mLeftRadioButtonView = findViewById(R.id.cb_left);
        //动画效果
        //动画
        Animation leftInAnimation = AnimationUtils.loadAnimation(this, R.anim.left_in);
        Animation leftOutAnimation = AnimationUtils.loadAnimation(this, R.anim.left_out);
        Animation rightInAnimation = AnimationUtils.loadAnimation(this, R.anim.right_in);
        Animation rightOutAnimation = AnimationUtils.loadAnimation(this, R.anim.right_out);

        mInputDataRootView = findViewById(R.id.ll_input_data_root);
        mSaveDataRootView = findViewById(R.id.ll_save_data_root);
        mNextButtonView = findViewById(R.id.bt_next);
        mIbSave = findViewById(R.id.ib_save);
        mIbStart = findViewById(R.id.ib_start);
        mEtTitle = findViewById(R.id.et_title);
        mIvExcel = findViewById(R.id.excel);
        mCbLeft = findViewById(R.id.cb_left);
        mCbRight = findViewById(R.id.cb_right);
        mRgDirection = findViewById(R.id.hover_direction);
        mInterval = findViewById(R.id.interval);


        // 数据导入page
        mImportDataButtonView = findViewById(R.id.button_Import_data);
        //拖动条
        mEtHoverTime = findViewById(R.id.et_hover_time);
        mEtHoverHeight = findViewById(R.id.et_hover_height);
        mSbHoverTime = findViewById(R.id.sb_Hover_time);
        mSbHoverHeight = findViewById(R.id.sb_Hover_height);
        mSbHoverTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mEtHoverTime.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSbHoverHeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mEtHoverHeight.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

    private void initListener() {
        mIbBack.setOnClickListener(this);
        mLocation.setOnClickListener(this);
        mDrawerRootView.setOnTouchListener(this);
        mNextButtonView.setOnClickListener(this);
        mRoundView_one.setmOnClickListener(this);
        mRoundView_two.setmOnClickListener(this);
        mImportDataButtonView.setOnClickListener(this);
        mIbSave.setOnClickListener(this);
        mLeftRadioButtonView.setOnClickListener(this);
        mRgDirection.setOnCheckedChangeListener(this);
        mArrowMapView.setOnClickListener(this);
    }

    ////权限判断和申请
    public void showContacts() {
        if (Build.VERSION.SDK_INT >= 23) { //判断是否为android6.0系统版本，如果是，需要动态添加权限
            if (ContextCompat.checkSelfPermission(CreateAty.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(CreateAty.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(CreateAty.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                }, 1);
            }
        }
    }

    ////权限判断和申请
    public void showWriteContacts() {

    }


    private void setInit() {
        // other init
        // map init
        mBaiduMap = mMapView.getMap();  //获取百度地图对象
        mBaiduMap.setMyLocationEnabled(true);

        mLocationClient = new LocationClient(this);  //创建LocationClient类
        mLocationClient.registerLocationListener(new MyLocationListener());   //注册监听函数
        initLocation();  //调用initLocation()方法，实现初始化定位

        initKeyBoardListener();

        mLocationMsgBody = new LocationMsgBody();
    }

    private void initLocation() {  //该方法实现初始化定位
        //创建LocationClientOption对象，用于设置定位方式
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");  //设置坐标类型
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setScanSpan(3000);      //1秒定位一次
        option.setOpenGps(true);      //打开GPS
        option.setAddrType("all");//返回定位结果包含地址信息
        option.setPriority(LocationClientOption.NetWorkFirst); // 设置网络优先
        option.setPriority(LocationClientOption.GpsFirst);       //gps
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

        /*
        初始化定位事件
         */
        mBaiduMap.setOnMapClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        //屏蔽短时间内双击
        if (DoubleClickHelper.isOnDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            //回到当前定位按钮
            case R.id.ib_location:
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(mLatLng).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                showGPSContacts();
                break;
            //下一步按钮
            case R.id.bt_next:
                if (TextUtils.isEmpty(mStartLatView.getText().toString())
                        || TextUtils.isEmpty(mStartLngView.getText().toString())
                        || TextUtils.isEmpty(mEndLatView.getText().toString())
                        || TextUtils.isEmpty(mEndLngView.getText().toString())) {
                    Toast.makeText(this, R.string.data_is_complete, Toast.LENGTH_SHORT).show();
                    return;
                }
                mBaiduMap.snapshot(this);
                mInputDataRootView.setVisibility(View.GONE);
                mSaveDataRootView.setVisibility(View.VISIBLE);
                mIbSave.setVisibility(View.VISIBLE);
                mIbStart.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_arrow_map:
                mSaveDataRootView.setVisibility(View.GONE);
                mInputDataRootView.setVisibility(View.VISIBLE);
                break;
            case R.id.ib_back:
                finish();
                break;
            //导入数据按钮
            case R.id.button_Import_data:
                if (Build.VERSION.SDK_INT >= 23) { //判断是否为android6.0系统版本，如果是，需要动态添加权限
                    if (ContextCompat.checkSelfPermission(CreateAty.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        // 没有权限，申请权限。
                        CreateAty.this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
                    } else {
                        try {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/vnd.ms-excel");
                            intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            startActivityForResult(intent, 1);
                        } catch (Exception e) {
                            Log.e(TAG, "-----------_>" + e.toString());
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("application/vnd.ms-excel");
                        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(intent, 3);
                    } catch (Exception e) {
                        Log.e(TAG, "-----------_>" + e.toString());
                        e.printStackTrace();
                    }
                }

                break;
            case R.id.ib_save:
                // 保存数据到数据库
                //起点终点经纬度存入数据库
                mLocationMsgBody.setStartLat(mStartLatView.getText().toString());
                mLocationMsgBody.setEndLat(mEndLatView.getText().toString());
                mLocationMsgBody.setEndLng(mEndLngView.getText().toString());
                mLocationMsgBody.setStartLng(mStartLngView.getText().toString());
                //悬停时间存入数据库
                mLocationMsgBody.setHoverTime(mEtHoverTime.getText().toString());
                //悬停高度存入数据库
                mLocationMsgBody.setHoverHeight(mEtHoverHeight.getText().toString());
                //航线名称存入数据库
                mLocationMsgBody.setRouteName(mEtTitle.getText().toString());
                //创建时间
                mLocationMsgBody.setCreateTime(getTime(System.currentTimeMillis()));
                //保存间隔设置
                mLocationMsgBody.setNumInterval(mInterval.getText().toString());
                // 保存当前地址
                mLocationMsgBody.setLocation(mCurrentAddress);

                if (TextUtils.isEmpty(mSgId)) {
                    mLocationMsgBody.setId(String.valueOf(System.currentTimeMillis()));
                    long insert = SQLiteHelper.with(this).insert(mLocationMsgBody);
                    if (insert > 0) {
                        // Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                        ToastHelper.getInstance().showToast(getContext(), getString(R.string.save), Toast.LENGTH_SHORT);
                    }
                } else {
                    mLocationMsgBody.setId(mSgId);
                    long update = SQLiteHelper.with(this).update(mLocationMsgBody, null, null);
                    if (update > 0) {
                        // Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
                        ToastHelper.getInstance().showToast(getContext(), getString(R.string.modified), Toast.LENGTH_SHORT);
                    }
                }
                break;
            default:
                break;
        }
    }

    //获取时间戳转换
    public static String getTime(long time) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdr = new SimpleDateFormat("MM/dd/yyyy");
        return sdr.format(new Date(time));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == 1) {
            Uri uri = data.getData();
            try {
                InputStream inStream = getContentResolver().openInputStream(uri);
                List<SelectionBody> selectionBodyList = new ArrayList<>();
                List<SelectionBody> excelDataList = ExcelUtils.readExcel(inStream, selectionBodyList);
                Log.d(TAG, "--->" + excelDataList.size() + "--->" + excelDataList);
                // 暂存入数据库
                // SQLiteHelper.with(CreateAty.this).insert(excelDataList);
                IdHelper idHelper = new IdHelper(1, 1, 1);
                StringBuilder idBuilder = new StringBuilder();
                for (SelectionBody body : excelDataList) {
                    body.setId(String.valueOf(idHelper.nextId()));
                    idBuilder.append(body.getId()).append(",");
                    SQLiteHelper.with(CreateAty.this).insert(body);
                }
                mLocationMsgBody.setVoluntarilyData(idBuilder.toString());
                mIvExcel.setVisibility(View.VISIBLE);
                // Toast.makeText(CreateAty.this, "已导入断面", Toast.LENGTH_SHORT).show();
                ToastHelper.getInstance().showToast(getContext(), getString(R.string.have_input), Toast.LENGTH_SHORT);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            List<SelectionBody> bodyList = SQLiteHelper.with(CreateAty.this).query(SelectionBody.class);
        }
    }

    //左右滑动
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
                mDownX = x;
                mDownY = y;
                Log.e(TAG, "=======hang downX：" + x);
                Log.e(TAG, "=======hang downY：" + y);
                //
                //获取到距离差
                float dx_down = x - mDownX;
                float dy_down = y - mDownY;

                Log.d(TAG, "-   dx_down-->" + dx_down);
                Log.d(TAG, "-   dy_down-->" + dy_down);
                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "=======hang onX：" + x);
                Log.e(TAG, "=======hang onY：" + y);

                //获取到距离差
                float dx = x - mDownX;
                float dy = y - mDownY;
                //防止是按下也判断
                if (Math.abs(dx) > 10 && Math.abs(dy) > 10) {
                    Log.d(TAG, "----->" + dx);
                    //通过距离差判断方向
                    int orientation = getOrientation(dx, dy);
                    switch (orientation) {
                        case 'r':
                            setPageSlideRightParams();
                            mSaveDataRootView.setVisibility(View.GONE);
                            mInputDataRootView.setVisibility(View.GONE);
                            break;
                        case 'l':
                            setPageSlideLeftParams();
                            mSaveDataRootView.setVisibility(View.VISIBLE);
                            mInputDataRootView.setVisibility(View.VISIBLE);
                            break;
                        case 't':
                            break;
                        case 'b':
                            break;
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 设置右滑属性
     */
    private void setPageSlideRightParams() {
        ViewGroup.LayoutParams lp;
        lp = mDrawerRootView.getLayoutParams();
        lp.width = 180;
        lp.height = LinearLayout.LayoutParams.MATCH_PARENT;
        mDrawerRootView.setLayoutParams(lp);
        mArrowMapView.setRotation(0);
        mRoundView_one.setVisibility(View.GONE);
        mRoundView_two.setVisibility(View.GONE);
    }

    /**
     * 设置左滑属性
     */
    private void setPageSlideLeftParams() {
        ViewGroup.LayoutParams lp2;
        lp2 = mDrawerRootView.getLayoutParams();
        lp2.width = 800;
        lp2.height = LinearLayout.LayoutParams.MATCH_PARENT;
        mDrawerRootView.setLayoutParams(lp2);
        mArrowMapView.setRotation(180);
        mViewFlipper.setInAnimation(this, R.anim.left_in);
        mViewFlipper.showNext();
        mRoundView_one.setVisibility(View.VISIBLE);
        mRoundView_two.setVisibility(View.VISIBLE);
    }

    /**
     * 根据距离差判断 滑动方向 * @param dx X轴的距离差 * @param dy Y轴的距离差 * @return 滑动的方向
     */
    private int getOrientation(float dx, float dy) {
        if (Math.abs(dx) > Math.abs(dy)) {
            //X轴移动
            return dx > 0 ? 'r' : 'l';
        } else {
            //Y轴移动
            return dy > 0 ? 'b' : 't';
        }
    }

    /**
     * 轮盘
     *
     * @param roundMenuView
     * @param arrow
     */
    @Override
    public void onViewClick(RoundMenuView roundMenuView, int arrow) {
        // 第一个轮盘
        if (roundMenuView == mRoundView_one) {
            Wheel_FLAG = 1;
            if (arrow == Constants.getInstance().Click_CENTER_ARROW) {
                return;
                //setMarker(currentLatSize, currentLngSize, R.mipmap.first);
                // 替换为起点的图标
                // setMarker(true, currentStartLatSize, currentStartLngSize, R.mipmap.first);
                // Wheel_FLAG = 2;
            } else if (arrow == Constants.getInstance().Click_RIGHT_ARROW) {
                if (TextUtils.isEmpty(mStartLatView.getText())) {
                    Toast.makeText(CreateAty.this, R.string.pls_input_start, Toast.LENGTH_SHORT).show();
                } else {
                    //edittext
                    currentStartLngSize += SPACE_SCROLL_SIZE;
                    mStartLngView.setText(String.valueOf(currentStartLngSize));
                    // 绘制地图
                    setMarker(false, currentStartLatSize, currentStartLngSize, R.mipmap.first);
                }
            } else if (arrow == Constants.getInstance().Click_UP_ARROW) {
                if (TextUtils.isEmpty(mStartLngView.getText())) {
                    Toast.makeText(CreateAty.this, R.string.pls_input_start, Toast.LENGTH_SHORT).show();
                } else {
                    currentStartLatSize += SPACE_SCROLL_SIZE;
                    mStartLatView.setText(String.valueOf(currentStartLatSize));
                    setMarker(false, currentStartLatSize, currentStartLngSize, R.mipmap.first);
                }
            } else if (arrow == Constants.getInstance().Click_LEFT_ARROW) {
                if (TextUtils.isEmpty(mStartLatView.getText())) {
                    Toast.makeText(CreateAty.this, R.string.pls_input_start, Toast.LENGTH_SHORT).show();
                } else {
                    currentStartLngSize -= SPACE_SCROLL_SIZE;
                    mStartLngView.setText(String.valueOf(currentStartLngSize));
                    setMarker(false, currentStartLatSize, currentStartLngSize, R.mipmap.first);
                }
            } else if (arrow == Constants.getInstance().Click_DOWN_ARROW) {
                if (TextUtils.isEmpty(mStartLngView.getText())) {
                    Toast.makeText(CreateAty.this, R.string.pls_input_start, Toast.LENGTH_SHORT).show();
                } else {
                    currentStartLatSize -= SPACE_SCROLL_SIZE;
                    mStartLatView.setText(String.valueOf(currentStartLatSize));
                    setMarker(false, currentStartLatSize, currentStartLngSize, R.mipmap.first);
                }
            } else {
                throw new RuntimeException("This is a error");
            }


        }
        // 第二个轮盘
        else if (roundMenuView == mRoundView_two) {
            Wheel_FLAG = 2;
            if (arrow == Constants.getInstance().Click_CENTER_ARROW) {
                return;

            } else if (arrow == Constants.getInstance().Click_RIGHT_ARROW) {
                if (TextUtils.isEmpty(mEndLatView.getText())) {
                    Toast.makeText(CreateAty.this, R.string.pls_input_start, Toast.LENGTH_SHORT).show();
                } else {
                    currentEdnLngSize += SPACE_SCROLL_SIZE;
                    mEndLngView.setText(String.valueOf(currentEdnLngSize));
                    // 绘制地图
                    setMarker(false, currentEndLatSize, currentEdnLngSize, R.mipmap.end);
                }
            } else if (arrow == Constants.getInstance().Click_UP_ARROW) {
                if (TextUtils.isEmpty(mEndLngView.getText())) {
                    Toast.makeText(CreateAty.this, R.string.pls_input_start, Toast.LENGTH_SHORT).show();
                } else {
                    currentEndLatSize += SPACE_SCROLL_SIZE;
                    mEndLatView.setText(String.valueOf(currentEndLatSize));
                    setMarker(false, currentEndLatSize, currentEdnLngSize, R.mipmap.end);
                }
            } else if (arrow == Constants.getInstance().Click_LEFT_ARROW) {
                if (TextUtils.isEmpty(mEndLatView.getText())) {
                    Toast.makeText(CreateAty.this, R.string.pls_input_start, Toast.LENGTH_SHORT).show();
                } else {
                    currentEdnLngSize -= SPACE_SCROLL_SIZE;
                    mEndLngView.setText(String.valueOf(currentEdnLngSize));
                    setMarker(false, currentEndLatSize, currentEdnLngSize, R.mipmap.end);
                }
            } else if (arrow == Constants.getInstance().Click_DOWN_ARROW) {
                if (TextUtils.isEmpty(mEndLngView.getText())) {
                    Toast.makeText(CreateAty.this, R.string.pls_input_start, Toast.LENGTH_SHORT).show();
                } else {
                    currentEndLatSize -= SPACE_SCROLL_SIZE;
                    mEndLatView.setText(String.valueOf(currentEndLatSize));
                    setMarker(false, currentEndLatSize, currentEdnLngSize, R.mipmap.end);
                }
            } else {
                throw new RuntimeException("This is a error");
            }
        }

        //连线
        getPolyLineOptions();
    }

    private double currentStartLatSize = 0;
    private double currentStartLngSize = 0;
    private double currentEndLatSize = 0;
    private double currentEdnLngSize = 0;
    //点击次数
    private int finger_count = 0;
    private int currentMarker;//判断点击的marker点是起点还是终点

    @Override
    public void onMapClick(LatLng latLng) {
        // 地图点击
        if (finger_count == 0) {
            currentStartLatSize = latLng.latitude;
            currentStartLngSize = latLng.longitude;
            setMarker(true, currentStartLatSize, currentStartLngSize, R.mipmap.first);
            mStartLatView.setText(String.valueOf(latLng.latitude));
            mStartLngView.setText(String.valueOf(latLng.longitude));
            Toast.makeText(CreateAty.this, R.string.again, Toast.LENGTH_SHORT).show();
        }
        if (finger_count == 1) {
            currentEndLatSize = latLng.latitude;
            currentEdnLngSize = latLng.longitude;
            setMarker(true, currentEndLatSize, currentEdnLngSize, R.mipmap.end);
            mEndLatView.setText(String.valueOf(latLng.latitude));
            mEndLngView.setText(String.valueOf(latLng.longitude));
            getPolyLineOptions();
        }
        ++finger_count;

        if (finger_count > 1 && currentMarker != 0) {
            // 之后的点击
            switch (currentMarker) {
                case 1:
                    currentStartLatSize = latLng.latitude;
                    currentStartLngSize = latLng.longitude;
                    setMarker(true, currentStartLatSize, currentStartLngSize,
                            currentMarker == 1 ? R.mipmap.first : R.mipmap.end);
                    mStartLatView.setText(String.valueOf(latLng.latitude));
                    mStartLngView.setText(String.valueOf(latLng.longitude));
                    currentMarker=0;
                    break;
                case 2:
                    currentEndLatSize = latLng.latitude;
                    currentEdnLngSize = latLng.longitude;
                    setMarker(true, currentEndLatSize, currentEdnLngSize,
                            currentMarker == 1 ? R.mipmap.first : R.mipmap.end);
                    mEndLatView.setText(String.valueOf(latLng.latitude));
                    mEndLngView.setText(String.valueOf(latLng.longitude));
                    currentMarker=0;
                    break;
            }
            getPolyLineOptions();
        }
    }


    //连线
    private void getPolyLineOptions() {
        // PolylineOptions
        //起点
        LatLng startLatLng = new LatLng(currentStartLatSize, currentStartLngSize);
        // 终点
        LatLng endLatLng = new LatLng(currentEndLatSize, currentEdnLngSize);
        List<LatLng> lineList = new ArrayList<LatLng>();
        lineList.add(startLatLng);
        lineList.add(endLatLng);
        //设置折线的属性
        OverlayOptions mOverlayOptions = new PolylineOptions()
                .width(5)
                .color(0xAAFF0000)
                .points(lineList);
        ;
        //在地图上绘制折线
        //mPloyline 折线对象
        if (!TextUtils.isEmpty(mEndLatView.getText()) && !TextUtils.isEmpty(mEndLngView.getText()) && !TextUtils.isEmpty(mStartLatView.getText()) && !TextUtils.isEmpty(mStartLngView.getText())) {
            Overlay mPolyline = mBaiduMap.addOverlay(mOverlayOptions);
        }
    }


    @Override
    public void onMapPoiClick(MapPoi mapPoi) {
        // poi
    }

    ;

    @Override
    public void onSnapshotReady(Bitmap bitmap) {
        // 百度地图截图
        mLocationMsgBody.setThumbnailPath(ImageHelper.bitmapToBase64(bitmap));
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == mCbLeft.getId()) {
            mLocationMsgBody.setHoverDirection("left");

        }
        if (checkedId == mCbRight.getId()) {
            mLocationMsgBody.setHoverDirection("right");
        }
        List<LocationMsgBody> locationMsgBodyList = SQLiteHelper.with(this).query(LocationMsgBody.class);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        switch (marker.getTitle()) {
            case mStartStr:
                Log.d(TAG, "--->点击了起点");
                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(CreateAty.this);
                normalDialog.setMessage(R.string.repaint_first);
                normalDialog.setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                currentMarker = 1;
                                Toast.makeText(CreateAty.this, R.string.mark_start, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                normalDialog.setNegativeButton(cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                normalDialog.show();

                break;
            case mEndStr:
                Log.d(TAG, "--->点击了终点");
                final AlertDialog.Builder normalDialog_end =
                        new AlertDialog.Builder(CreateAty.this);
                normalDialog_end.setMessage(R.string.repaint_end);
                normalDialog_end.setPositiveButton(yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                currentMarker = 2;
                                Toast.makeText(CreateAty.this, R.string.mark_end, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                normalDialog_end.setNegativeButton(cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                normalDialog_end.show();
                break;
            default:

                break;
        }

        return false;
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
            mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            // Log.d(TAG, "-----------" + location.getLatitude() + "-------" + location.getLongitude());
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {  //如果是第一次定位,就定位到以自己为中心
                isFirstLoc = false;
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(mLatLng).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                // 预置第一次进来，但是没有点击地图，默认初始化为当前位置坐标
                currentStartLatSize = mLatLng.latitude;
                currentStartLngSize = mLatLng.longitude;
            }
            Log.d(TAG, "---->" + location.getAddress().address + "---->" + location.getDistrict());
            //位置信息保存到数据库
            /**
             *1.国家:location.getCountry()
             * 2.城市:location.getCity()
             * 3.区域(例：南岸区)：location.getDistrict()
             * 4.地点(例：桃源路)：location.getStreet()
             * 5.详细地址：location.getAddrStr()
             */
            mCurrentAddress = location.getCity() + location.getDistrict() + location.getStreet();
        }

    }

    /**
     * 应该把这个方法替换成Handler执行
     *
     * @param confirm
     * @param latitude
     * @param longitude
     * @param resId
     */
    private void setMarker(boolean confirm, double latitude, double longitude, int resId) {
        runOnUiThread(() -> {
            // 只保留一个点
            if (mLastLocationIcon == resId) {
                // 清除之前的marker
                mMapView.getMap().clear();
            }
            if (mLastLocationIcon != resId) {
                if (mEndExitsFlag) {
                    mBaiduMap.clear();
                }
            }
            mLastLocationIcon = resId;
            //构建Marker图标
            // R.latyout, R.drawablw
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(resId);

            LatLng latLng = new LatLng(latitude, longitude);
            if (resId == R.mipmap.first) {
                //构建MarkerOption，用于在地图上添加Marker
                mStartOption = new MarkerOptions()
                        .position(latLng)
                        .title(mStartStr)
                        .icon(bitmap);    //在地图上添加Marker，并显示
            } else if (resId == R.mipmap.end) {
                // else {
                //构建MarkerOption，用于在地图上添加Marker
                mEndOption = new MarkerOptions()
                        .position(latLng)
                        .title(mEndStr)
                        .icon(bitmap);    //在地图上添加Marker，并显示
            }

            if (mStartOption != null) {
                mBaiduMap.addOverlay(mStartOption);
            }
            if (mEndOption != null) {
                mBaiduMap.addOverlay(mEndOption);
                mEndExitsFlag = true;
            }
        });
    }

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

    /**
     * EditText键盘的事件监听
     */
    private void initKeyBoardListener() {
        addEditTextListener(mStartLatView);
        addEditTextListener(mStartLngView);
        addEditTextListener(mEndLatView);
        addEditTextListener(mEndLngView);
        addEditTextListener(mEtHoverTime);
        addEditTextListener(mEtHoverHeight);
        addEditTextListener(mEtTitle);
    }

    public void addEditTextListener(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 1、数据更新到地图定位点
                if (TextUtils.isEmpty(s)) {
                    return;
                }
                if (s.toString().startsWith(".")) {
                    s.clear();
                    s.append("0.");
                }

                // 起点坐标
                if (editText == mStartLatView) {
                    currentStartLatSize = Double.parseDouble(s.toString());
                    if (TextUtils.isEmpty(mStartLngView.getText().toString())) {
                        return;
                    }
                    setMarker(true, currentStartLatSize, currentStartLngSize, R.mipmap.first);
                } else if (editText == mStartLngView) {
                    currentStartLngSize = Double.parseDouble(s.toString());
                    if (TextUtils.isEmpty(mStartLatView.getText().toString())) {
                        return;
                    }
                    setMarker(true, currentStartLatSize, currentStartLngSize, R.mipmap.first);
                }
                // 终点
                else if (editText == mEndLatView) {
                    currentEndLatSize = Double.parseDouble(s.toString());
                    if (TextUtils.isEmpty(mEndLngView.getText().toString())) {
                        return;
                    }
                    setMarker(true, currentEndLatSize, currentEdnLngSize, R.mipmap.end);
                } else if (editText == mEndLngView) {
                    currentEdnLngSize = Double.parseDouble(s.toString());
                    if (TextUtils.isEmpty(mEndLatView.getText().toString())) {
                        return;
                    }
                    setMarker(true, currentEndLatSize, currentEdnLngSize, R.mipmap.end);
                    getPolyLineOptions();
                } else if (editText == mEtHoverHeight) {
                    if (TextUtils.isEmpty(mEtHoverHeight.getText().toString())) {
                        return;
                    }
                    if (Integer.parseInt(mEtHoverHeight.getText().toString()) < -200
                            || Integer.parseInt(mEtHoverHeight.getText().toString()) > 500) {
                        Toast.makeText(CreateAty.this, ethover_height, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mSbHoverHeight.setProgress(Integer.parseInt(s.toString()));
                } else if (editText == mEtHoverTime) {

                    if (TextUtils.isEmpty(mEtHoverTime.getText().toString())) {
                        return;
                    }
                    if (Integer.parseInt(mEtHoverTime.getText().toString()) < 20
                            || Integer.parseInt(mEtHoverTime.getText().toString()) > 60) {
                        Toast.makeText(CreateAty.this, time, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mSbHoverTime.setProgress(Integer.parseInt(s.toString()));
                } else if (editText == mEtTitle) {
                    if (TextUtils.isEmpty(mEtTitle.getText().toString())) {
                        return;
                    }
                } else if (editText == mInterval) {
                    if (TextUtils.isEmpty(mInterval.getText().toString())) {
                        return;
                    }
                } else {
                    throw new RuntimeException("This is Error");
                }
            }
        });
    }

    public void showGPSContacts() {
        //得到系统的位置服务，判断GPS是否激活
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean ok = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ok) {
            if (Build.VERSION.SDK_INT >= 23) { //判断是否为android6.0系统版本，如果是，需要动态添加权限
                if (ContextCompat.checkSelfPermission(CreateAty.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(CreateAty.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // 没有权限，申请权限。
                    CreateAty.this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
                } else {
                    mLocationClient.start();
                }

            } else {
                mLocationClient.start();
            }
        } else {
            final AlertDialog.Builder normalDialog =
                    new AlertDialog.Builder(this);
            normalDialog.setIcon(R.drawable.ic_location_dialog);
            normalDialog.setTitle(R.string.location_error);
            normalDialog.setMessage(R.string.check_location);
            normalDialog.setPositiveButton(R.string.turn_on,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, PRIVATE_CODE);
                        }
                    });
            normalDialog.setNegativeButton(R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Toast.makeText(CreateAty.this, no_open, Toast.LENGTH_LONG).show();
                        }
                    });
            normalDialog.show();
        }
    }

    /**
     * Android6.0申请权限的回调方法
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
//             requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case 1:
                //如果用户取消，permissions可能为null.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //有权限
                    // 获取到权限，作相应处理
                } else {
                }
                break;
            case 2:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //有权限
                    // 获取到权限，作相应处理
                    mLocationClient.start();
                } else {
                    final AlertDialog.Builder normalDialog =
                            new AlertDialog.Builder(CreateAty.this);
                    normalDialog.setTitle("定位权限获取失败");
                    normalDialog.setMessage("测流需要获取定位权限，建议您选择允许授权定位服务");
                    normalDialog.setPositiveButton("立即开启",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            });
                    normalDialog.setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Toast.makeText(CreateAty.this, "定位权限无法获取，定位功能无法使用！", Toast.LENGTH_LONG).show();
                                }
                            });
                    normalDialog.show();

                }
                break;
            case 3:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //有权限
                    // 获取到权限，作相应处理
                } else {
                    final AlertDialog.Builder normalDialog =
                            new AlertDialog.Builder(CreateAty.this);
                    normalDialog.setMessage("在设置-应用-测流-权限中开启储存访问权限，以正常使用相关功能");
                    normalDialog.setPositiveButton("去设置",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            });
                    normalDialog.setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    normalDialog.show();

                }
                break;

        }
    }

}