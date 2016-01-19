package com.meiriq.xposehook;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.meiriq.xposehook.adapter.AppListAdapter;
import com.meiriq.xposehook.bean.AppInfo;
import com.meiriq.xposehook.utils.L;
import com.meiriq.xposehook.utils.RecordFileUtil;

import java.util.ArrayList;
import java.util.List;

public class RecordFileWhiteActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    public static final String DATA = "data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_file_white);
        initActionBar();

        initView();

    }

    private AppListAdapter adapter;
    private List<String> mCurLists;
    private ArrayList<String> mWhiteFileRecord;
    private List<AppInfo> mShowWhiteLists;
    private void initView() {
        mCurLists = getIntent().getStringArrayListExtra(DATA);
        mWhiteFileRecord = RecordFileUtil.getWhiteFileRecord();
        mShowWhiteLists = new ArrayList<>();
        //添加未选择数据供用户选择
        for (int i = 0; i < mCurLists.size(); i++) {
            if(!mWhiteFileRecord.contains(mCurLists.get(i))){
                AppInfo appInfo = new AppInfo();
                appInfo.setAppname(mCurLists.get(i));
                mShowWhiteLists.add(appInfo);
            }
        }
        //添加已经是白名单的数据供用户修改
        for (int i = 0; i < mWhiteFileRecord.size(); i++) {
            AppInfo appInfo = new AppInfo();
            appInfo.setIsSelect(true);
            appInfo.setAppname(mWhiteFileRecord.get(i));
            mShowWhiteLists.add(appInfo);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_recycleview);
        adapter = new AppListAdapter(this);
        adapter.setData(mShowWhiteLists);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK){
            back();
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void back() {
        RecordFileUtil.deleteWhiteFile();
        for (int i = 0; i < mShowWhiteLists.size(); i++) {
            if(mShowWhiteLists.get(i).isSelect()){
                RecordFileUtil.addWhiteFileRecord(mShowWhiteLists.get(i).getAppname());
            }
        }
    }
}
