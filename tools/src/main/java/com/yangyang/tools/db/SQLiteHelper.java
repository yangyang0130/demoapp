package com.yangyang.tools.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SQLiteHelper implements OnSQLiteOpenHelperListener {

    private final int DATABASE_VERSION = 1;
    private final String DATABASE_NAME = "AndroidSQLite.db";
    protected final String CREATE_TABLE_HEAD = "CREATE TABLE IF NOT EXISTS ";
    protected final String CREATE_PRIMARY_KEY = "SYS_ID INTEGER PRIMARY KEY AUTOINCREMENT,";

    private Context context;
    private int version;
    private String databaseName;
    private SQLiteOpenHelper sqLiteOpenHelper;
    private SQLiteDatabase db;
    private static SQLiteHelper sqLiteHelper;
    private OnSQLiteHelperListener listener;

    /**
     * 基础的数据库构造方法<br/>
     * This Construction method have default database name
     * and default database version . If you want to defined
     * database name and database version ,please chose other
     * construction.
     *
     * @param context 上下文
     */
    public SQLiteHelper(Context context) {
        this.context = context;
        databaseName = DATABASE_NAME;
        version = DATABASE_VERSION;
        if (sqLiteOpenHelper == null) {
            sqLiteOpenHelper = new SQLiteOpenHelper(context, databaseName, null, version, this);
            db = sqLiteOpenHelper.getWritableDatabase();
        }
        onCreate(db);
        if (new File(db.getPath()).exists()) {
            onCreate(db);
        }
    }

    /**
     * 自定义数据库名称及路劲和版本的构造方法
     * This Construction method you can defined
     * database name and database version.
     *
     * @param context
     * @param databaseVersion you should upgrade you database when version change
     */
    public SQLiteHelper(Context context, int databaseVersion) {
        this.context = context;
        databaseName = DATABASE_NAME;
        if (sqLiteOpenHelper == null) {
            sqLiteOpenHelper = new SQLiteOpenHelper(context, databaseName, null, databaseVersion, this);
            db = sqLiteOpenHelper.getWritableDatabase();
        }
        onCreate(db);
        if (new File(db.getPath()).exists()) {
            onCreate(db);
        }
    }

    /**
     * 自定义数据库名称及路劲和版本的构造方法
     * This Construction method you can defined
     * database name and database version.
     *
     * @param context
     * @param databaseName if you want to defined you database path
     *                     you should add a path before of database name.
     * @param version      you should upgrade you database when version change
     */
    public SQLiteHelper(Context context, String databaseName, int version) {
        this.context = context;
        if (sqLiteOpenHelper == null) {
            sqLiteOpenHelper = new SQLiteOpenHelper(context, databaseName, null, version, this);
            db = sqLiteOpenHelper.getWritableDatabase();
        }
        onCreate(db);
        if (new File(db.getPath()).exists()) {
            onCreate(db);
        }
    }


    /**
     * 获取数据库对象
     *
     * @param context 上下文对象
     * @return
     */
    public static SQLiteHelper with(Context context) {
        if (sqLiteHelper == null) {
            synchronized (SQLiteHelper.class) {
                if (sqLiteHelper == null) {
                    sqLiteHelper = new SQLiteHelper(context);
                }
            }
        }
        return sqLiteHelper;
    }

    /**
     * 获取数据库助手
     *
     * @param context      上下文
     * @param databaseName 数据库名称
     * @param version      数据库版本号
     * @return
     */
    public static SQLiteHelper with(Context context, String databaseName, int version) {
        if (sqLiteHelper == null) {
            synchronized (SQLiteHelper.class) {
                if (sqLiteHelper == null) {
                    sqLiteHelper = new SQLiteHelper(context, databaseName, version);
                }
            }
        }
        return sqLiteHelper;
    }

    /**
     * 获取数据库助手
     *
     * @param context         上下文
     * @param databaseVersion 数据库版本号
     * @return
     */
    public static SQLiteHelper with(Context context, int databaseVersion) {
        if (sqLiteHelper == null) {
            synchronized (SQLiteHelper.class) {
                if (sqLiteHelper == null) {
                    sqLiteHelper = new SQLiteHelper(context, databaseVersion);
                }
            }
        }
        return sqLiteHelper;
    }

    /**
     * 设置数据库助手监听
     *
     * @param listener
     */
    public void setOnSQLiteHelperListener(OnSQLiteHelperListener listener) {
        this.listener = listener;
    }

    /**
     * 创建数据表
     * create table
     * for example: create table if not exists user (_id integer primary key autoincrement,user_name text,user_sex text,user_pwd text)
     * then db.execSQL(TABLE_USER_SQL)
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        if (listener != null) {
            listener.onCreate(db);
        }
    }

    /**
     * 升级数据库
     * update grade database
     * [newVersion > oldVersion] for example drop table user
     *
     * @param db         open or write to database object
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (listener != null) {
            listener.onUpgrade(db, version, newVersion);
        }
    }

    /**
     * 建表
     * Create the SQL statement for the table.
     *
     * @param table   表名
     * @param columns 列名
     * @return
     */
    public void createTable(String table, String[] columns) {
        if (TextUtils.isEmpty(table)) {
            return;
        }
        if (columns == null) {
            return;
        }
        if (columns.length == 0) {
            return;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(CREATE_TABLE_HEAD + table);
        sb.append(" (");
        sb.append(CREATE_PRIMARY_KEY);
        for (String key : columns) {
            sb.append(key + " text");
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        Log.e(this.getClass().getSimpleName(), "createTable:" + sb.toString());
        db.execSQL(sb.toString());
    }

    /**
     * 创建表
     *
     * @param cls 类名
     */
    public void createTable(Class<?> cls) {
        StringBuffer sb = new StringBuffer();
        sb.append(CREATE_TABLE_HEAD + cls.getSimpleName());
        sb.append(" (");
        sb.append(CREATE_PRIMARY_KEY);
        Field[] fields = cls.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            String name = field.getName();
            if (!name.startsWith("$") && !name.equals("serialVersionUID")) {
                sb.append(name + " text");
                sb.append(",");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        db.execSQL(sb.toString());
    }

    /**
     * 插入数据
     * insert you want what information.
     *
     * @param table
     * @param contentValues
     * @return
     */
    public long insert(String table, ContentValues contentValues) {
        long result = -1;
        db.beginTransaction();
        try {
            result = db.insert(table, null, contentValues);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.i(this.getClass().getSimpleName(), this.getClass().getSimpleName() + " insert Exception" + e.toString());
        }
        db.endTransaction();
        return result;
    }

    /**
     * 插入对象数据
     * @param obj
     * @return 插入数据
     */
    public long insert(Object obj) {
        Class<?> cls = obj.getClass();
        Field[] fields = cls.getDeclaredFields();
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            String name = field.getName();
            String value = "";
            try {
                value = String.valueOf(field.get(obj));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (!name.equals("$change") && !name.equals("serialVersionUID")) {
                contentValues.put(name, value);
            }
        }
        return insert(cls.getSimpleName(), contentValues);
    }


    /**
     * 插入数据
     * insert you want what information.
     *
     * @param sql sql语句
     * @return
     */
    public void insert(String sql) {
        execSQL(sql);
    }

    /**
     * 删除数据
     * delete from database what you want to do anything.
     *
     * @param table
     * @param whereClause for example "name = ?"
     * @param whereArgs   for example new String[]{"Mary"}
     * @return 删除的条数
     */
    public long delete(String table, String whereClause, String[] whereArgs) {
        long result = -1;
        db.beginTransaction();
        try {
            result = db.delete(table, whereClause, whereArgs);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.i(this.getClass().getSimpleName(), this.getClass().getSimpleName() + " delete Exception" + e.toString());
        }
        db.endTransaction();
        return result;
    }

    /**
     * 删除数据
     * delete from database what you want to do anything.
     *
     * @param table
     * @param whereClause for example "name = ?"
     * @param whereArgs   for example new String[]{"Mary"}
     * @return 删除的条数
     */
    public long delete(Class table, String whereClause, String[] whereArgs) {
        long result = -1;
        db.beginTransaction();
        try {
            result = db.delete(table.getSimpleName(), whereClause, whereArgs);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.i(this.getClass().getSimpleName(), this.getClass().getSimpleName() + " delete Exception" + e.toString());
        }
        db.endTransaction();
        return result;
    }

    /**
     * 删除数据
     * delete from database what you want to do anything.
     *
     * @param sql sql语句
     * @return
     */
    public void delete(String sql) {
        execSQL(sql);
    }

    /**
     * 更新数据
     * update from your table in database.
     *
     * @param table
     * @param contentValues you is gonging to update values
     * @param whereClause   for example "name = ?"
     * @param whereArgs     for example new String[]{"Mary"}
     * @return
     */
    public long update(String table, ContentValues contentValues, String whereClause, String[] whereArgs) {
        long result = -1;
        db.beginTransaction();
        try {
            result = db.update(table, contentValues, whereClause, whereArgs);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.i(this.getClass().getSimpleName(), this.getClass().getSimpleName() + " update Exception" + e.toString());
        }
        db.endTransaction();
        return result;
    }

    /**
     * 更新数据
     * update from your table in database.
     *
     * @param obj         data object
     * @param whereClause for example "name = ?"
     * @param whereArgs   for example new String[]{"Mary"}
     * @return
     */
    public long update(Object obj, String whereClause, String[] whereArgs) {
        long result = -1;
        db.beginTransaction();
        ContentValues contentValues = new ContentValues();
        Class cls = obj.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (field != null) {
                field.setAccessible(true);
                String name = field.getName();
                String value = "";
                try {
                    value = String.valueOf(field.get(obj));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (!name.equals("$change") && !name.equals("serialVersionUID")) {
                    contentValues.put(name, value);
                }
            }
        }
        try {
            result = db.update(obj.getClass().getSimpleName(), contentValues, whereClause, whereArgs);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.i(this.getClass().getSimpleName(), this.getClass().getSimpleName() + " update Exception" + e.toString());
        }
        db.endTransaction();
        return result;
    }

    /**
     * 更新数据
     * update from your table in database.
     * for example update user set user_name = 'Jerry' where user_name = 'Mary'
     *
     * @param sql
     * @return
     */
    public void update(String sql) {
        execSQL(sql);
    }

    /**
     * 查询数据
     * query from databases and find you what you are gong to finding.
     *
     * @param sql 数据库语句
     * @return
     */
    public List<Map<String, String>> query(String sql) {
        Cursor cursor = db.rawQuery(sql, null);
        String[] columnNames = cursor.getColumnNames();
        List<Map<String, String>> queryList = new ArrayList<Map<String, String>>();
        while (cursor.moveToNext()) {
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < columnNames.length; i++) {
                map.put(columnNames[i], cursor.getString(cursor.getColumnIndex(columnNames[i])));
            }
            queryList.add(map);
        }
        cursor.close();
        return queryList;
    }

    /**
     * 查询数据
     *
     * @param cls 实体类
     * @param <T> 实体类泛型
     * @return
     */
    public <T> List<T> query(Class<T> cls) {
        return query(cls, "select * from " + cls.getSimpleName());
    }

    /**
     * 查询数据
     *
     * @param cls 实体类
     * @param sql sql语句
     * @param <T> 实体类泛型
     * @return 实体列表
     */
    public <T> List<T> query(Class<T> cls, String sql) {
        Cursor cursor = db.rawQuery(sql, null);
        String[] columnNames = cursor.getColumnNames();
        List<T> queryList = new ArrayList<T>();
        while (cursor.moveToNext()) {
            T bean = null;
            try {
                bean = cls.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < columnNames.length; i++) {
                Field field = null;
                try {
                    field = cls.getDeclaredField(columnNames[i]);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
                if (field != null) {
                    field.setAccessible(true);
                    try {
                        field.set(bean, cursor.getString(cursor.getColumnIndex(columnNames[i])));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            queryList.add(bean);
        }
        cursor.close();
        return queryList;
    }

    /**
     * 执行SQL语句
     * CMD SQL
     *
     * @param sql sql语句
     */
    public void execSQL(String sql) {
        Log.e(this.getClass().getSimpleName(), this.getClass().getSimpleName() + " execSQL sql:" + sql);
        db.beginTransaction();
        try {
            db.execSQL(sql);
            //Notice:if you beginTransaction() method and endTransaction()
            // method you must use this method.if you don't while your data insert、update、delete fail.
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), this.getClass().getSimpleName() + " execSQL Exception:" + e.toString());
        }
        db.endTransaction();
    }

    /**
     * 删除表
     * drop table
     *
     * @param table 数据表
     */
    public void dropTable(String table) {
        db.beginTransaction();
        String sql = "drop table if exists " + table;
        try {
            db.execSQL(sql);
            Log.e(this.getClass().getSimpleName(), this.getClass().getSimpleName() + " dropTable sql:" + sql);
            db.setTransactionSuccessful();//Notice:if you beginTransaction() method and endTransaction() method you must use this method.if you don't while your data insert、update、delete fail.
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), this.getClass().getSimpleName() + " execSQL Exception:" + e.toString());
        }
        db.endTransaction();
    }

    /**
     * 删除表中的数据
     *
     * @param table
     */
    public void deleteTable(Class table) {
        deleteTable(table.getSimpleName());
    }

    /**
     * 清除表中数据
     * truncate table
     *
     * @param table 数据表
     */
    public void deleteTable(String table) {
        db.beginTransaction();
        //除去表内的数据，但并不删除表本身
        String sql = "delete from " + table;
        try {
            db.execSQL(sql);
            Log.e(this.getClass().getSimpleName(), this.getClass().getSimpleName() + " dropTable sql:" + sql);
            db.setTransactionSuccessful();//Notice:if you beginTransaction() method and endTransaction() method you must use this method.if you don't while your data insert、update、delete fail.
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), this.getClass().getSimpleName() + " execSQL Exception:" + e.toString());
        }
        db.endTransaction();
    }

    /**
     * 删除数据库
     * delete database
     */
    public void dropDatabase() {
        context.deleteDatabase(sqLiteOpenHelper.getDatabaseName());
        Log.e(this.getClass().getSimpleName(), this.getClass().getSimpleName() + " dropDatabase databaseName:" + sqLiteOpenHelper.getDatabaseName());
    }

}
