package com.yangyang.unmanneddrone.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yangyang.tools.db.SQLiteHelper;
import com.yangyang.tools.permission.OnPermission;
import com.yangyang.tools.permission.Permission;
import com.yangyang.tools.permission.XXPermissions;
import com.yangyang.unmanneddrone.Adapter.MeasurementAdapter;
import com.yangyang.unmanneddrone.Body.ExcelDemoBean;
import com.yangyang.unmanneddrone.Body.LocationMsgBody;
import com.yangyang.unmanneddrone.Body.MeasurementBody;
import com.yangyang.unmanneddrone.Body.SelectionBody;
import com.yangyang.unmanneddrone.R;
import com.yangyang.unmanneddrone.View.CustomEditTextDialog;
import com.yangyang.unmanneddrone.base.MyActivity;
import com.yangyang.unmanneddrone.helper.DoubleClickHelper;
import com.yangyang.unmanneddrone.helper.ExcelUtil;
import com.yangyang.unmanneddrone.helper.ExcelUtils;
import com.yangyang.unmanneddrone.helper.IdHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

public class MeasurementRecordAty extends MyActivity implements View.OnClickListener {

    private ImageView iv_back;
    private MeasurementAdapter measurementAdapter;
    private List<MeasurementBody> currentList;
    private LocationMsgBody locationMsgBody;

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.aty_measurement_record);
        intView();
        setOnclickListener();
        intData();
    }

    private void intView() {
        RecyclerView mRecyclerView = findViewById(R.id.rv_measurement);
        iv_back = findViewById(R.id.iv_back);
        measurementAdapter = new MeasurementAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(measurementAdapter);
        currentList = new ArrayList<>();
    }


    private void setOnclickListener() {
        iv_back.setOnClickListener(this);
    }

    @SuppressLint("SimpleDateFormat")
    private void intData() {
        measurementAdapter.setListener(new MeasurementAdapter.Listener() {
            @Override
            public void itemOnClick(int position) {
                Intent intentItem = new Intent(MeasurementRecordAty.this, DetailedDataAty.class);
                startActivity(intentItem);

            }

            @Override
            public void editOnClick(int position) {
                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(MeasurementRecordAty.this);
                normalDialog.setMessage("修改");
                normalDialog.setPositiveButton("导入断面数据",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                XXPermissions.with(MeasurementRecordAty.this)
                                        .permission(Permission.Group.STORAGE)
                                        .request(new OnPermission() {
                                            @Override
                                            public void hasPermission(List<String> granted, boolean all) {
                                                if (all) {

                                                    // excel数据导入
                                                    try {
                                                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                                        intent.setType("application/vnd.ms-excel");
                                                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                                                        startActivityForResult(intent, 1);
                                                    } catch (Exception e) {
                                                        Log.e(TAG, "-----------_>" + e.toString());
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void noPermission(List<String> denied, boolean never) {
                                                Toast.makeText(MeasurementRecordAty.this, "当前权限不足,谢谢！", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                normalDialog.setNegativeButton("修改水位",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //...To-do
                                showDialogWaterLevel();
                            }
                        });

                normalDialog.setNeutralButton("关闭",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // ...To-do
                                dialog.dismiss();
                            }
                        });
                // 显示
                normalDialog.show();
            }

            @Override
            public void downloadOnClick(int position) {
                XXPermissions.with(MeasurementRecordAty.this)
                        .permission(Permission.Group.STORAGE)
                        .request(new OnPermission() {
                            @Override
                            public void hasPermission(List<String> granted, boolean all) {
                                if (all) {
                                    exportExcel(MeasurementRecordAty.this);
                                    Toast.makeText(MeasurementRecordAty.this, "已保存至"
                                                    + Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getAbsolutePath() + "/flowMeasurement",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void noPermission(List<String> denied, boolean never) {

                            }
                        });
            }
        });

        //
        for (int i = 0; i < 100; i++) {
            MeasurementBody body = new MeasurementBody();
            body.setAverageVelocity("平均流速：1.23m/s");
            body.setFlow("128㎡/s");
            body.setMeasurementName("长江测流路线1");
            body.setMeasureTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new Date(System.currentTimeMillis())));
            currentList.add(body);
        }
        measurementAdapter.setList(currentList);
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
                    SQLiteHelper.with(MeasurementRecordAty.this).insert(body);
                }
                locationMsgBody.setVoluntarilyData(idBuilder.toString());
                Toast.makeText(MeasurementRecordAty.this, "已导入断面", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            List<SelectionBody> bodyList = SQLiteHelper.with(MeasurementRecordAty.this).query(SelectionBody.class);
            Log.d(TAG, "-----插入数据----->" + bodyList);
        }
    }


    @Override
    public void onClick(View v) {
        //屏蔽短时间内双击
        if (DoubleClickHelper.isOnDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
        }
    }

    private String filePath = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getAbsolutePath()
            + "/flowMeasurement/";

    private void exportExcel(Context context) {

        StringBuilder fileName = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            char randomChar = getRandomChar();
            fileName.append(randomChar);
        }

        Log.d(TAG, "---->" + filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        // String excelFileName = "嘉陵江航线测流及流量计算表.xls";
        String excelFileName = fileName.toString() + ".xls";
        String sheetName = "无人机测流及流量计算表";
        List<ExcelDemoBean> excelDemoBeanList = new ArrayList<>();
        double random = Math.random();

        String[] title = {"施测时间",
                "2020/11/2 10:23:39",
                "至",
                "2020/11/2 10:41:19"};

        ExcelDemoBean titleBean = new ExcelDemoBean(
                "序号", "起点距", "间隔(m)", "测速垂线(m/s)");
        excelDemoBeanList.add(titleBean);
        for (int i = 0; i < 10; i++) {
            ExcelDemoBean excelDemoBean1 = new ExcelDemoBean(
                    String.valueOf(i + 1),
                    String.valueOf(random * i),
                    String.valueOf(5),
                    String.valueOf(0.95));
            excelDemoBeanList.add(excelDemoBean1);
        }
        ExcelDemoBean title1 = new ExcelDemoBean("", "流量计算表", "", "");
        excelDemoBeanList.add(title1);
        ExcelDemoBean flow = new ExcelDemoBean("断面流量", "278.39", "m3/s", "");
        excelDemoBeanList.add(flow);
        ExcelDemoBean area = new ExcelDemoBean("断面面积", "316.35", "m2", "");
        excelDemoBeanList.add(area);
        ExcelDemoBean rate = new ExcelDemoBean("平均流速", "0.00", "m/s", "");
        excelDemoBeanList.add(rate);
        ExcelDemoBean level = new ExcelDemoBean("水位", "", "", "");
        excelDemoBeanList.add(level);
        ExcelDemoBean personnel = new ExcelDemoBean("巡测人员", "", "", "");
        excelDemoBeanList.add(personnel);
        filePath = filePath + excelFileName;
        ExcelUtil.initExcel(filePath, sheetName, title);
        ExcelUtil.writeObjListToExcel(excelDemoBeanList, filePath, context);
    }


    private char getRandomChar() {
        String str = "";
        int hightPos;
        int lowPos;
        Random random = new Random();
        hightPos = (176 + Math.abs(random.nextInt(39)));
        lowPos = (161 + Math.abs(random.nextInt(93)));
        byte[] b = new byte[2];
        b[0] = (Integer.valueOf(hightPos)).byteValue();
        b[1] = (Integer.valueOf(lowPos)).byteValue();
        try {
            str = new String(b, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str.charAt(0);
    }

    public void showDialogWaterLevel() {
        final CustomEditTextDialog customDialog = new CustomEditTextDialog(this);
        final EditText editText = (EditText) customDialog.getEditText();//方法在CustomDialog中实现
        customDialog.setOnSureListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MeasurementRecordAty.this, "你点击了确定,输入的值为:" + editText.getText().toString(), Toast.LENGTH_SHORT).show();
                customDialog.dismiss();
            }
        });
        customDialog.setOnCanlceListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MeasurementRecordAty.this, "你点击了取消", Toast.LENGTH_SHORT).show();
                customDialog.dismiss();
            }
        });
        customDialog.setTile("请输入水位");
        customDialog.show();

        Window dialogWindow = customDialog.getWindow();
        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = 600;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.y = 0;
        dialogWindow.setAttributes(lp);

    }
}
