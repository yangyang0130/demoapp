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
 * ??????????????????
 */
public class CreateAty extends MyActivity
        implements View.OnClickListener, View.OnTouchListener,
        RoundMenuView.OnViewClickListener, BaiduMap.OnMapClickListener, BaiduMap.OnMarkerClickListener,
        BaiduMap.SnapshotReadyCallback, RadioGroup.OnCheckedChangeListener {
    /**
     * ??????????????? ??????????????????
     * 1; ????????????
     * 2??? ????????????
     */
    private int Wheel_FLAG = 0x001;
    // ???????????? ?????????????????????0.000001
    private final double SPACE_SCROLL_SIZE = 0.000005;
    /**
     * ??????SDK
     */
    private BaiduMap mBaiduMap;   // ????????????????????????
    private LocationClient mLocationClient;  //??????LocationClient
    private boolean isFirstLoc = true;  //?????????????????????
    private MyLocationConfiguration.LocationMode mCurrentMode;  //????????????????????????
    private LatLng mLatLng;
    private LocationManager mLocationManager;
    private static final int PRIVATE_CODE = 1315;//??????GPS??????
    private final String mStartStr = "??????";
    private final String mEndStr = "??????";
    /**
     * xml??????View??????
     */
    private MapView mMapView;     // ????????????????????????
    private ImageView mArrowMapView;
    //????????????
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
    //?????????
    private SeekBar mSbHoverTime, mSbHoverHeight;
    private EditText mEtHoverTime, mEtHoverHeight;
    private EditText mEtTitle;
    //????????????
    private float mDownX;    //????????? ???X??????
    private float mDownY;    //????????? ???Y??????
    private Button mImportDataButtonView;
    private RadioButton mLeftRadioButtonView;
    private ImageView mImageView;
    private RelativeLayout mMapParentView;
    private ImageView mIvExcel;
    private RadioButton mCbLeft, mCbRight;
    private RadioGroup mRgDirection;
    private EditText mInterval;
    /**
     * ??????marker
     */
    private int mLastLocationIcon;
    //
    private OverlayOptions mStartOption = null;
    private OverlayOptions mEndOption = null;
    private boolean mEndExitsFlag = false;


    //?????????
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
     * ?????????????????????
     */
    private void initData() {
        String transId = getIntent().getStringExtra("transId");
        if (!TextUtils.isEmpty(transId)) {
            List<LocationMsgBody> msgBodyList = SQLiteHelper.with(this).query(LocationMsgBody.class,
                    "select * from " + LocationMsgBody.class.getSimpleName() + " where id=" + transId);
            Log.d(TAG, "-----?????????????????????------>" + msgBodyList);
            // ???????????????????????????
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
                //??????
                LatLng startLatLng = new LatLng(Double.parseDouble(mStartLatView.getText().toString()),
                        Double.parseDouble(mStartLngView.getText().toString()));
                // ??????
                LatLng endLatLng = new LatLng(Double.parseDouble(mEndLatView.getText().toString()),
                        Double.parseDouble(mEndLngView.getText().toString()));
                List<LatLng> lineList = new ArrayList<LatLng>();
                lineList.add(startLatLng);
                lineList.add(endLatLng);
                //?????????????????????
                OverlayOptions mOverlayOptions = new PolylineOptions()
                        .width(10)
                        .color(0xAAFF0000)
                        .points(lineList);
                //????????????????????????
                //mPloyline ????????????
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
                // ??????????????????
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
        //??????????????????
        mMapView = findViewById(R.id.mv_map);
        //?????????????????????????????????
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
        //????????????
        //??????
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


        // ????????????page
        mImportDataButtonView = findViewById(R.id.button_Import_data);
        //?????????
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

    ////?????????????????????
    public void showContacts() {
        if (Build.VERSION.SDK_INT >= 23) { //???????????????android6.0???????????????????????????????????????????????????
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

    ////?????????????????????
    public void showWriteContacts() {

    }


    private void setInit() {
        // other init
        // map init
        mBaiduMap = mMapView.getMap();  //????????????????????????
        mBaiduMap.setMyLocationEnabled(true);

        mLocationClient = new LocationClient(this);  //??????LocationClient???
        mLocationClient.registerLocationListener(new MyLocationListener());   //??????????????????
        initLocation();  //??????initLocation()??????????????????????????????

        initKeyBoardListener();

        mLocationMsgBody = new LocationMsgBody();
    }

    private void initLocation() {  //??????????????????????????????
        //??????LocationClientOption?????????????????????????????????
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");  //??????????????????
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setScanSpan(3000);      //1???????????????
        option.setOpenGps(true);      //??????GPS
        option.setAddrType("all");//????????????????????????????????????
        option.setPriority(LocationClientOption.NetWorkFirst); // ??????????????????
        option.setPriority(LocationClientOption.GpsFirst);       //gps
        mLocationClient.setLocOption(option);  //???????????????????????????
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;  //??????????????????

        // ??????logo
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
        // ????????????
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient.start();

        /*
        ?????????????????????
         */
        mBaiduMap.setOnMapClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        //????????????????????????
        if (DoubleClickHelper.isOnDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            //????????????????????????
            case R.id.ib_location:
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(mLatLng).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                showGPSContacts();
                break;
            //???????????????
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
            //??????????????????
            case R.id.button_Import_data:
                if (Build.VERSION.SDK_INT >= 23) { //???????????????android6.0???????????????????????????????????????????????????
                    if (ContextCompat.checkSelfPermission(CreateAty.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        // ??????????????????????????????
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
                // ????????????????????????
                //????????????????????????????????????
                mLocationMsgBody.setStartLat(mStartLatView.getText().toString());
                mLocationMsgBody.setEndLat(mEndLatView.getText().toString());
                mLocationMsgBody.setEndLng(mEndLngView.getText().toString());
                mLocationMsgBody.setStartLng(mStartLngView.getText().toString());
                //???????????????????????????
                mLocationMsgBody.setHoverTime(mEtHoverTime.getText().toString());
                //???????????????????????????
                mLocationMsgBody.setHoverHeight(mEtHoverHeight.getText().toString());
                //???????????????????????????
                mLocationMsgBody.setRouteName(mEtTitle.getText().toString());
                //????????????
                mLocationMsgBody.setCreateTime(getTime(System.currentTimeMillis()));
                //??????????????????
                mLocationMsgBody.setNumInterval(mInterval.getText().toString());
                // ??????????????????
                mLocationMsgBody.setLocation(mCurrentAddress);

                if (TextUtils.isEmpty(mSgId)) {
                    mLocationMsgBody.setId(String.valueOf(System.currentTimeMillis()));
                    long insert = SQLiteHelper.with(this).insert(mLocationMsgBody);
                    if (insert > 0) {
                        // Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
                        ToastHelper.getInstance().showToast(getContext(), getString(R.string.save), Toast.LENGTH_SHORT);
                    }
                } else {
                    mLocationMsgBody.setId(mSgId);
                    long update = SQLiteHelper.with(this).update(mLocationMsgBody, null, null);
                    if (update > 0) {
                        // Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
                        ToastHelper.getInstance().showToast(getContext(), getString(R.string.modified), Toast.LENGTH_SHORT);
                    }
                }
                break;
            default:
                break;
        }
    }

    //?????????????????????
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
                // ??????????????????
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
                // Toast.makeText(CreateAty.this, "???????????????", Toast.LENGTH_SHORT).show();
                ToastHelper.getInstance().showToast(getContext(), getString(R.string.have_input), Toast.LENGTH_SHORT);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            List<SelectionBody> bodyList = SQLiteHelper.with(CreateAty.this).query(SelectionBody.class);
        }
    }

    //????????????
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        String action = "";
        //?????????????????????????????????
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //???????????????????????????
                mDownX = x;
                mDownY = y;
                Log.e(TAG, "=======hang downX???" + x);
                Log.e(TAG, "=======hang downY???" + y);
                //
                //??????????????????
                float dx_down = x - mDownX;
                float dy_down = y - mDownY;

                Log.d(TAG, "-   dx_down-->" + dx_down);
                Log.d(TAG, "-   dy_down-->" + dy_down);
                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "=======hang onX???" + x);
                Log.e(TAG, "=======hang onY???" + y);

                //??????????????????
                float dx = x - mDownX;
                float dy = y - mDownY;
                //????????????????????????
                if (Math.abs(dx) > 10 && Math.abs(dy) > 10) {
                    Log.d(TAG, "----->" + dx);
                    //???????????????????????????
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
     * ??????????????????
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
     * ??????????????????
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
     * ????????????????????? ???????????? * @param dx X??????????????? * @param dy Y??????????????? * @return ???????????????
     */
    private int getOrientation(float dx, float dy) {
        if (Math.abs(dx) > Math.abs(dy)) {
            //X?????????
            return dx > 0 ? 'r' : 'l';
        } else {
            //Y?????????
            return dy > 0 ? 'b' : 't';
        }
    }

    /**
     * ??????
     *
     * @param roundMenuView
     * @param arrow
     */
    @Override
    public void onViewClick(RoundMenuView roundMenuView, int arrow) {
        // ???????????????
        if (roundMenuView == mRoundView_one) {
            Wheel_FLAG = 1;
            if (arrow == Constants.getInstance().Click_CENTER_ARROW) {
                return;
                //setMarker(currentLatSize, currentLngSize, R.mipmap.first);
                // ????????????????????????
                // setMarker(true, currentStartLatSize, currentStartLngSize, R.mipmap.first);
                // Wheel_FLAG = 2;
            } else if (arrow == Constants.getInstance().Click_RIGHT_ARROW) {
                if (TextUtils.isEmpty(mStartLatView.getText())) {
                    Toast.makeText(CreateAty.this, R.string.pls_input_start, Toast.LENGTH_SHORT).show();
                } else {
                    //edittext
                    currentStartLngSize += SPACE_SCROLL_SIZE;
                    mStartLngView.setText(String.valueOf(currentStartLngSize));
                    // ????????????
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
        // ???????????????
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
                    // ????????????
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

        //??????
        getPolyLineOptions();
    }

    private double currentStartLatSize = 0;
    private double currentStartLngSize = 0;
    private double currentEndLatSize = 0;
    private double currentEdnLngSize = 0;
    //????????????
    private int finger_count = 0;
    private int currentMarker;//???????????????marker????????????????????????

    @Override
    public void onMapClick(LatLng latLng) {
        // ????????????
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
            // ???????????????
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


    //??????
    private void getPolyLineOptions() {
        // PolylineOptions
        //??????
        LatLng startLatLng = new LatLng(currentStartLatSize, currentStartLngSize);
        // ??????
        LatLng endLatLng = new LatLng(currentEndLatSize, currentEdnLngSize);
        List<LatLng> lineList = new ArrayList<LatLng>();
        lineList.add(startLatLng);
        lineList.add(endLatLng);
        //?????????????????????
        OverlayOptions mOverlayOptions = new PolylineOptions()
                .width(5)
                .color(0xAAFF0000)
                .points(lineList);
        ;
        //????????????????????????
        //mPloyline ????????????
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
        // ??????????????????
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
                Log.d(TAG, "--->???????????????");
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
                Log.d(TAG, "--->???????????????");
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

    //?????????????????????
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //???????????????????????????????????????
            if (location == null || mMapView == null) {
                return;
            }
            //
            if (location.getLatitude() == 4.9E-324 || location.getLongitude() == 4.9E-324) {
                return;
            }

            // ??????????????????
            MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
                    // ?????????????????????????????????????????????????????????0-360
                    .direction(180)
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();

            // ??????????????????
            mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            // Log.d(TAG, "-----------" + location.getLatitude() + "-------" + location.getLongitude());
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {  //????????????????????????,??????????????????????????????
                isFirstLoc = false;
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(mLatLng).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                // ???????????????????????????????????????????????????????????????????????????????????????
                currentStartLatSize = mLatLng.latitude;
                currentStartLngSize = mLatLng.longitude;
            }
            Log.d(TAG, "---->" + location.getAddress().address + "---->" + location.getDistrict());
            //??????????????????????????????
            /**
             *1.??????:location.getCountry()
             * 2.??????:location.getCity()
             * 3.??????(???????????????)???location.getDistrict()
             * 4.??????(???????????????)???location.getStreet()
             * 5.???????????????location.getAddrStr()
             */
            mCurrentAddress = location.getCity() + location.getDistrict() + location.getStreet();
        }

    }

    /**
     * ??????????????????????????????Handler??????
     *
     * @param confirm
     * @param latitude
     * @param longitude
     * @param resId
     */
    private void setMarker(boolean confirm, double latitude, double longitude, int resId) {
        runOnUiThread(() -> {
            // ??????????????????
            if (mLastLocationIcon == resId) {
                // ???????????????marker
                mMapView.getMap().clear();
            }
            if (mLastLocationIcon != resId) {
                if (mEndExitsFlag) {
                    mBaiduMap.clear();
                }
            }
            mLastLocationIcon = resId;
            //??????Marker??????
            // R.latyout, R.drawablw
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(resId);

            LatLng latLng = new LatLng(latitude, longitude);
            if (resId == R.mipmap.first) {
                //??????MarkerOption???????????????????????????Marker
                mStartOption = new MarkerOptions()
                        .position(latLng)
                        .title(mStartStr)
                        .icon(bitmap);    //??????????????????Marker????????????
            } else if (resId == R.mipmap.end) {
                // else {
                //??????MarkerOption???????????????????????????Marker
                mEndOption = new MarkerOptions()
                        .position(latLng)
                        .title(mEndStr)
                        .icon(bitmap);    //??????????????????Marker????????????
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
        //???activity??????onResume?????????mMapView. onResume ()?????????????????????????????????
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        //???activity??????onPause?????????mMapView. onPause ()?????????????????????????????????
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //???activity??????onDestroy?????????mMapView.onDestroy()?????????????????????????????????
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    /**
     * EditText?????????????????????
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
                // 1?????????????????????????????????
                if (TextUtils.isEmpty(s)) {
                    return;
                }
                if (s.toString().startsWith(".")) {
                    s.clear();
                    s.append("0.");
                }

                // ????????????
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
                // ??????
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
        //????????????????????????????????????GPS????????????
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean ok = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ok) {
            if (Build.VERSION.SDK_INT >= 23) { //???????????????android6.0???????????????????????????????????????????????????
                if (ContextCompat.checkSelfPermission(CreateAty.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(CreateAty.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // ??????????????????????????????
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
     * Android6.0???????????????????????????
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
//             requestCode????????????????????????????????????checkSelfPermission?????????
            case 1:
                //?????????????????????permissions?????????null.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //?????????
                    // ?????????????????????????????????
                } else {
                }
                break;
            case 2:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //?????????
                    // ?????????????????????????????????
                    mLocationClient.start();
                } else {
                    final AlertDialog.Builder normalDialog =
                            new AlertDialog.Builder(CreateAty.this);
                    normalDialog.setTitle("????????????????????????");
                    normalDialog.setMessage("????????????????????????????????????????????????????????????????????????");
                    normalDialog.setPositiveButton("????????????",
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
                    normalDialog.setNegativeButton("??????",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Toast.makeText(CreateAty.this, "??????????????????????????????????????????????????????", Toast.LENGTH_LONG).show();
                                }
                            });
                    normalDialog.show();

                }
                break;
            case 3:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //?????????
                    // ?????????????????????????????????
                } else {
                    final AlertDialog.Builder normalDialog =
                            new AlertDialog.Builder(CreateAty.this);
                    normalDialog.setMessage("?????????-??????-??????-???????????????????????????????????????????????????????????????");
                    normalDialog.setPositiveButton("?????????",
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
                    normalDialog.setNegativeButton("??????",
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