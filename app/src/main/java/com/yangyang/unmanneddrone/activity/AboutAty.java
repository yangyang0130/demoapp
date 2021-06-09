package com.yangyang.unmanneddrone.activity;

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
//关于界面
@SuppressWarnings("all")
public class AboutAty extends MyActivity implements View.OnClickListener {

    private ImageView mIvBack;
    private TextView mPrivacyPolicy;
    private TextView mUserAgreement;
    private RelativeLayout mRecording;
    private RelativeLayout mUpdates;

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.aty_about);
        mIvBack = findViewById(R.id.iv_back);
        mRecording = findViewById(R.id.recording);
        mUpdates = findViewById(R.id.updates);
        mPrivacyPolicy = findViewById(R.id.privacyPolicy);
        mUserAgreement = findViewById(R.id.userAgreement);
        mIvBack.setOnClickListener(this);
        mPrivacyPolicy.setOnClickListener(this);
        mRecording.setOnClickListener(this);
        mUserAgreement.setOnClickListener(this);
        mUpdates.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.userAgreement:
                // Toast.makeText(this, "暂未开放", Toast.LENGTH_SHORT).show();
                ToastHelper.getInstance().showToast(this, getString(R.string.unopen), Toast.LENGTH_SHORT);
                break;
            case R.id.privacyPolicy:
                // Toast.makeText(this, "暂未开放", Toast.LENGTH_SHORT).show();
                ToastHelper.getInstance().showToast(this, getString(R.string.is_new_ver), Toast.LENGTH_SHORT);
                break;
            case R.id.recording:
                // Toast.makeText(this, "当前已是最新版本", Toast.LENGTH_SHORT).show();
                ToastHelper.getInstance().showToast(this, getString(R.string.currently_the_latest_version), Toast.LENGTH_SHORT, Gravity.CENTER);
                break;
            case R.id.updates:
                // oast.makeText(this, "当前暂无更新", Toast.LENGTH_SHORT).show();
                ToastHelper.getInstance().showToast(this, getString(R.string.currently_the_latest_version), Toast.LENGTH_SHORT);
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
