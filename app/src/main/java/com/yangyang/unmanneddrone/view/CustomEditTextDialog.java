package com.yangyang.unmanneddrone.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.yangyang.unmanneddrone.R;

public class CustomEditTextDialog extends Dialog {
    private final Context mContext;
    private TextView mBtnSure;
    private TextView mBtnCancel;
    private TextView mTitle;
    private EditText mEditText;

    public CustomEditTextDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    //初始化
    public void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_input, null);
        mTitle = view.findViewById(R.id.title);
        mEditText = view.findViewById(R.id.edittext);
        mBtnSure = view.findViewById(R.id.dialog_confirm_sure);
        mBtnCancel = view.findViewById(R.id.dialog_confirm_cancle);
        super.setContentView(view);
    }


    public CustomEditTextDialog setTile(String s) {
        mTitle.setText(s);
        return this;
    }

    //获取当前输入框对象
    public View getmEditText() {
        return mEditText;
    }

    //确定键监听器
    public void setOnSureListener(View.OnClickListener listener) {
        mBtnSure.setOnClickListener(listener);
    }

    //取消键监听器
    public void setOnCanlceListener(View.OnClickListener listener) {
        mBtnCancel.setOnClickListener(listener);
    }
}
