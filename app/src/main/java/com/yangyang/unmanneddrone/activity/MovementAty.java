package com.yangyang.unmanneddrone.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.yangyang.unmanneddrone.R;
import com.yangyang.unmanneddrone.base.MyActivity;

//手动测量
public class MovementAty extends MyActivity {
    private ImageView mIvBack;
    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.aty_movement);
        mIvBack=findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
