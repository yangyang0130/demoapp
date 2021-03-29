package com.yangyang.unmanneddrone.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.yangyang.tools.db.SQLiteHelper;
import com.yangyang.unmanneddrone.Body.LocationMsgBody;
import com.yangyang.unmanneddrone.R;
import com.yangyang.unmanneddrone.base.MyActivity;

import java.util.List;

public class DetailedDataAty extends MyActivity {
    private ImageView ivMap;

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.aty_detaileddata);
        ivMap=findViewById(R.id.iv_map);
        List<LocationMsgBody> locationMsgBodyList = SQLiteHelper.with(this).query(LocationMsgBody.class);
        if (TextUtils.isEmpty((CharSequence) locationMsgBodyList.get(0))){
            Glide.with(this)
                    .asBitmap()
                    .load(locationMsgBodyList.get(0))
                    .into(ivMap);
        }
        // ivMap.setImageResource(locationMsgBodyList);

    }
}
