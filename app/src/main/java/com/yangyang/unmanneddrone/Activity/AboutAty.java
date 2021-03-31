package com.yangyang.unmanneddrone.Activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.yangyang.unmanneddrone.R;
import com.yangyang.unmanneddrone.base.MyActivity;
import com.yangyang.unmanneddrone.helper.ToastHelper;

@SuppressWarnings("all")
public class AboutAty extends MyActivity implements View.OnClickListener {

    private ImageView iv_back;
    private TextView privacyPolicy;
    private TextView userAgreement;
    private RelativeLayout recording;
    private RelativeLayout updates;

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.aty_about);
        iv_back = findViewById(R.id.iv_back);
        recording = findViewById(R.id.recording);
        updates = findViewById(R.id.updates);
        privacyPolicy = findViewById(R.id.privacyPolicy);
        userAgreement = findViewById(R.id.userAgreement);
        iv_back.setOnClickListener(this);
        privacyPolicy.setOnClickListener(this);
        recording.setOnClickListener(this);
        userAgreement.setOnClickListener(this);
        updates.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.userAgreement:
                // Toast.makeText(this, "暂未开放", Toast.LENGTH_SHORT).show();
                ToastHelper.getInstance().showToast(this, "暂未开放", Toast.LENGTH_SHORT);
                break;
            case R.id.privacyPolicy:
                // Toast.makeText(this, "暂未开放", Toast.LENGTH_SHORT).show();
                ToastHelper.getInstance().showToast(this, "当前已是最新版本", Toast.LENGTH_SHORT);
                break;
            case R.id.recording:
                // Toast.makeText(this, "当前已是最新版本", Toast.LENGTH_SHORT).show();
                ToastHelper.getInstance().showToast(this, "当前已是最新版本", Toast.LENGTH_SHORT, Gravity.CENTER);
                break;
            case R.id.updates:
                // oast.makeText(this, "当前暂无更新", Toast.LENGTH_SHORT).show();
                ToastHelper.getInstance().showToast(this, "当前已是最新版本", Toast.LENGTH_SHORT);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ToastHelper.getInstance().cancelToast();
    }
}
