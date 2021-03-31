package com.yangyang.unmanneddrone.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yangyang.unmanneddrone.R;
import com.yangyang.unmanneddrone.View.TopPopWindow;
import com.yangyang.unmanneddrone.base.MyActivity;
import com.yangyang.unmanneddrone.helper.DoubleClickHelper;

//首页
public class MainAty extends MyActivity implements View.OnClickListener {
    private ImageView mImageView_more;
    private ImageButton mImageButton_hand_movement;
    private ImageButton mImageButton_voluntarily;
    private ImageView mImageView_status;
    private TextView mTextView_status;
    private TopPopWindow popWindow;


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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onClick(View v) {
        //屏蔽短时间内双击
        if (DoubleClickHelper.isOnDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.ib_hand_movement:
                // 手动测量
                Intent intent_movement = new Intent(MainAty.this, MovementAty.class);
                startActivity(intent_movement);
                break;
            case R.id.ib_voluntarily:
                // 航线测量
                Intent intent_voluntarily = new Intent(MainAty.this, VoluntarilyAty.class);
                startActivity(intent_voluntarily);
                break;
            case R.id.iv_more:
                // 关于
                popWindow = new TopPopWindow(MainAty.this);
                popWindow.showAtBottom(mImageView_more);
                showIcon();
                popWindow.setOnDismissListener(this::showIcon);
                popWindow.setTouchInterceptor((v1, event) -> {
                    if (event.getY() >= 0) {//PopupWindow内部的事件
                        return false;
                    }
                    // 点击外部,弹窗消失
                    popWindow.dismiss();
                    return true;
                });
                break;
            default:
                break;
        }
    }

    private void showIcon() {
        backgroundAlpha((popWindow.isShowing() ? 0.5f : 1.0f));
        Glide.with(MainAty.this)
                .asBitmap()
                .load(popWindow.isShowing() ? R.drawable.ic_cancel : R.drawable.ic_more)
                .into(mImageView_more);
    }

    //设置添加屏幕的背景透明度
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }
}