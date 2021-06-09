package com.yangyang.unmanneddrone.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;
/**
 * toast提示工具类
 */

public class ToastHelper {

    private static Toast mToast = null;

    public ToastHelper() {
    }

    private static class SingletonHolder {
        private static final ToastHelper INSTANCE = new ToastHelper();
    }

    public static ToastHelper getInstance() {
        return ToastHelper.SingletonHolder.INSTANCE;
    }

    /**
     * 弹出Toast
     * @param context  上下文对象
     * @param text     提示的文本
     * @param duration 持续时间（0：短；1：长）
     */
    @SuppressLint("ShowToast")
    public void showToast(Context context, String text, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, duration);
        } else {
            mToast.setText(text);
            mToast.setDuration(duration);
        }
        mToast.setGravity(Gravity.BOTTOM, 0,50);
        mToast.show();
    }

    /**
     * 弹出Toast
     * @param context  上下文对象
     * @param text     提示的文本
     * @param duration 持续时间（0：短；1：长）
     * @param gravity  位置（Gravity.CENTER;Gravity.TOP;...）
     */
    @SuppressLint("ShowToast")
    public void showToast(Context context, String text, int duration, int gravity) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, duration);
        } else {
            mToast.setText(text);
            mToast.setDuration(duration);
        }
        mToast.setGravity(gravity, 0, 0);
        mToast.show();
    }

    /**
     * 关闭Toast
     */
    public void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}
