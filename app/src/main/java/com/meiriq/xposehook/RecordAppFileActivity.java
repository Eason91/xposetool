package com.meiriq.xposehook;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.meiriq.xposehook.adapter.FileListAdapter;
import com.meiriq.xposehook.bean.AppInfo;
import com.meiriq.xposehook.bean.Config;
import com.meiriq.xposehook.bean.ConfigHelper;
import com.meiriq.xposehook.utils.L;
import com.meiriq.xposehook.utils.RecordFileUtil;
import com.meiriq.xposehook.utils.XposeUtil;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecordAppFileActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener{

    private static final int REQ_CHOOSEAPP = 0x1 << 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_app_file);
        initActionBar();



        initView();
    }

    private RecyclerView recyclerView;
    private EditText editText;
    private Config config;
    private AppCompatCheckBox checkBox;
    private List<String> pkgList;
    private void initView() {
        TextInputLayout textInputLayout = (TextInputLayout) findViewById(R.id.til_appinfo);
        textInputLayout.setHint("请选择应用");
        editText = textInputLayout.getEditText();

        recyclerView = (RecyclerView) findViewById(R.id.rv_recycleview);

        config = ConfigHelper.getConfig();
        checkBox = (AppCompatCheckBox) findViewById(R.id.accb_appchoose);
        checkBox.setOnCheckedChangeListener(this);
        checkBox.setChecked(XposeUtil.configMap.optBoolean(XposeUtil.FileRecordPackageNameSwitch));

        XposeUtil.initConfigMap();
        pkgList = map2List(XposeUtil.configMap.optString(XposeUtil.FileRecordPackageName));
        L.debug(pkgList.toString());
        for (int i = 0; i <pkgList.size(); i++) {
            L.debug("开始"+pkgList.get(i));
            RecordFileUtil.getFileRecord(pkgList.get(i));
        }
        Set<String> strings = RecordFileUtil.fileMap.keySet();
        L.debug(strings.toString());
        List<String> lists = new ArrayList<>();
        lists.addAll(strings);
        L.debug(lists.toString());

        adapter = new FileListAdapter(this);
        adapter.setData(lists);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);



        setEdit();
    }

    private List<String> map2List(String map){
        List<String> list = new ArrayList<>();
        String[] split = map.split(",");
        if(split == null)
            return list;
        for (int i = 0; i < split.length; i++) {
            String pkg = split[i].split("=")[0];
            if(pkg.startsWith("{"))
                pkg = pkg.substring(1);
            list.add(pkg.trim());
        }
        return list;
    }


    private FileListAdapter adapter;
    private void setEdit() {
        HashMap<String, String> hashMap = ConfigHelper.getConfig().getFileHookNameAndPackage();


        L.debug((hashMap == null)+"");
        if(hashMap != null){
            Iterator<Map.Entry<String, String>> iterator = hashMap.entrySet().iterator();
            String recordappname = "";
            while (iterator.hasNext()){
                recordappname = recordappname + iterator.next().getValue()+",";
            }
            editText.setText(recordappname);
            editText.setEnabled(false);
            try {
                XposeUtil.configMap.put(XposeUtil.FileRecordPackageName,hashMap.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.record_app_file, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            delete();
        }else if(id == R.id.action_add){
            startActivityForResult(new Intent(this,RecordAppFileChooseActivity.class),REQ_CHOOSEAPP);
        }

        return super.onOptionsItemSelected(item);
    }

    private void delete() {
        RecordFileUtil.deleteFile();
        for (int i = 0; i < pkgList.size(); i++) {

            boolean b = RecordFileUtil.clearFileRecord(pkgList.get(i));
            L.debug("目录删除－－－－－－－－－－－"+b+pkgList.get(i));
        }
        Toast.makeText(this,"删除成功",Toast.LENGTH_SHORT).show();
        RecordFileUtil.fileMap.clear();
        adapter.clearData();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConfigHelper.saveConfig(this, config);
        XposeUtil.saveConfigMap();
        Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_CHOOSEAPP){
            setEdit();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            L.debug("记录文件");
            try {
                XposeUtil.configMap.put(XposeUtil.FileRecordPackageNameSwitch,true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            L.debug("不记录");
            RecordFileUtil.isRecord = false;
            try {
                XposeUtil.configMap.put(XposeUtil.FileRecordPackageNameSwitch,false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
