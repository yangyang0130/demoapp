package com.yangyang.unmanneddrone.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yangyang.unmanneddrone.Adapter.MeasurementAdapter;
import com.yangyang.unmanneddrone.Body.MeasurementBody;
import com.yangyang.unmanneddrone.R;
import com.yangyang.unmanneddrone.base.MyActivity;
import com.yangyang.unmanneddrone.helper.DoubleClickHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MeasurementRecordAty extends MyActivity implements View.OnClickListener {

    private ImageView iv_back;
    private MeasurementAdapter measurementAdapter;
    private List<MeasurementBody> currentList;

    public static void start(Context context) {
        Intent intent = new Intent(context, MeasurementRecordAty.class);
        context.startActivity(intent);
    }

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
              Intent intent=new Intent(MeasurementRecordAty.this,DetailedDataAty.class);
              startActivity(intent);
            }
        });

        //
        for (int i = 0; i < 100; i++) {
            MeasurementBody body = new MeasurementBody();
            body.setAverageVelocity("1.23m/s");
            body.setFlow("128㎡/s");
            body.setMeasurementName("长江测流路线1");
            body.setMeasureTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new Date(System.currentTimeMillis())));
            currentList.add(body);
        }
        measurementAdapter.setList(currentList);
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
}
