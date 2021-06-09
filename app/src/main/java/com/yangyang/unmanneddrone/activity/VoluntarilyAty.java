package com.yangyang.unmanneddrone.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yangyang.tools.db.SQLiteHelper;
import com.yangyang.tools.permission.OnPermission;
import com.yangyang.tools.permission.Permission;
import com.yangyang.tools.permission.XXPermissions;
import com.yangyang.unmanneddrone.adapter.VoluntarilyAdapter;
import com.yangyang.unmanneddrone.body.LocationMsgBody;
import com.yangyang.unmanneddrone.body.VoluntarilyBody;
import com.yangyang.unmanneddrone.R;
import com.yangyang.unmanneddrone.base.MyActivity;
import com.yangyang.unmanneddrone.helper.DoubleClickHelper;

import java.util.ArrayList;
import java.util.List;

import static com.yangyang.unmanneddrone.R.string.success_remove;

//航线测量
public class VoluntarilyAty extends MyActivity implements View.OnClickListener {
    private VoluntarilyAdapter mVoluntarilyAdapter;
    private ImageView mIvBack;
    private List<VoluntarilyBody> mCurrentList;

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.aty_voluntarily);
        initView();
        initData();
    }

    private void initView() {
        mIvBack = findViewById(R.id.iv_back);
        RecyclerView mRecyclerView = findViewById(R.id.rlv);
        mVoluntarilyAdapter = new VoluntarilyAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mVoluntarilyAdapter);
        mIvBack.setOnClickListener(this);

    }


    private void initData() {
        // init click listener
        mVoluntarilyAdapter.setListener(new VoluntarilyAdapter.Listener() {
            @Override
            public void CreateNewLine(int position) {
                startActivity(new Intent(VoluntarilyAty.this, CreateAty.class));

            }

            @Override
            public void itemLongPress(int position) {
                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(VoluntarilyAty.this);
                normalDialog.setMessage(R.string.check_remove);
                normalDialog.setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SQLiteHelper.with(VoluntarilyAty.this).delete(LocationMsgBody.class,
                                        " id = " + mVoluntarilyAdapter.getList().get(position).getId(), null);
                                mVoluntarilyAdapter.removeAtIndex(position);
                                Toast.makeText(VoluntarilyAty.this, success_remove, Toast.LENGTH_SHORT).show();
                            }
                        });
                normalDialog.setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                normalDialog.show();

            }

            /**
             * item 的点击事件
             * @param position
             */
            @Override
            public void itemOnClick(int position) {
                //
                Intent intent_item = new Intent(VoluntarilyAty.this, CreateAty.class);
                intent_item.putExtra("transId", mVoluntarilyAdapter.getList().get(position).getId());
                startActivity(intent_item);
            }
        });
    }

    @Override
    public void onClick(View v) {
        //屏蔽短时间内双击
        if (DoubleClickHelper.isOnDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        // 查询数据库所保存的航线数据
        mCurrentList = new ArrayList<>();
        mCurrentList.add(new VoluntarilyBody());
        List<LocationMsgBody> locationMsgBodyList = SQLiteHelper.with(this).query(LocationMsgBody.class);
        for (LocationMsgBody body : locationMsgBodyList) {
            VoluntarilyBody voluntarilyBody = new VoluntarilyBody();
            voluntarilyBody.setId(body.getId());
            voluntarilyBody.setTitle(body.getRouteName());
            voluntarilyBody.setUpdateTime(body.getCreateTime());
            voluntarilyBody.setMap(body.getThumbnailPath());
            voluntarilyBody.setLocation(body.getLocation());
            mCurrentList.add(voluntarilyBody);
        }
        mVoluntarilyAdapter.setData(mCurrentList);
    }

}
