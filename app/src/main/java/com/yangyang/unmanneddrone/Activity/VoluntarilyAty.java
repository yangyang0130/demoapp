package com.yangyang.unmanneddrone.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yangyang.tools.permission.OnPermission;
import com.yangyang.tools.permission.Permission;
import com.yangyang.tools.permission.XXPermissions;
import com.yangyang.unmanneddrone.Adapter.VoluntarilyAdapter;
import com.yangyang.unmanneddrone.Body.VoluntarilyBody;
import com.yangyang.unmanneddrone.R;
import com.yangyang.unmanneddrone.base.MyActivity;

import java.util.ArrayList;
import java.util.List;

//航线测量
public class VoluntarilyAty extends MyActivity {
    private VoluntarilyAdapter voluntarilyAdapter;

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.aty_voluntarily);
        initView();
        initData();
    }

    private void initView() {
        RecyclerView mRecyclerView = findViewById(R.id.rlv);
        voluntarilyAdapter = new VoluntarilyAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(voluntarilyAdapter);
    }

    private void initData() {
        // setData
        voluntarilyAdapter.setData(getData());
        // init click listener
        voluntarilyAdapter.setListener(position -> {
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
        });
    }

    private List<VoluntarilyBody> getData() {
        List<VoluntarilyBody> list = new ArrayList<>();
        list.add(new VoluntarilyBody());
        for (int i = 0; i < 10; i++) {
            VoluntarilyBody voluntarilyBody = new VoluntarilyBody();
            voluntarilyBody.setTitle("嘉陵江飞行");
            voluntarilyBody.setUpdate_time("3/19/2021");
            voluntarilyBody.setMap(R.drawable.map);
            list.add(voluntarilyBody);
        }
        return list;
    }
}
