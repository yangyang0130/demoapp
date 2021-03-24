package com.yangyang.tools.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public final class SQLiteOpenHelper extends android.database.sqlite.SQLiteOpenHelper {

    private OnSQLiteOpenHelperListener helperListener;

    public SQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, OnSQLiteOpenHelperListener helperListener) {
        super(context, name, factory, version);
        this.helperListener = helperListener;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (helperListener != null) {
            helperListener.onCreate(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (helperListener != null) {
            helperListener.onUpgrade(db, oldVersion, newVersion);
        }
    }
}
