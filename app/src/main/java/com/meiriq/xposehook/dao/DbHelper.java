package com.meiriq.xposehook.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tian on 15-11-27.
 */
public class DbHelper extends SQLiteOpenHelper{

    /**
     * 数据库名称
     */
    public static final String DB_NAME = "mrq_xpose.db";
    /**
     * 数据库版本信息
     */
    public static final int DB_VERSION = 1;

    public static final String TABLE_WHITE_UNINSTALL = "whiteuninstall";

    public static final String TABLE_CLEAR_DATA = "cleardata";

    public static final String TABLE_WHITE_APK = "whiteapk";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_WHITE_UNINSTALL_TABLE = "CREATE TABLE "
                + TABLE_WHITE_UNINSTALL
                + "(_id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,pkgname VARCHAR(100) NOT NULL UNIQUE,appname VARCHAR(32))";
        db.execSQL(CREATE_WHITE_UNINSTALL_TABLE);

        String CREATE_TCLEAR_DATA_TABLE = "CREATE TABLE "
                + TABLE_CLEAR_DATA
                + "(_id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,pkgname VARCHAR(100) NOT NULL UNIQUE,appname VARCHAR(32))";
        db.execSQL(CREATE_TCLEAR_DATA_TABLE);

        String CREATE_WHITE_APK_TABLE = "CREATE TABLE "
                + TABLE_WHITE_APK
                + "(_id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,name VARCHAR(100) NOT NULL UNIQUE,directory VARCHAR(100))";
        db.execSQL(CREATE_WHITE_APK_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            String DROP_TABLE = "drop table ";
            // 先删除旧表
            db.execSQL(DROP_TABLE + TABLE_WHITE_UNINSTALL);
            db.execSQL(DROP_TABLE + TABLE_CLEAR_DATA);
            db.execSQL(DROP_TABLE + TABLE_WHITE_APK);

            onCreate(db); // 建立新的表
        }
    }
}
