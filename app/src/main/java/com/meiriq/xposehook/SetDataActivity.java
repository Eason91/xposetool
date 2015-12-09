package com.meiriq.xposehook;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.meiriq.xposehook.adapter.DataSpinnerAdapter;
import com.meiriq.xposehook.utils.L;
import com.meiriq.xposehook.utils.XposeUtil;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SetDataActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_data);
        initActionBar();

        setActionBar();

        initView();

        XposeUtil.initConfigMap();

    }

    private void setActionBar() {

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(R.layout.actionbar_custom_spinner);
        Spinner spinner = (Spinner) actionBar.getCustomView().findViewById(R.id.spinner);
        DataSpinnerAdapter adapter = new DataSpinnerAdapter(this);
        List<String> lists = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            lists.add(i+"hello");
        }
        adapter.setData(lists);
        spinner.setAdapter(adapter);
    }

    private EditText mDeviceId;
    private EditText mAndroidId;
    private EditText mPhoneNum;
    private EditText mSimId;
    private EditText mISIM;
    private EditText mOperator;
    EditText mNetTypeName;
    EditText mNetType;
    EditText mPhoneType;
    EditText mSimStatus;
    EditText mMacAddress;
    EditText mRouteName;
    EditText mRouteAddress;
    EditText mSystemVersion;
    EditText mSystemVersionValue;
    EditText mSystemFramework;
    EditText mScreenSize;
    EditText mFirmwareVersion;
    EditText mPhoneBrand;
    EditText mPhoneModelNumber;
    EditText mProductName;
    EditText mProductor;
    EditText mEquipmentName;
    EditText mCpu;
    EditText mHardware;
    EditText mFingerPrint;
    EditText mPortNumber;
    EditText mBluetoothAddress;
    EditText mInternalIp;
    private void initView() {
        TextInputLayout tilDeviceId = (TextInputLayout) findViewById(R.id.til_device_id);
        tilDeviceId.setHint("序列号");
        mDeviceId = tilDeviceId.getEditText();

        TextInputLayout tilAndroidId = (TextInputLayout) findViewById(R.id.til_android_id);
        tilAndroidId.setHint("android_id");
        mAndroidId = tilAndroidId.getEditText();

        TextInputLayout tilPhoneNum = (TextInputLayout) findViewById(R.id.til_phone_num);
        tilPhoneNum.setHint("手机号码");
        mPhoneNum = tilPhoneNum.getEditText();

        TextInputLayout tilSimid = (TextInputLayout) findViewById(R.id.til_sim_id);
        tilSimid.setHint("手机卡序列号");
        mSimId = tilSimid.getEditText();

        TextInputLayout tilISIM = (TextInputLayout) findViewById(R.id.til_isim);
        tilISIM.setHint("ISIM");
        mISIM = tilISIM.getEditText();

        TextInputLayout tilOperator = (TextInputLayout) findViewById(R.id.til_operator);
        tilOperator.setHint("运营商");
        mOperator = tilOperator.getEditText();


        TextInputLayout t7 = (TextInputLayout) findViewById(R.id.til_net_type_name);
        t7.setHint("网络类型名");
        mNetTypeName = t7.getEditText();

        TextInputLayout t8 = (TextInputLayout) findViewById(R.id.til_net_type);
        t8.setHint("网络类型");
        mNetType = t8.getEditText();

        TextInputLayout t9 = (TextInputLayout) findViewById(R.id.til_phone_type);
        t9.setHint("手机类型");
        mPhoneType = t9.getEditText();

        TextInputLayout t10 = (TextInputLayout) findViewById(R.id.til_sim_status);
        t10.setHint("手机卡状态");
        mSimStatus = t10.getEditText();

        TextInputLayout t11 = (TextInputLayout) findViewById(R.id.til_mac);
        t11.setHint("mac地址");
        mMacAddress = t11.getEditText();

        TextInputLayout t12 = (TextInputLayout) findViewById(R.id.til_route_name);
        t12.setHint("无线路由器名");
        mRouteName = t12.getEditText();

        TextInputLayout t13 = (TextInputLayout) findViewById(R.id.til_route_add);
        t13.setHint("无线路由器地址");
        mRouteAddress = t13.getEditText();

        TextInputLayout t14 = (TextInputLayout) findViewById(R.id.til_sys_version);
        t14.setHint("系统版本");
        mSystemVersion = t14.getEditText();

        TextInputLayout t15 = (TextInputLayout) findViewById(R.id.til_sys_version_value);
        t15.setHint("系统版本值");
        mSystemVersionValue = t15.getEditText();

        TextInputLayout t16 = (TextInputLayout) findViewById(R.id.til_sys_framework);
        t16.setHint("系统构架");
        mSystemFramework = t16.getEditText();

        TextInputLayout t17 = (TextInputLayout) findViewById(R.id.til_screen_size);
        t17.setHint("屏幕分辨率");
        mScreenSize = t17.getEditText();

        TextInputLayout t18 = (TextInputLayout) findViewById(R.id.til_firmware_version);
        t18.setHint("固件版本");
        mFirmwareVersion = t18.getEditText();

        TextInputLayout t19 = (TextInputLayout) findViewById(R.id.til_phone_brand);
        t19.setHint("手机品牌");
        mPhoneBrand = t19.getEditText();

        TextInputLayout t20 = (TextInputLayout) findViewById(R.id.til_phone_model_number);
        t20.setHint("型号");
        mPhoneModelNumber = t20.getEditText();

        TextInputLayout t21 = (TextInputLayout) findViewById(R.id.til_product_name);
        t21.setHint("产品名");
        mProductName = t21.getEditText();

        TextInputLayout t22 = (TextInputLayout) findViewById(R.id.til_productor);
        t22.setHint("制造商");
        mProductor = t22.getEditText();

        TextInputLayout t23 = (TextInputLayout) findViewById(R.id.til_equipment_name);
        t23.setHint("设备名");
        mEquipmentName = t23.getEditText();

        TextInputLayout t24 = (TextInputLayout) findViewById(R.id.til_cpu);
        t24.setHint("CPU型号");
        mCpu = t24.getEditText();

        TextInputLayout t25 = (TextInputLayout) findViewById(R.id.til_hardware);
        t25.setHint("硬件");
        mHardware = t25.getEditText();

        TextInputLayout t26 = (TextInputLayout) findViewById(R.id.til_fingerprint);
        t26.setHint("指纹");
        mFingerPrint = t26.getEditText();

        TextInputLayout t27 = (TextInputLayout) findViewById(R.id.til_port_serial_number);
        t27.setHint("串口序列号");
        mPortNumber = t27.getEditText();

        TextInputLayout t28 = (TextInputLayout) findViewById(R.id.til_bluetooth_address);
        t28.setHint("蓝牙地址");
        mBluetoothAddress = t28.getEditText();

        TextInputLayout t29 = (TextInputLayout) findViewById(R.id.til_internal_work_ip);
        t29.setHint("内网IP");
        mInternalIp = t29.getEditText();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setdata, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            int i = new Random().nextInt(100);
            String tess = "1234567890"+i;
            try {
                XposeUtil.configMap.put(XposeUtil.deviceId,tess);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            XposeUtil.saveConfigMap();
            L.debug("保存成功"+tess);

        }else if(id ==R.id.action_get){
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            String deviceId = telephonyManager.getDeviceId();
            Toast.makeText(this,deviceId+"",Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }



}
