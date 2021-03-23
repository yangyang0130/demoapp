package com.yangyang.unmanneddrone.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yangyang.unmanneddrone.R;
import com.yangyang.unmanneddrone.View.TopPopWindow;
import com.yangyang.unmanneddrone.base.MyActivity;

import java.util.PropertyResourceBundle;

//首页
public class MainAty extends MyActivity implements View.OnClickListener {
    private ImageView mImageView_more;
    private ImageButton mImageButton_hand_movement;
    private ImageButton mImageButton_voluntarily;
    private ImageView mImageView_status;
    private TextView mTextView_status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_main);
        OnInView();
        OnClickListener();
    }


    private void OnInView() {
        mImageButton_hand_movement = findViewById(R.id.ib_hand_movement);
        mImageButton_voluntarily = findViewById(R.id.ib_voluntarily);
        mImageView_more = findViewById(R.id.iv_more);
        mImageView_status = findViewById(R.id.iv_status);
        mTextView_status = findViewById(R.id.tv_status);

    }

    private void OnClickListener() {
        mImageView_status.setOnClickListener(this);
        mImageButton_voluntarily.setOnClickListener(this);
        mImageButton_hand_movement.setOnClickListener(this);
        mImageView_more.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_hand_movement:
                Intent intent_movement = new Intent(MainAty.this, MovementAty.class);
                startActivity(intent_movement);
                break;
            case R.id.ib_voluntarily:
                Intent intent_voluntarily = new Intent(MainAty.this, VoluntarilyAty.class);
                startActivity(intent_voluntarily);

                break;
            case R.id.iv_more:
                new TopPopWindow(MainAty.this).showAtBottom(mImageView_more);
                break;

            default:
                break;

        }

    }
}