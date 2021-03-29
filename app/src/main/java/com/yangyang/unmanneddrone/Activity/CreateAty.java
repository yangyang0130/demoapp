package com.yangyang.unmanneddrone.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.util.LogTime;
import com.yangyang.tools.db.SQLiteHelper;
import com.yangyang.tools.permission.OnPermission;
import com.yangyang.tools.permission.Permission;
import com.yangyang.tools.permission.XXPermissions;
import com.yangyang.unmanneddrone.Body.LocationMsgBody;
import com.yangyang.unmanneddrone.Body.SelectionBody;
import com.yangyang.unmanneddrone.Body.VoluntarilyBody;
import com.yangyang.unmanneddrone.R;
import com.yangyang.unmanneddrone.View.RoundMenuView;
import com.yangyang.unmanneddrone.base.MyActivity;
import com.yangyang.unmanneddrone.helper.Constants;
import com.yangyang.unmanneddrone.helper.DoubleClickHelper;
import com.yangyang.unmanneddrone.helper.ExcelUtils;
import com.yangyang.unmanneddrone.helper.IdHelper;
import com.yangyang.unmanneddrone.helper.ImageHelper;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateAty extends MyActivity
        implements View.OnClickListener, View.OnTouchListener,
        RoundMenuView.OnViewClickListener, BaiduMap.OnMapClickListener,
        BaiduMap.SnapshotReadyCallback, RadioGroup.OnCheckedChangeListener {

    /**
     * 选择的轮盘 默认选择起点
     * 1; 起点位置
     * 2： 终点位置
     */
    private int Wheel_FLAG = 0x001;
    // 设置微调 的间距，默认为0.01
    private final double SPACE_SCROLL_SIZE = 0.0001;
    /**
     * 百度SDK
     */
    private BaiduMap mBaiduMap;   // 定义百度地图对象
    private LocationClient mLocationClient;  //定义LocationClient
    private boolean isFirstLoc = true;  //定义第一次启动
    private MyLocationConfiguration.LocationMode mCurrentMode;  //定义当前定位模式
    private LatLng latLng;
    private LocationManager lm;
    private static final int PRIVATE_CODE = 1315;//开启GPS权限

    /**
     * xml控件View定义
     */
    private MapView mMapView;     // 定义百度地图组件
    private ImageView arrowMapView;
    //轮盘按钮
    private RoundMenuView roundView_one;
    private RoundMenuView roundView_two;
    private LinearLayout drawerRootView;
    private ViewFlipper viewFlipper;
    private EditText startLatView;
    private EditText startLngView;
    private EditText endLatView;
    private EditText endLngView;
    private ImageButton mLocation;
    private LinearLayout inputDataRootView;
    private LinearLayout saveDataRootView;
    private Button nextButtonView;
    private ImageButton ib_save, ib_start;
    //拖动条
    private SeekBar sb_Hover_time, sb_Hover_height;
    private EditText et_hover_time, et_hover_height;
    private EditText et_title;
    //动画
    Animation leftInAnimation;
    Animation leftOutAnimation;
    Animation rightInAnimation;
    Animation rightOutAnimation;
    //手势滑动
    private float downX;    //按下时 的X坐标
    private float downY;    //按下时 的Y坐标
    private Button importDataButtonView;
    private RadioButton leftRadioButtonView;
    private ImageView imageView;
    private RelativeLayout mapParentView;
    private ImageView excel;
    private RadioButton cb_left, cb_right;
    private RadioGroup rg_direction;


    /**
     * 设置marker
     */
    private int lastLocationIcon;
    //
    private OverlayOptions startOption = null;
    private OverlayOptions endOption = null;
    private boolean endExitsFlag = false;


    //数据库
    private LocationMsgBody locationMsgBody;
    private String currentAddress;


    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.aty_create);
        initView();
        setInit();
        initListener();
        showGPSContacts();
        initData();
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
                et_title.setText(msgBody.getRouteName());
                startLatView.setText(msgBody.getStartLat());
                startLngView.setText(msgBody.getStartLng());
                endLngView.setText(msgBody.getEndLng());
                endLatView.setText(msgBody.getEndLat());
                et_hover_time.setText(msgBody.getHoverTime());
                et_hover_height.setText(msgBody.getHoverHeight());
                //起点
                LatLng startLatLng = new LatLng(Double.parseDouble(startLatView.getText().toString()),
                        Double.parseDouble(startLngView.getText().toString()));
                // 终点
                LatLng endLatLng = new LatLng(Double.parseDouble(endLatView.getText().toString()),
                        Double.parseDouble(endLngView.getText().toString()));
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
                if ("right".equals(msgBody.getHover_direction())) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cb_right.setChecked(true);
                        }
                    });
                }
                if ("left".equals(msgBody.getHover_direction())) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cb_left.setChecked(true);
                        }
                    });
                }
                // 设置导入可见
                excel.setVisibility(TextUtils.isEmpty(msgBody.getVoluntarilyData()) ? View.GONE : View.VISIBLE);
                String[] data = msgBody.getVoluntarilyData().split(",");
                List<SelectionBody> selectionBodyList = new ArrayList<>();
                for (String datum : data) {
                    List<SelectionBody> bodyList = SQLiteHelper.with(this).query(SelectionBody.class,
                            "select * from " + SelectionBody.class.getSimpleName() + " where id=" + datum);
                    //
                    selectionBodyList.addAll(bodyList);
                }
                //
                Log.d(TAG, "---excel 数据--->" + selectionBodyList);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        roundView_one = findViewById(R.id.roundView_one);
        roundView_two = findViewById(R.id.roundView_two);
        //获取地图组件
        mMapView = findViewById(R.id.mv_map);
        //定义定位到当前位置按钮
        mLocation = findViewById(R.id.ib_location);

        imageView = findViewById(R.id.iv_image);
        mapParentView = findViewById(R.id.rl_map_parent);
        ///others
        arrowMapView = findViewById(R.id.iv_arrow_map);
        startLatView = findViewById(R.id.et_start_Latitude);
        startLngView = findViewById(R.id.et_start_Longitude);

        endLatView = findViewById(R.id.et_end_Latitude);
        endLngView = findViewById(R.id.et_end_Longitude);

        drawerRootView = findViewById(R.id.ll_drawer_root);
        viewFlipper = findViewById(R.id.viewFlipper);
        leftRadioButtonView = findViewById(R.id.cb_left);
        //动画效果
        leftInAnimation = AnimationUtils.loadAnimation(this, R.anim.left_in);
        leftOutAnimation = AnimationUtils.loadAnimation(this, R.anim.left_out);
        rightInAnimation = AnimationUtils.loadAnimation(this, R.anim.right_in);
        rightOutAnimation = AnimationUtils.loadAnimation(this, R.anim.right_out);

        inputDataRootView = findViewById(R.id.ll_input_data_root);
        saveDataRootView = findViewById(R.id.ll_save_data_root);
        nextButtonView = findViewById(R.id.bt_next);
        ib_save = findViewById(R.id.ib_save);
        ib_start = findViewById(R.id.ib_start);
        et_title = findViewById(R.id.et_title);
        excel = findViewById(R.id.excel);
        cb_left = findViewById(R.id.cb_left);
        cb_right = findViewById(R.id.cb_right);
        rg_direction = findViewById(R.id.hover_direction);


        // 数据导入page
        importDataButtonView = findViewById(R.id.button_Import_data);
        //拖动条
        et_hover_time = findViewById(R.id.et_hover_time);
        et_hover_height = findViewById(R.id.et_hover_height);
        sb_Hover_time = findViewById(R.id.sb_Hover_time);
        sb_Hover_height = findViewById(R.id.sb_Hover_height);
        sb_Hover_time.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                et_hover_time.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb_Hover_height.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                et_hover_height.setText(String.valueOf(progress));
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
        mLocation.setOnClickListener(this);
        drawerRootView.setOnTouchListener(this);
        nextButtonView.setOnClickListener(this);
        roundView_one.setmOnClickListener(this);
        roundView_two.setmOnClickListener(this);
        importDataButtonView.setOnClickListener(this);
        ib_save.setOnClickListener(this);
        leftRadioButtonView.setOnClickListener(this);
        rg_direction.setOnCheckedChangeListener(this);
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

        locationMsgBody = new LocationMsgBody();
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
                builder.target(latLng).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                break;
            //下一步按钮
            case R.id.bt_next:
                if (TextUtils.isEmpty(startLatView.getText().toString())
                        || TextUtils.isEmpty(startLngView.getText().toString())
                        || TextUtils.isEmpty(endLatView.getText().toString())
                        || TextUtils.isEmpty(endLngView.getText().toString())) {
                    Toast.makeText(this, "请确认输入数据是否完整", Toast.LENGTH_SHORT).show();
                    return;
                }
                mBaiduMap.snapshot(this);
                inputDataRootView.setVisibility(View.GONE);
                saveDataRootView.setVisibility(View.VISIBLE);
                ib_save.setVisibility(View.VISIBLE);
                ib_start.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_arrow_map:
                float rotation = arrowMapView.getRotation();
                if (rotation == 0) {
                    setPageSlideLeftParams();
                }
                if (rotation == 180) {
                    setPageSlideRightParams();
                }
                break;
            //导入数据按钮
            case R.id.button_Import_data:
                // 打开手机文件中选择excel文件，目前暂时写死
                XXPermissions.with(this)
                        .permission(Permission.Group.STORAGE)
                        .request(new OnPermission() {
                            @Override
                            public void hasPermission(List<String> granted, boolean all) {
                                if (all) {
                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                    intent.setType("*/*");
                                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                                    startActivityForResult(intent, 1);
                                    // excel数据导入
                                    try {
                                        List<SelectionBody> selectionBodyList = new ArrayList<>();
                                        InputStream inputStream = getAssets().open("selection_table.xlsx");
                                        List<SelectionBody> excelDataList = ExcelUtils.readExcel(inputStream, selectionBodyList);
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
                                        locationMsgBody.setVoluntarilyData(idBuilder.toString());
                                        excel.setVisibility(View.VISIBLE);
                                        Toast.makeText(CreateAty.this, "已导入断面", Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        Log.e(TAG, "-----------_>" + e.toString());
                                        e.printStackTrace();
                                    }
                                    List<SelectionBody> bodyList = SQLiteHelper.with(CreateAty.this).query(SelectionBody.class);
                                    Log.d(TAG, "-----插入数据----->" + bodyList);
                                }
                            }

                            @Override
                            public void noPermission(List<String> denied, boolean never) {
                                Toast.makeText(CreateAty.this, "当前权限不足,谢谢！", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case R.id.ib_save:
                // 保存数据到数据库
                //起点终点经纬度存入数据库
                locationMsgBody.setStartLat(startLatView.getText().toString());
                locationMsgBody.setEndLat(endLatView.getText().toString());
                locationMsgBody.setEndLng(endLngView.getText().toString());
                locationMsgBody.setStartLng(startLngView.getText().toString());
                //悬停时间存入数据库
                locationMsgBody.setHoverTime(et_hover_time.getText().toString());
                //悬停高度存入数据库
                locationMsgBody.setHoverHeight(et_hover_height.getText().toString());
                //航线名称存入数据库
                locationMsgBody.setRouteName(et_title.getText().toString());
                //创建时间
                locationMsgBody.setCreateTime(getTime(System.currentTimeMillis()));
                // 保存当前地址
                locationMsgBody.setLocation(currentAddress);
                locationMsgBody.setId(String.valueOf(System.currentTimeMillis()));
                long insert = SQLiteHelper.with(this).insert(locationMsgBody);
                if (insert > 0) {
                    Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                    // 加一个延迟1秒钟
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
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


    /*  @Override
      protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
          super.onActivityResult(requestCode, resultCode, data);
          if (data == null) {
              return;
          }
          if (requestCode == Constants.getInstance().FILE_REQUEST_CODE) {
              // excel数据导入
              Uri uri = data.getData();
              File file = null;   //图片地址
              try {
                  file = new File(new URI(uri.toString()));
              } catch (URISyntaxException e) {
                  e.printStackTrace();
              }
              try {
                  List<SelectionBody> selectionBodyList = new ArrayList<>();
                  List<SelectionBody> excelDataList = ExcelUtils.readExcel(file, selectionBodyList);
                  Log.d(TAG, "--->" + excelDataList.size() + "--->" + excelDataList);
                  // 暂存入数据库
                  // SQLiteHelper.with(CreateAty.this).insert(excelDataList);
                  for (SelectionBody body : excelDataList) {
                      SQLiteHelper.with(CreateAty.this).insert(body);
                  }
              } catch (Exception e) {
                  Log.e(TAG, "-----------_>" + e.toString());
                  e.printStackTrace();
              }
          }
      }
  */
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
                            setPageSlideRightParams();
                            saveDataRootView.setVisibility(View.GONE);
                            inputDataRootView.setVisibility(View.GONE);
                            break;
                        case 'l':
                            action = "左";
                            setPageSlideLeftParams();
                            saveDataRootView.setVisibility(View.VISIBLE);
                            inputDataRootView.setVisibility(View.VISIBLE);
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
     * 设置右滑属性
     */
    private void setPageSlideRightParams() {
        ViewGroup.LayoutParams lp;
        lp = drawerRootView.getLayoutParams();
        lp.width = 180;
        lp.height = LinearLayout.LayoutParams.MATCH_PARENT;
        drawerRootView.setLayoutParams(lp);
        arrowMapView.setRotation(0);
        roundView_one.setVisibility(View.GONE);
        roundView_two.setVisibility(View.GONE);
    }

    /**
     * 设置左滑属性
     */
    private void setPageSlideLeftParams() {
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

    @Override
    public void onViewClick(RoundMenuView roundMenuView, int arrow) {
        // 第一个轮盘
        if (roundMenuView == roundView_one) {
            Wheel_FLAG = 1;
            if (arrow == Constants.getInstance().Click_CENTER_ARROW) {
                //setMarker(currentLatSize, currentLngSize, R.mipmap.first);
                // 替换为起点的图标
                setMarker(true, currentStartLatSize, currentStartLngSize, R.mipmap.first);
                Wheel_FLAG = 2;
            } else if (arrow == Constants.getInstance().Click_RIGHT_ARROW) {
                //edittext
                currentStartLngSize += SPACE_SCROLL_SIZE;
                startLngView.setText(String.valueOf(currentStartLngSize));
                // 绘制地图
                setMarker(false, currentStartLatSize, currentStartLngSize, R.mipmap.first);
            } else if (arrow == Constants.getInstance().Click_UP_ARROW) {
                currentStartLatSize += SPACE_SCROLL_SIZE;
                startLatView.setText(String.valueOf(currentStartLatSize));
                setMarker(false, currentStartLatSize, currentStartLngSize, R.mipmap.first);
            } else if (arrow == Constants.getInstance().Click_LEFT_ARROW) {
                currentStartLngSize -= SPACE_SCROLL_SIZE;
                startLngView.setText(String.valueOf(currentStartLngSize));
                setMarker(false, currentStartLatSize, currentStartLngSize, R.mipmap.first);
            } else if (arrow == Constants.getInstance().Click_DOWN_ARROW) {
                currentStartLatSize -= SPACE_SCROLL_SIZE;
                startLatView.setText(String.valueOf(currentStartLatSize));
                setMarker(false, currentStartLatSize, currentStartLngSize, R.mipmap.first);
            } else {
                throw new RuntimeException("This is a error");
            }
        }
        // 第二个轮盘
        else if (roundMenuView == roundView_two) {
            Wheel_FLAG = 2;
            if (arrow == Constants.getInstance().Click_CENTER_ARROW) {
                setMarker(true, currentEndLatSize, currentEdnLngSize, R.mipmap.end);
                // PolylineOptions
                if (TextUtils.isEmpty(startLatView.getText().toString())
                        || TextUtils.isEmpty(startLngView.getText().toString())
                        || TextUtils.isEmpty(endLatView.getText().toString())
                        || TextUtils.isEmpty(endLngView.getText().toString())) {
                    Toast.makeText(this, "请核对起始点坐标", Toast.LENGTH_SHORT).show();
                    return;
                }
                //起点
                LatLng startLatLng = new LatLng(Double.parseDouble(startLatView.getText().toString()),
                        Double.parseDouble(startLngView.getText().toString()));
                // 终点
                LatLng endLatLng = new LatLng(Double.parseDouble(endLatView.getText().toString()),
                        Double.parseDouble(endLngView.getText().toString()));
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
            } else if (arrow == Constants.getInstance().Click_RIGHT_ARROW) {
                currentEdnLngSize += SPACE_SCROLL_SIZE;
                endLngView.setText(String.valueOf(currentEdnLngSize));
                // 绘制地图
                setMarker(false, currentEndLatSize, currentEdnLngSize, R.mipmap.end);
            } else if (arrow == Constants.getInstance().Click_UP_ARROW) {
                currentEndLatSize += SPACE_SCROLL_SIZE;
                endLatView.setText(String.valueOf(currentEndLatSize));
                setMarker(false, currentEndLatSize, currentEdnLngSize, R.mipmap.end);
            } else if (arrow == Constants.getInstance().Click_LEFT_ARROW) {
                currentEdnLngSize -= SPACE_SCROLL_SIZE;
                endLngView.setText(String.valueOf(currentEdnLngSize));
                setMarker(false, currentEndLatSize, currentEdnLngSize, R.mipmap.end);
            } else if (arrow == Constants.getInstance().Click_DOWN_ARROW) {
                currentEndLatSize -= SPACE_SCROLL_SIZE;
                endLatView.setText(String.valueOf(currentEndLatSize));
                setMarker(false, currentEndLatSize, currentEdnLngSize, R.mipmap.end);
            } else {
                throw new RuntimeException("This is a error");
            }
        }
    }

    private double currentStartLatSize = 0;
    private double currentStartLngSize = 0;
    private double currentEndLatSize = 0;
    private double currentEdnLngSize = 0;

    @Override
    public void onMapClick(LatLng latLng) {
        // 地图点击
        Log.d(TAG, "---点击的经纬度-->" + latLng.latitude + "----------" + latLng.longitude);
        switch (Wheel_FLAG) {
            case 1: // 点击了起点
                currentStartLatSize = latLng.latitude;
                currentStartLngSize = latLng.longitude;
                setMarker(true, currentStartLatSize, currentStartLngSize, R.mipmap.first);
                startLatView.setText(String.valueOf(latLng.latitude));
                startLngView.setText(String.valueOf(latLng.longitude));
                break;
            case 2:     // 点击了终点
                currentEndLatSize = latLng.latitude;
                currentEdnLngSize = latLng.longitude;
                setMarker(true, currentEndLatSize, currentEdnLngSize, R.mipmap.end);
                endLatView.setText(String.valueOf(latLng.latitude));
                endLngView.setText(String.valueOf(latLng.longitude));
                break;
            default:
                break;
        }
    }

    @Override
    public void onMapPoiClick(MapPoi mapPoi) {
        // poi
    }

    @Override
    public void onSnapshotReady(Bitmap bitmap) {
        // 百度地图截图
//        Glide.with(this)
//                .load(bitmap)
//                .into(imageView);
        // 数据库保存图片
        locationMsgBody.setThumbnail_path(ImageHelper.bitmapToBase64(bitmap));
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == cb_left.getId()) {
            locationMsgBody.setHover_direction("left");

        }
        if (checkedId == cb_right.getId()) {
            locationMsgBody.setHover_direction("right");
        }
        List<LocationMsgBody> locationMsgBodyList = SQLiteHelper.with(this).query(LocationMsgBody.class);
        Log.d(TAG,"---------------数据库---------"+locationMsgBodyList);
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
                // 预置第一次进来，但是没有点击地图，默认初始化为当前位置坐标
                currentStartLatSize = latLng.latitude;
                currentStartLngSize = latLng.longitude;
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
            currentAddress = location.getCity() + location.getDistrict() + location.getStreet();
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
            if (lastLocationIcon == resId) {
                // 清除之前的marker
                mMapView.getMap().clear();
            }
            if (lastLocationIcon != resId) {
                if (endExitsFlag) {
                    mBaiduMap.clear();
                }
            }
            lastLocationIcon = resId;
            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(resId);
            LatLng latLng = new LatLng(latitude, longitude);
            if (resId == R.mipmap.first) {
                //构建MarkerOption，用于在地图上添加Marker
                startOption = new MarkerOptions()
                        .position(latLng)
                        .icon(bitmap);    //在地图上添加Marker，并显示
            } else if (resId == R.mipmap.end) {
                // else {
                //构建MarkerOption，用于在地图上添加Marker
                endOption = new MarkerOptions()
                        .position(latLng)
                        .icon(bitmap);    //在地图上添加Marker，并显示
            }

            if (startOption != null) {
                mBaiduMap.addOverlay(startOption);
            }
            if (endOption != null) {
                mBaiduMap.addOverlay(endOption);
                endExitsFlag = true;
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
        addEditTextListener(startLatView);
        addEditTextListener(startLngView);
        addEditTextListener(endLatView);
        addEditTextListener(endLngView);
        addEditTextListener(et_hover_time);
        addEditTextListener(et_hover_height);
        addEditTextListener(et_title);
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

                // 起点坐标
                if (editText == startLatView) {
                    currentStartLatSize = Double.parseDouble(s.toString());
                    if (TextUtils.isEmpty(startLngView.getText().toString())) {
                        return;
                    }
                    setMarker(true, currentStartLatSize, currentStartLngSize, R.mipmap.first);
                } else if (editText == startLngView) {
                    currentStartLngSize = Double.parseDouble(s.toString());
                    if (TextUtils.isEmpty(startLatView.getText().toString())) {
                        return;
                    }
                    setMarker(true, currentStartLatSize, currentStartLngSize, R.mipmap.first);
                }
                // 终点
                else if (editText == endLatView) {
                    currentEndLatSize = Double.parseDouble(s.toString());
                    if (TextUtils.isEmpty(endLngView.getText().toString())) {
                        return;
                    }
                    setMarker(true, currentEndLatSize, currentEdnLngSize, R.mipmap.end);

                } else if (editText == endLngView) {
                    currentEdnLngSize = Double.parseDouble(s.toString());
                    if (TextUtils.isEmpty(endLatView.getText().toString())) {
                        return;
                    }
                    setMarker(true, currentEndLatSize, currentEdnLngSize, R.mipmap.end);
                } else if (editText == et_hover_height) {
                    if (TextUtils.isEmpty(et_hover_height.getText().toString())) {
                        return;
                    }
                    sb_Hover_height.setProgress(Integer.parseInt(s.toString()));

                } else if (editText == et_hover_time) {
                    if (TextUtils.isEmpty(et_hover_time.getText().toString())) {
                        return;
                    }
                    sb_Hover_time.setProgress(Integer.parseInt(s.toString()));
                } else if (editText == et_title) {
                    if (TextUtils.isEmpty(et_title.getText().toString())) {
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
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (ok) {
            if (Build.VERSION.SDK_INT >= 23) { //判断是否为android6.0系统版本，如果是，需要动态添加权限
                if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                        (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                    // 没有权限，申请权限。
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
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
            normalDialog.setTitle("定位失败");
            normalDialog.setMessage("请检查是否开启定位服务?");
            normalDialog.setPositiveButton("立即开启",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, PRIVATE_CODE);
                        }
                    });
            normalDialog.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Toast.makeText(CreateAty.this, "定位未打开，无法获取当前位置！", Toast.LENGTH_LONG).show();
                        }
                    });
            normalDialog.show();
        }

    }
}