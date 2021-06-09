package com.yangyang.unmanneddrone.helper;

import android.view.View;

import androidx.annotation.IdRes;

public interface ClickAction extends View.OnClickListener{

    <V extends View> V findViewById(@IdRes int id);

    @Override
    default void onClick(View v){

    }

    default void setOnClickListener(@IdRes int... ids){
        for (int id : ids) {
            findViewById(id).setOnClickListener(this);
        }
    }
}
