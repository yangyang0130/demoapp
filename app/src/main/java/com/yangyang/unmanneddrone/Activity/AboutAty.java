package com.yangyang.unmanneddrone.Activity;

import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.yangyang.unmanneddrone.R;
import com.yangyang.unmanneddrone.base.MyActivity;

public class AboutAty extends MyActivity implements View.OnClickListener {
    private ImageView iv_back;

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.aty_about);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
        }
    }
}
