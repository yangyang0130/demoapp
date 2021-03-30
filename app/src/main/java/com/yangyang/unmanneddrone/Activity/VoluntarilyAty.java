package com.yangyang.unmanneddrone.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
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
import com.yangyang.unmanneddrone.Adapter.VoluntarilyAdapter;
import com.yangyang.unmanneddrone.Body.LocationMsgBody;
import com.yangyang.unmanneddrone.Body.VoluntarilyBody;
import com.yangyang.unmanneddrone.R;
import com.yangyang.unmanneddrone.base.MyActivity;
import com.yangyang.unmanneddrone.helper.DoubleClickHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//航线测量
public class VoluntarilyAty extends MyActivity implements View.OnClickListener {
    private VoluntarilyAdapter voluntarilyAdapter;
    private ImageView iv_back;
    private List<VoluntarilyBody> currentList;

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.aty_voluntarily);
        initView();
        initData();
    }

    private void initView() {
        iv_back = findViewById(R.id.iv_back);
        RecyclerView mRecyclerView = findViewById(R.id.rlv);
        voluntarilyAdapter = new VoluntarilyAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(voluntarilyAdapter);
        iv_back.setOnClickListener(this);
//        voluntarilyAdapter.setLongClickLisenter(new VoluntarilyAdapter.LongClickLisenter() {
//            @Override
//            public void LongClickLisenter(int position) {
//                voluntarilyAdapter.del(position);
//                Toast.makeText(VoluntarilyAty.this, "删除成功", Toast.LENGTH_SHORT).show();
//            }
//        });

    }


    private void initData() {
        // init click listener
        voluntarilyAdapter.setListener(new VoluntarilyAdapter.Listener() {
            @Override
            public void CreateNewLine(int position) {
                // 判断权限
                XXPermissions.with(VoluntarilyAty.this)
                        .permission(Permission.Group.LOCATION)
                        .request(new OnPermission() {
                            @Override
                            public void hasPermission(List<String> granted, boolean all) {
                                if (all) {
                                    startActivity(new Intent(VoluntarilyAty.this, CreateAty.class));
                                } else {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                }
                            }

                            @Override
                            public void noPermission(List<String> denied, boolean never) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                            }
                        });
            }

            @Override
            public void itemLongPress(int position) {
                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(VoluntarilyAty.this);
                normalDialog.setMessage("请确认是否删除该条数据?");
                normalDialog.setPositiveButton("确认",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SQLiteHelper.with(VoluntarilyAty.this).delete(LocationMsgBody.class,
                                        " id = " + voluntarilyAdapter.getList().get(position).getId(), null);
                                voluntarilyAdapter.removeAtIndex(position);
                                Toast.makeText(VoluntarilyAty.this, "删除成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                normalDialog.setNegativeButton("取消",
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
                intent_item.putExtra("transId", voluntarilyAdapter.getList().get(position).getId());
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
        currentList = new ArrayList<>();
        currentList.add(new VoluntarilyBody());
        List<LocationMsgBody> locationMsgBodyList = SQLiteHelper.with(this).query(LocationMsgBody.class);
        for (LocationMsgBody body : locationMsgBodyList) {
            VoluntarilyBody voluntarilyBody = new VoluntarilyBody();
            voluntarilyBody.setId(body.getId());
            voluntarilyBody.setTitle(body.getRouteName());
            voluntarilyBody.setUpdate_time(body.getCreateTime());
            voluntarilyBody.setMap(body.getThumbnail_path());
            voluntarilyBody.setLocation(body.getLocation());
            currentList.add(voluntarilyBody);
        }
        voluntarilyAdapter.setData(currentList);
    }

}
