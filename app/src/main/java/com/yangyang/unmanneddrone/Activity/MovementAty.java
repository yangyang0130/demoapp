package com.yangyang.unmanneddrone.Activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.yangyang.unmanneddrone.R;
import com.yangyang.unmanneddrone.base.MyActivity;

//手动测量
public class MovementAty extends MyActivity {
    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.aty_movement);
    }
}
