package com.yangyang.unmanneddrone.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.yangyang.unmanneddrone.activity.AboutAty;
import com.yangyang.unmanneddrone.activity.MeasurementRecordAty;
import com.yangyang.unmanneddrone.R;
import com.yangyang.unmanneddrone.helper.DoubleClickHelper;

//顶部弹窗
public class TopPopWindow extends PopupWindow implements View.OnClickListener {
    private Context mContext;
    private View mLlLocation, mLlAbout, mLlSetting;

    public TopPopWindow(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.confirm_dialog, null);
        mLlLocation = view.findViewById(R.id.ll_location);
        mLlAbout = view.findViewById(R.id.ll_about);
        mLlSetting = view.findViewById(R.id.ll_setting);
        mLlLocation.setOnClickListener(this);
        mLlAbout.setOnClickListener(this);
        mLlSetting.setOnClickListener(this);
        setContentView(view);
        initWindow();
    }

    private void initWindow() {
        DisplayMetrics d = mContext.getResources().getDisplayMetrics();
        this.setWidth((int) (d.widthPixels * 0.4));
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
//        backgroundAlpha((Activity) mContext, 0.8f);//0.0-1.0
//        this.setOnDismissListener(new OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                backgroundAlpha((Activity) mContext, 1f);
//            }
//        });
    }
    public void showAtBottom(View view) {
        //弹窗位置设置
        showAsDropDown(view, Math.abs((view.getWidth() - getWidth()) / 2), 40);
        //showAtLocation(view, Gravity.TOP | Gravity.RIGHT, 10, 110);//有偏差
    }

    @Override
    public void onClick(View view) {
        //屏蔽短时间内双击
        if (DoubleClickHelper.isOnDoubleClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.ll_about:
                // 关于
                Intent intent_about = new Intent(mContext, AboutAty.class);
                mContext.startActivity(intent_about);
                this.dismiss();
                break;
            case R.id.ll_location:
                // 测量
                Intent intent_data = new Intent(mContext, MeasurementRecordAty.class);
                mContext.startActivity(intent_data);
                this.dismiss();
                break;
            default:
                break;
        }
    }

}

