package com.meiriq.xposehook.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.meiriq.xposehook.bean.ApkInfo;
import com.meiriq.xposehook.bean.DataInfo;
import com.meiriq.xposehook.bean.util.ApkInfoUtil;
import com.meiriq.xposehook.bean.util.SetDataUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tian on 15-11-27.
 */
public class LocalDataDao extends BaseDao<DataInfo>{

    private final String TABLE_LOCAL_DATA = DbHelper.TABLE_LOCAL_DATA;

    public LocalDataDao(Context context) {
        super(context);
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

    public Cursor queryDateTime(String[] args){
        return query(String.format("select * from %s where savetime = ? and usetime != ?", TABLE_LOCAL_DATA), args);
    }

    public Cursor queryDataType(String[] args){
        return query(String.format("select savetime,count(*) as counts from %s where usetime != ? group by savetime ", TABLE_LOCAL_DATA),args);
    }


}
