package com.meiriq.xposehook;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.meiriq.xposehook.adapter.AppListAdapter;
import com.meiriq.xposehook.bean.AppInfo;
import com.meiriq.xposehook.bean.ConfigHelper;
import com.meiriq.xposehook.dao.WhiteUninstallDao;
import com.meiriq.xposehook.tutorial.AppUtils;
import com.meiriq.xposehook.utils.L;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RecordAppFileChooseActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private List<AppInfo> installApps;
    private AppListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uninstall_white);
        initActionBar();

        initView();
        initData();
    }


    private void initData() {


        installApps = AppUtils.getInstance().getInstallApps(this, AppUtils.APP_TYPE_CUSTOM);
        HashMap<String, String> hashMap = ConfigHelper.getConfig().getFileHookNameAndPackage();
        if(hashMap != null){
            Iterator<Map.Entry<String, String>> iterator = hashMap.entrySet().iterator();

            while (iterator.hasNext()){
                String key = iterator.next().getKey();
                for (int i = 0; i < installApps.size(); i++) {
                    if(key.equals(installApps.get(i).getPname())){
                        installApps.get(i).setIsSelect(true);
                        break;
                    }
                }
            }

        }
        adapter = new AppListAdapter(this);
        adapter.setData(installApps);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
    }

    private void initView() {

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_recycleview);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            back();
        }


        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void back() {
        super.back();
        save();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    private void save() {
        HashMap<String, String> hashMap = ConfigHelper.getConfig().getFileHookNameAndPackage();
        if(hashMap == null)
            hashMap = new HashMap<>();
        hashMap.clear();
        for (int i = 0; i < installApps.size(); i++) {
            AppInfo appInfo = installApps.get(i);
            if(appInfo.isSelect()){
                hashMap.put(appInfo.getPname(), appInfo.getAppname());
            }
        }
        ConfigHelper.getConfig().setFileHookNameAndPackage(hashMap);
        setResult(Activity.RESULT_OK);
        finish();
    }
}
