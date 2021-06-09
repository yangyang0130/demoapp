package com.yangyang.unmanneddrone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yangyang.unmanneddrone.R;
import com.yangyang.unmanneddrone.base.MyActivity;
//初始化加载界面
public class InitializeAty extends MyActivity {

    private ProgressBar mProgressBar;
    private Button mButton;
    private TextView mTextView;
    private int index = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_initialize);
        onInView();
    }

    private void onInView() {
        mTextView = (TextView) findViewById(R.id.tv_schedule);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_progressbar);
        mButton = (Button) findViewById(R.id.button);

        Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                mProgressBar.setProgress(msg.what);
                mTextView.setText(getString(R.string.loading) + String.valueOf(msg.what) + "%");
                if (msg.what == 100) {
                    Intent intent = new Intent(InitializeAty.this, MainAty.class);
                    startActivity(intent);
                    InitializeAty.this.finish();
                }
            }
        };
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (index <= 100) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(index);
                    index++;
                }
            }
        };
        thread.start();

    }
}
