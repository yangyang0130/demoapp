package com.yangyang.unmanneddrone.base;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yangyang.unmanneddrone.helper.ClickAction;

public class MyActivity extends AppCompatActivity implements ClickAction {

    public String TAG = this.getClass().getSimpleName();

    private Context mContext;

    /**
     * 加载对话框
     */
    private Dialog mDialog;
    /**
     * 对话框数量
     */
    private int mDialogTotal;

    /**
     * 当前加载对话框是否在显示中
     */
    public boolean isShowDialog() {
        return mDialog != null && mDialog.isShowing();
    }

    /**
     * 显示加载对话框
     */
    public void showDialog() {
        mDialogTotal++;
//        postDelayed(() -> {
//            if (mDialogTotal > 0 && !isFinishing()) {
//                if (mDialog == null) {
//                    mDialog = new WaitDialog.Builder(this)
//                            .setCancelable(false)
//                            .create();
//                }
//                if (!mDialog.isShowing()) {
//                    mDialog.show();
//                }
//            }
//        }, 300);
    }

    /**
     * 隐藏加载对话框
     */
    public void hideDialog() {
        if (mDialogTotal > 0) {
            mDialogTotal--;
        }

        if (mDialogTotal == 0 && mDialog != null && mDialog.isShowing() && !isFinishing()) {
            mDialog.dismiss();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        // 强制竖屏
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 强制横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏

        /*
        1, 点击toast
        2, 判断当前toast的个数, 以及显示所依赖的Activity
         */
    }

    public Context getContext() {
        return getBaseContext();
    }

    /**
     * 状态栏字体深色模式
     */
    protected boolean isStatusBarDarkFont() {
        return true;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        // overridePendingTransition(R.anim.right_in_activity, R.anim.right_out_activity);
    }

    @Override
    public void finish() {
        super.finish();
        // overridePendingTransition(R.anim.left_in_activity, R.anim.left_out_activity);
    }

    @Override
    protected void onDestroy() {
        if (isShowDialog()) {
            hideDialog();
        }
        mDialog = null;
        super.onDestroy();
    }
}
