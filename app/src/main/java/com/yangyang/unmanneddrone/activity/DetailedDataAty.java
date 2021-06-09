package com.yangyang.unmanneddrone.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.yangyang.tools.db.SQLiteHelper;
import com.yangyang.unmanneddrone.body.LocationMsgBody;
import com.yangyang.unmanneddrone.R;
import com.yangyang.unmanneddrone.base.MyActivity;
import com.yangyang.unmanneddrone.helper.ImageHelper;

import java.util.List;
//测量记录详情
public class DetailedDataAty extends MyActivity {
    private ImageView mIvMap;
    private ImageButton mIbBack;

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.aty_detaileddata);
        mIbBack=findViewById(R.id.ib_back);
        mIbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mIvMap =findViewById(R.id.iv_map);
        List<LocationMsgBody> locationMsgBodyList = SQLiteHelper.with(this).query(LocationMsgBody.class);

        if (testEmpty(locationMsgBodyList) == 0) {
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.ic_no_context)
                    .into(mIvMap);
            return;
        }

        if (!TextUtils.isEmpty(locationMsgBodyList.get(0).getThumbnailPath())){
            Glide.with(this)
                    .asBitmap()
                    .error(R.drawable.ic_no_context)
                    .load(ImageHelper.base64ToBitmap(locationMsgBodyList.get(0).getThumbnailPath()))
                    .into(mIvMap);
        }
        // ivMap.setImageResource(locationMsgBodyList);

    }

    private int testEmpty(List<LocationMsgBody> locationMsgBodyList) {
        return locationMsgBodyList != null ? locationMsgBodyList.size() : 0;
    }
}
