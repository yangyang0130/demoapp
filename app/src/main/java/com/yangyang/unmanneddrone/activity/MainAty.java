package com.yangyang.unmanneddrone.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import com.bumptech.glide.Glide;
import com.yangyang.unmanneddrone.R;
import com.yangyang.unmanneddrone.view.TopPopWindow;
import com.yangyang.unmanneddrone.base.MyActivity;
import com.yangyang.unmanneddrone.helper.DoubleClickHelper;

import static com.yangyang.unmanneddrone.R.string.exit_again;

//首页
public class MainAty extends MyActivity implements View.OnClickListener {
    private ImageView mImageViewMore;
    private ImageButton mImageButtonHandMovement;
    private ImageButton mImageButtonVoluntarily;
    private ImageView mImageView_status;
    private TextView mTextViewStatus;
    private TopPopWindow popWindow;
    private long mExitTime;       //实现“再按一次退出”的记录时间变量


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_main);
        OnInView();
        OnClickListener();

    }


    private void OnInView() {
        mImageButtonHandMovement = findViewById(R.id.ib_hand_movement);
        mImageButtonVoluntarily = findViewById(R.id.ib_voluntarily);
        mImageViewMore = findViewById(R.id.iv_more);
        mImageView_status = findViewById(R.id.iv_status);
        mTextViewStatus = findViewById(R.id.tv_status);
    }

    private void OnClickListener() {
        mImageView_status.setOnClickListener(this);
        mImageButtonVoluntarily.setOnClickListener(this);
        mImageButtonHandMovement.setOnClickListener(this);
        mImageViewMore.setOnClickListener(this);

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
                popWindow.showAtBottom(mImageViewMore);
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
                .into(mImageViewMore);
    }

    //设置添加屏幕的背景透明度
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断用户是否点击了“返回键”
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //与上次点击返回键时刻作差
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //大于2000ms则认为是误操作，使用Toast进行提示
                Toast.makeText(this, exit_again, Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                //小于2000ms则认为是用户确实希望退出程序-调用System.exit()方法进行退出
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}