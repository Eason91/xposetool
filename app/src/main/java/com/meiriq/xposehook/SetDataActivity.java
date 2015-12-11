package com.meiriq.xposehook;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.meiriq.xposehook.adapter.DataSpinnerAdapter;
import com.meiriq.xposehook.bean.ConfigHelper;
import com.meiriq.xposehook.bean.Constant;
import com.meiriq.xposehook.bean.DataInfo;
import com.meiriq.xposehook.bean.util.SetDataUtil;
import com.meiriq.xposehook.net.VolleyListener;
import com.meiriq.xposehook.net.control.DataService;
import com.meiriq.xposehook.utils.DialogUtil;
import com.meiriq.xposehook.utils.XposeUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
    private EditText mIMSI;
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
    DataService dataService;
    private void initView() {

        dataService = new DataService(this);

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
        mIMSI = tilISIM.getEditText();

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

        dataInfo = ConfigHelper.loadDataInfo(this);
        if(dataInfo!=null){
            setDataInfo(dataInfo);
        }

    }
    private DataInfo dataInfo;

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
            saveDataInfo();
            XposeUtil.saveConfigMap();
            Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();

        }else if(id ==R.id.action_get){
            showLoadingProgressDialog();
            dataService.getStringRequest(Constant.url_getData, null, new VolleyListener() {
                @Override
                public void onComplete(JSONObject jsonObject) {
                    dismissProgressDialog();
                    dataInfo = SetDataUtil.parseJsonObject2DataInfo(jsonObject);
                    setDataInfo(dataInfo);
                }

                @Override
                public void onError(VolleyError error) {
                    dismissProgressDialog();
                    DialogUtil.showOkDialog(SetDataActivity.this, "加载失败");
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveDataInfo() {

        jsonPut(XposeUtil.m_deviceId, mDeviceId.getText().toString().trim());
        jsonPut(XposeUtil.m_androidId, mAndroidId.getText().toString().trim());
        jsonPut(XposeUtil.m_bluetoothaddress, mBluetoothAddress.getText().toString().trim());
//        jsonPut(XposeUtil.m_, mCpu.getText().toString().trim());
        jsonPut(XposeUtil.m_fingerprint, mFingerPrint.getText().toString().trim());
        jsonPut(XposeUtil.m_firmwareversion, mFirmwareVersion.getText().toString().trim());
        jsonPut(XposeUtil.m_hardware, mHardware.getText().toString().trim());
//        jsonPut(XposeUtil.m_, mInternalIp.getText().toString().trim());
        jsonPut(XposeUtil.m_subscriberId, mIMSI.getText().toString().trim());
        jsonPut(XposeUtil.m_macAddress, mMacAddress.getText().toString().trim());
        jsonPut(XposeUtil.m_networkType, mNetType.getText().toString().trim());
//        jsonPut(XposeUtil.m_, mEquipmentName.getText().toString().trim());
        jsonPut(XposeUtil.m_networkOperatorName, mNetTypeName.getText().toString().trim());
        jsonPut(XposeUtil.m_simOperator, mOperator.getText().toString().trim());
        jsonPut(XposeUtil.m_brand, mPhoneBrand.getText().toString().trim());
        jsonPut(XposeUtil.m_model, mPhoneModelNumber.getText().toString().trim());
        jsonPut(XposeUtil.m_phoneNum, mPhoneNum.getText().toString().trim());
        jsonPut(XposeUtil.m_phoneType, mPhoneType.getText().toString().trim());
        jsonPut(XposeUtil.m_serial, mPortNumber.getText().toString().trim());
        jsonPut(XposeUtil.m_product, mProductName.getText().toString().trim());
        jsonPut(XposeUtil.m_manufacture, mProductor.getText().toString().trim());
        jsonPut(XposeUtil.m_simSerialNumber, mSimId.getText().toString().trim());
        jsonPut(XposeUtil.m_SSID, mRouteName.getText().toString().trim());
        jsonPut(XposeUtil.m_BSSID, mRouteAddress.getText().toString().trim());
        jsonPut(XposeUtil.m_screenSize, mScreenSize.getText().toString().trim());
        jsonPut(XposeUtil.m_simState, mSimStatus.getText().toString().trim());
        jsonPut(XposeUtil.m_framework, mSystemFramework.getText().toString().trim());
        jsonPut(XposeUtil.m_RELEASE, mSystemVersion.getText().toString().trim());
        jsonPut(XposeUtil.m_SDK, mSystemVersionValue.getText().toString().trim());

    }

    private void setDataInfo(DataInfo dataInfo) {
        mDeviceId.setText(dataInfo.getDeviceId());
        mAndroidId.setText(dataInfo.getAndroidId());
        mBluetoothAddress.setText(dataInfo.getBluetoothAddress());
        mCpu.setText(dataInfo.getCpu());
        mFingerPrint.setText(dataInfo.getFingerPrint());
        mFirmwareVersion.setText(dataInfo.getFirmwareVersion());
        mHardware.setText(dataInfo.getHardware());
        mInternalIp.setText(dataInfo.getInternalIp());
        mIMSI.setText(dataInfo.getImsi());
        mMacAddress.setText(dataInfo.getMacAddress());
        mNetType.setText(dataInfo.getNetType());
        mEquipmentName.setText(dataInfo.getEquipmentName());
        mNetTypeName.setText(dataInfo.getNetTypeName());
        mOperator.setText(dataInfo.getOperator());
        mPhoneBrand.setText(dataInfo.getPhoneBrand());
        mPhoneModelNumber.setText(dataInfo.getPhoneModelNumber());
        mPhoneNum.setText(dataInfo.getPhoneNum());
        mPhoneType.setText(dataInfo.getPhoneType());
        mPortNumber.setText(dataInfo.getPortNumber());
        mProductName.setText(dataInfo.getProductName());
        mProductor.setText(dataInfo.getProductor());
        mSimId.setText(dataInfo.getSimId());
        mRouteAddress.setText(dataInfo.getRouteAddress());
        mRouteName.setText(dataInfo.getRouteName());
        mScreenSize.setText(dataInfo.getScreenSize());
        mSimStatus.setText(dataInfo.getSimStatus());
        mSystemFramework.setText(dataInfo.getSystemFramework());
        mSystemVersion.setText(dataInfo.getSystemVersion());
        mSystemVersionValue.setText(dataInfo.getSystemVersionValue());

    }

    private void updateDataInfo(){

        dataInfo.setDeviceId(getTextString(mDeviceId));
        dataInfo.setAndroidId(getTextString(mAndroidId));
        dataInfo.setBluetoothAddress(getTextString(mBluetoothAddress));
        dataInfo.setCpu(getTextString(mCpu));
        dataInfo.setFingerPrint(getTextString(mFingerPrint));
        dataInfo.setFirmwareVersion(getTextString(mFirmwareVersion));
        dataInfo.setHardware(getTextString(mHardware));
        dataInfo.setInternalIp(getTextString(mInternalIp));
        dataInfo.setImsi(getTextString(mIMSI));
        dataInfo.setMacAddress(getTextString(mMacAddress));
        dataInfo.setNetType(getTextString(mNetType));
        dataInfo.setEquipmentName(getTextString(mEquipmentName));
        dataInfo.setNetTypeName(getTextString(mNetTypeName));
        dataInfo.setOperator(getTextString(mOperator));
        dataInfo.setPhoneBrand(getTextString(mPhoneBrand));
        dataInfo.setPhoneModelNumber(getTextString(mPhoneModelNumber));
        dataInfo.setPhoneNum(getTextString(mPhoneNum));
        dataInfo.setPhoneType(getTextString(mPhoneType));
        dataInfo.setPortNumber(getTextString(mPortNumber));
        dataInfo.setProductName(getTextString(mProductName));
        dataInfo.setProductor(getTextString(mProductor));
        dataInfo.setSimId(getTextString(mSimId));
        dataInfo.setRouteName(getTextString(mRouteName));
        dataInfo.setRouteAddress(getTextString(mRouteAddress));
        dataInfo.setScreenSize(getTextString(mScreenSize));
        dataInfo.setSimStatus(getTextString(mSimStatus));
        dataInfo.setSystemFramework(getTextString(mSystemFramework));
        dataInfo.setSystemVersion(getTextString(mSystemVersion));
        dataInfo.setSystemVersionValue(getTextString(mSystemVersionValue));


    }

    private String getTextString(EditText editText) {
        String result = editText.getText().toString().trim();
        if(result== null)
            result = "";

        return result;
    }

    private void jsonPut(String key,String value){
        try {
            XposeUtil.configMap.put(key,value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

        updateDataInfo();
        ConfigHelper.saveDataInfo(this, dataInfo);
        saveDataInfo();
        XposeUtil.saveConfigMap();
        Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
    }
}
