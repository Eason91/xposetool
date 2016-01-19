package com.meiriq.xposehook.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.meiriq.xposehook.bean.ApkInfo;
import com.meiriq.xposehook.bean.DataInfo;
import com.meiriq.xposehook.bean.util.ApkInfoUtil;
import com.meiriq.xposehook.bean.util.SetDataUtil;
import com.meiriq.xposehook.utils.DateUtil;
import com.meiriq.xposehook.utils.L;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tian on 15-11-27.
 */
public class LocalDataDao extends BaseDao<DataInfo>{

    private final String TABLE_LOCAL_DATA = DbHelper.TABLE_LOCAL_DATA;

    public LocalDataDao(Context context) {
        super(context);
        File file = new File(Environment.getExternalStorageDirectory()+"/.xpose/");
        if(!file.exists())
            file.mkdir();
        file = new File(file,"localdata.db");

        if(!file.exists()){
            mDatabase = SQLiteDatabase.openOrCreateDatabase
                    (file,null);
            mDatabase.execSQL(DbHelper.CREATE_LOCAL_DATA_TABLE);
        }else {
            mDatabase = SQLiteDatabase.openOrCreateDatabase
                    (file,null);
        }

    }

    @Override
    public boolean add(DataInfo item) {
        return insert(TABLE_LOCAL_DATA, SetDataUtil.wrapLocalData2Values(item)) != -1;
    }

    @Override
    public boolean addList(List<DataInfo> list) {
        List<ContentValues> values = new ArrayList<>();
        for (DataInfo item:list) {
            values.add(SetDataUtil.wrapLocalData2Values(item));

        }
        return insertList(TABLE_LOCAL_DATA, values) != -1;
    }

    @Override
    public int delete(String whereClause, String[] whereArgs) {
        return delete(TABLE_LOCAL_DATA, whereClause, whereArgs);
    }

    @Override
    public int clean() {
        return clean(TABLE_LOCAL_DATA);
    }

    @Override
    public int update(DataInfo item, String whereClause, String[] whereArgs) {
        return update(TABLE_LOCAL_DATA, SetDataUtil.wrapLocalData2Values(item), whereClause, whereArgs);
    }

    @Override
    public Cursor query(String[] args) {
        return query(String.format("select * from %s", TABLE_LOCAL_DATA), null);
    }

    public Cursor queryById(String[] args) {
        return query(String.format("select * from %s where id = ?", TABLE_LOCAL_DATA), args);
    }

    public int queryInWhichDayCount(String[] args){
        return query(String.format("select * from %s where savetime = ? and usetime = ?",TABLE_LOCAL_DATA),args).getCount();
    }

    public int queryDayCount(String[] args){
        return query(String.format("select * from %s where savetime = ?",TABLE_LOCAL_DATA),args).getCount();
    }


    /**
     * 指定时间的没用过的本地数据
     * @param args
     * @return
     */
    public Cursor queryDateTime(String[] args){
        return query(String.format("select * from %s where savetime = ? and usetime != ? order by detailtime ASC", TABLE_LOCAL_DATA), args);
    }

    /**
     * 指定时间的所有本地数据
     * @param args
     * @return
     */
    public Cursor queryDateTimeAll(String[] args){
        return query(String.format("select * from %s where savetime = ?", TABLE_LOCAL_DATA), args);
    }

    /**
     * 本地数据的目录
     * @param args
     * @return
     */
    public Cursor queryDataType(String[] args){
        return query(String.format("select savetime,count(*) as counts from %s group by savetime ", TABLE_LOCAL_DATA),null);
    }

    /**
     * 获取指定天数前的某一条数据
     * @param saveTime
     * @return
     */
    public DataInfo getLocalData(String saveTime){
        Cursor cursor = queryDateTime(new String[]{saveTime, DateUtil.getCurDate()});
        ArrayList<DataInfo> dataInfos = SetDataUtil.parseCursor2List(cursor);
        if(dataInfos.size()>0)
            return dataInfos.get(0);
        return null;
    }

    /**
     * 获取指定某天的数据与其总数据比重是否大于指定weight
     * @param saveTime
     * @param weight
     * @return
     */
    public boolean getDataWeight(String saveTime,float weight){

        int useCount = queryInWhichDayCount(new String[]{saveTime, DateUtil.getCurDate()});
        int allCount = queryDayCount(new String[]{saveTime});
        if(allCount <= 0){
            return true;
        }
        float v = (float) useCount * 100 / allCount;
        L.log("useCount"+useCount+"allCount"+allCount+"v"+v+"saveTime"+saveTime);
        if(v >= weight){
            return true;
        }

        return false;
    }


}
