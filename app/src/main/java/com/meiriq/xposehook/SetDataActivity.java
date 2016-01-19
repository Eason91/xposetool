package com.meiriq.xposehook;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.meiriq.xposehook.adapter.DataSpinner1Adapter;
import com.meiriq.xposehook.adapter.DataSpinnerAdapter;
import com.meiriq.xposehook.bean.Channel;
import com.meiriq.xposehook.bean.ConfigHelper;
import com.meiriq.xposehook.bean.DataInfo;
import com.meiriq.xposehook.bean.DataKeepStatus;
import com.meiriq.xposehook.dao.LocalDataDao;
import com.meiriq.xposehook.net.Callback;
import com.meiriq.xposehook.net.ErrorObject;
import com.meiriq.xposehook.net.control.ChannelService;
import com.meiriq.xposehook.net.control.DataService;
import com.meiriq.xposehook.utils.DateUtil;
import com.meiriq.xposehook.utils.DialogUtil;
import com.meiriq.xposehook.utils.L;
import com.meiriq.xposehook.utils.RandomUtil;
import com.meiriq.xposehook.utils.SP;
import com.meiriq.xposehook.utils.XposeUtil;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SetDataActivity extends TimePickActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private static final String[] DAY = {"0:0","留存(今天):1","留存(昨天):2","留存:3","留存:4","留存:5","留存:6","留存:7","留存:8","留存:9","留存:10"};
    private static final String[] HOURS = {"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"};
    private static final String[] MINUTE_AND_SECOND = {"00","10","20","30","40","50"};

    public static final int LOCAL_DATA = 0x1 << 2;

    private static List<DataKeepStatus> mDataKeepStatuses;//留存时间管理数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_data);
        initActionBar();

        setActionBar();

        initView();

        XposeUtil.initConfigMap();

        getChannelName();

        initData();


    }

    private void initData() {
        mDataKeepStatuses = DataKeepStatus.loadDataKeepStatus(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mDataKeepStatuses == null ){
            return;
        }
        DataKeepAsync keepAsync = new DataKeepAsync();
        keepAsync.execute(0);
    }

    /**
     * 每次resume都验证一下当前留存时间是否需要改变
     */
    class DataKeepAsync extends AsyncTask<Integer,Integer,Integer>{

        @Override
        protected Integer doInBackground(Integer... params) {
            String curDate = DateUtil.getCurDate();
            String keepDay = DAY[mPositionDay].split(":")[1];
            int queryInWhichDayCount = localDataDao.queryInWhichDayCount(new String[]{DateUtil.getCurDate(Integer.parseInt(keepDay)), curDate});
            long timeInMillis = Calendar.getInstance().getTimeInMillis();
            for (int i = 0; i < mDataKeepStatuses.size(); i++) {
                DataKeepStatus dataKeepStatus = mDataKeepStatuses.get(i);
                L.log(dataKeepStatus.toString());
                if( queryInWhichDayCount == dataKeepStatus.getKeepCount() && !curDate.equals(dataKeepStatus.getUseDay())){
                    mPositionDay = dataKeepStatus.getKeepDayStatus();
                    dataKeepStatus.setUseDay(curDate);
                    DataKeepStatus.saveDataKeepStatus(SetDataActivity.this, mDataKeepStatuses);
                    return mPositionDay;
                }
            }
            for (int j = 0; j < mDataKeepStatuses.size(); j++) {
                DataKeepStatus dataKeepStatus = mDataKeepStatuses.get(j);
                String[] split = dataKeepStatus.getKeepTime().split(":");
                Calendar instance = Calendar.getInstance();
                instance.set(Calendar.HOUR_OF_DAY,Integer.parseInt(split[0]));
                instance.set(Calendar.MINUTE, Integer.parseInt(split[1]));
                if(Math.abs(instance.getTimeInMillis() - timeInMillis) < 1000 * 60 *15 && !curDate.equals(dataKeepStatus.getUseDay())){
                    mPositionDay = dataKeepStatus.getKeepDayStatus();
                    dataKeepStatus.setUseDay(curDate);
                    DataKeepStatus.saveDataKeepStatus(SetDataActivity.this, mDataKeepStatuses);
                    return mPositionDay;
                }
            }
            return -1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if(result != -1){
                spinnerDay.setSelection(mPositionDay);
            }
        }
    }

    ChannelService channelService;
    private void getChannelName() {
        mPositionChannel = SP.getInt(SetDataActivity.this, SP.KEY_CHANNEL);
        channelList = ConfigHelper.loadChannel(this);
        channelService = new ChannelService(this);
        channelService.setCallback(new Callback() {
            @Override
            public void onStart() {
                showLoadingProgressDialog();
            }

            @Override
            public void onSuccess(Object object) {
                dismissProgressDialog();
                mSwipeRefreshLayout.setRefreshing(false);
                channelList = (List<Channel>) object;
                Log.d("unlock","刷新数据"+channelList.toString());
                ConfigHelper.saveChannel(SetDataActivity.this,channelList);
                setChannelName();
            }

            @Override
            public void onError(ErrorObject error) {
                mSwipeRefreshLayout.setRefreshing(false);
                dismissProgressDialog();
                channelList = Channel.getDefaultChannels();
                Log.d("unlock","刷新数据"+error.toString());

                setChannelName();
                Toast.makeText(SetDataActivity.this, "获取渠道名失败", Toast.LENGTH_SHORT).show();
            }
        });
        if(channelList == null){
            channelService.getChannelName();
        }else{
            setChannelName();
        }
    }

    private void setChannelName() {
        adapter.setData(channelList);
        adapter.notifyDataSetChanged();

        spinner.setSelection(mPositionChannel);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != mPositionChannel) {
                    mPositionChannel = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    List<Channel> channelList;
    int mPositionChannel = 0;
    int mPositionDay = 0;
    int mPositionHour = 0;
    int mPositionMinute = 0;
    int mPositionHourTo = 0;
    int mPositionMinuteTo = 0;
    DataSpinnerAdapter adapter;
    Spinner spinner;
    private void setActionBar() {

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(R.layout.actionbar_custom_spinner);
        spinner = (Spinner) actionBar.getCustomView().findViewById(R.id.spinner);
        adapter = new DataSpinnerAdapter(this);
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
    SwipeRefreshLayout mSwipeRefreshLayout;
    LocalDataDao localDataDao;
//    AppCompatCheckBox mEndTimeBox;
//    TextView mEndTimeText;
    private void initView() {

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        AppCompatButton mBt_LocalData = (AppCompatButton) findViewById(R.id.accb_local);
        mBt_LocalData.setOnClickListener(this);
        AppCompatButton mLocalRandom = (AppCompatButton) findViewById(R.id.accb_random);
        mLocalRandom.setOnClickListener(this);

        localDataDao = new LocalDataDao(this);
        dataService = new DataService(this);

        initSpinnerView();

        initDayView();

//        mEndTimeBox = (AppCompatCheckBox) findViewById(R.id.end_time_check);
//        mEndTimeBox.setChecked(SP.getBoolean(this, SP.KEY_SET_END_AUTO));
//        mEndTimeText = (TextView) findViewById(R.id.end_time_text);
//        mEndTimeText.setText(SP.getString(this, SP.KEY_END_AUTO_TODAY));
//        mEndTimeText.setOnClickListener(this);


        TextInputLayout tilDeviceId = (TextInputLayout) findViewById(R.id.til_device_id);
        tilDeviceId.setHint("IMEI");
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

        dataService.setCallback(new Callback() {
            @Override
            public void onStart() {
                showLoadingProgressDialog();
            }

            @Override
            public void onSuccess(Object object) {
                dismissProgressDialog();
                DataInfo data = (DataInfo) object;
                if(data == null){
                    Toast.makeText(SetDataActivity.this,"当前条件下无数据",Toast.LENGTH_LONG).show();
                }else{
                    dataInfo = data;
                    setDataInfo(dataInfo);
                }
            }

            @Override
            public void onError(ErrorObject error) {
                dismissProgressDialog();
                DialogUtil.showOkDialog(SetDataActivity.this, error.getMsg());
            }
        });


    }

    EditText mDay1;
    EditText mDay2;
    EditText mDay3;
    EditText mDay4;
    EditText mDay5;
    EditText mDay6;
    EditText mDay7;
    EditText mDay8;
    EditText mDay9;
    EditText mDay10;
    private void initDayView() {
        TextInputLayout day2 = (TextInputLayout) findViewById(R.id.day2);
        day2.setHint("昨天");
        mDay2 = day2.getEditText();
        TextInputLayout day3 = (TextInputLayout) findViewById(R.id.day3);
        day3.setHint("3天前");
        mDay3 = day3.getEditText();
        TextInputLayout day4 = (TextInputLayout) findViewById(R.id.day4);
        day4.setHint("4天前");
        mDay4 = day4.getEditText();
        TextInputLayout day5 = (TextInputLayout) findViewById(R.id.day5);
        day5.setHint("5天前");
        mDay5 = day5.getEditText();
        TextInputLayout day6 = (TextInputLayout) findViewById(R.id.day6);
        day6.setHint("6天前");
        mDay6 = day6.getEditText();
        TextInputLayout day7 = (TextInputLayout) findViewById(R.id.day7);
        day7.setHint("7天前");
        mDay7 = day7.getEditText();
        TextInputLayout day8 = (TextInputLayout) findViewById(R.id.day8);
        day8.setHint("8天前");
        mDay8 = day8.getEditText();
        TextInputLayout day9 = (TextInputLayout) findViewById(R.id.day9);
        day9.setHint("9天前");
        mDay9 = day9.getEditText();
        TextInputLayout day10 = (TextInputLayout) findViewById(R.id.day10);
        day10.setHint("10天前");
        mDay10 = day10.getEditText();
        mDay2.setText(SP.getString(this, SP.KEY_WEIGHT_DAY2));
        L.log(SP.getString(this, SP.KEY_WEIGHT_DAY2));
        mDay3.setText(SP.getString(this, SP.KEY_WEIGHT_DAY3));
        L.log(SP.getString(this, SP.KEY_WEIGHT_DAY3));
        mDay4.setText(SP.getString(this, SP.KEY_WEIGHT_DAY4));
        mDay5.setText(SP.getString(this, SP.KEY_WEIGHT_DAY5));
        mDay6.setText(SP.getString(this, SP.KEY_WEIGHT_DAY6));
        mDay7.setText(SP.getString(this, SP.KEY_WEIGHT_DAY7));
        mDay8.setText(SP.getString(this, SP.KEY_WEIGHT_DAY8));
        mDay9.setText(SP.getString(this, SP.KEY_WEIGHT_DAY9));
        mDay10.setText(SP.getString(this, SP.KEY_WEIGHT_DAY10));
    }

    Spinner spinnerDay;
    private void initSpinnerView() {
        spinnerDay = (Spinner) findViewById(R.id.spinner_days);
        final DataSpinner1Adapter adapterDay = new DataSpinner1Adapter(this);
        mPositionDay = SP.getInt(this, SP.KEY_DAY);
        adapterDay.setData(DAY);
        spinnerDay.setAdapter(adapterDay);
        spinnerDay.setSelection(mPositionDay, true);
        spinnerDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                L.debug("position"+position);
                if(position != mPositionDay){
                    mPositionDay = position;

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        Spinner spinnerHour = (Spinner) findViewById(R.id.spinner_hours);
        final DataSpinner1Adapter adapterHour = new DataSpinner1Adapter(this);
        mPositionHour = SP.getInt(this,SP.KEY_HOUR);
        adapterHour.setData(HOURS);
        spinnerHour.setAdapter(adapterHour);
        spinnerHour.setSelection(mPositionHour, true);
        spinnerHour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != mPositionHour){
                    mPositionHour = position;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Spinner spinnerMinute = (Spinner) findViewById(R.id.spinner_minutes);
        final DataSpinner1Adapter adapterMinute = new DataSpinner1Adapter(this);
        mPositionMinute = SP.getInt(this,SP.KEY_MINUTE);
        adapterMinute.setData(MINUTE_AND_SECOND);
        spinnerMinute.setAdapter(adapterMinute);
        spinnerMinute.setSelection(mPositionMinute,true);
        spinnerMinute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != mPositionMinute) {
                    mPositionMinute = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Spinner spinnerMinuteTo = (Spinner) findViewById(R.id.spinner_minutes_to);
        final DataSpinner1Adapter adapterMinuteTo = new DataSpinner1Adapter(this);
        mPositionMinuteTo = SP.getInt(this,SP.KEY_MINUTE_TO);
        adapterMinuteTo.setData(MINUTE_AND_SECOND);
        spinnerMinuteTo.setAdapter(adapterMinuteTo);
        spinnerMinuteTo.setSelection(mPositionMinuteTo, true);
        spinnerMinuteTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != mPositionMinuteTo) {
                    mPositionMinuteTo = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Spinner spinnerHourTo = (Spinner) findViewById(R.id.spinner_hours_to);
        final DataSpinner1Adapter adapterHourTo = new DataSpinner1Adapter(this);
        mPositionHourTo = SP.getInt(this,SP.KEY_HOUR_TO);
        adapterHourTo.setData(HOURS);
        spinnerHourTo.setAdapter(adapterHour);
        spinnerHourTo.setSelection(mPositionHourTo, true);
        spinnerHourTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != mPositionHourTo) {
                    mPositionHourTo = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
            updateDataInfo();
            setDataToLocal();
            saveDataInfo();
            XposeUtil.saveConfigMap();
            Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();

        }else if(id ==R.id.action_get){
            String channel = channelList.get(mPositionChannel).getName();
            String day = DAY[mPositionDay];
            String[] splitDay = day.split(":");
            if(mPositionDay == 0){
                Toast.makeText(this,"当前条件下不能获取数据",Toast.LENGTH_SHORT).show();
            }
            else if(mPositionDay == 1){
                if(splitDay.length == 2){
                    dataService.getSetDateNew(channel);
                }else{
                    Toast.makeText(this,"参数失效",Toast.LENGTH_SHORT).show();
                }
                //超过指定时间后，如果还是网络获取当天数据，则自动跳到获取昨天,但是一天只能跳一次
//                if(mEndTimeBox.isChecked() && !SP.getString(this,SP.KEY_AURO_DATE).equals(DateUtil.getCurDate())){
//                    String[] split = mEndTimeText.getText().toString().split(":");
//                    if(split.length == 2){
//                        Calendar calendar = Calendar.getInstance();
//                        if((calendar.get(Calendar.HOUR_OF_DAY) > Integer.parseInt(split[0])) ||
//                                ((calendar.get(Calendar.HOUR_OF_DAY) == Integer.parseInt(split[0])) && (calendar.get(Calendar.MINUTE) >= Integer.parseInt(split[1])))){
//                            mPositionDay = 2;
//                            spinnerDay.setSelection(mPositionDay);
//                            SP.set(SP.KEY_AURO_DATE,DateUtil.getCurDate());
//                        }
//                    }
//                }
            }else{
                if(splitDay.length == 2){
                    dataService.getSetDataUsed(channel, Integer.parseInt(splitDay[1]) ,HOURS[mPositionHour],MINUTE_AND_SECOND[mPositionMinute],HOURS[mPositionHourTo],MINUTE_AND_SECOND[mPositionMinuteTo]);
                }else{
                    Toast.makeText(this,"参数失效",Toast.LENGTH_SHORT).show();
                }
            }

        }else if(id == R.id.action_get_local){
            if(mPositionDay == 0){
                Toast.makeText(this,"当前条件下不能获取数据",Toast.LENGTH_SHORT).show();

            }else{
                String day = DAY[mPositionDay];
                String splitDay = day.split(":")[1];

                DataInfo localData = localDataDao.getLocalData(DateUtil.getCurDate(Integer.parseInt(splitDay)));
                //本地有数据
                if(localData != null){
                    dataInfo = localData;
                    setDataInfo(dataInfo);
                    Toast.makeText(SetDataActivity.this,"获取历史数据",Toast.LENGTH_SHORT).show();
                    //根据设定指定天数的权重来限制那一天的本地数据获取数量．
                    String dayString = getDayString(mPositionDay);
                    if(!TextUtils.isEmpty(dayString) && mPositionDay != 1){
                        try {
                            int weight = Integer.parseInt(dayString);
                            if(weight == 0){
                                return super.onOptionsItemSelected(item);
                            }
                            boolean dataWeight = localDataDao.getDataWeight(DateUtil.getCurDate(Integer.parseInt(splitDay)), weight);
                            if(dataWeight){
                                if(mPositionDay > 0 && mPositionDay < DAY.length - 1){
                                    mPositionDay ++;
                                }
//                                else if(mPositionDay == DAY.length - 1){
//                                    mPositionDay = 0;
//                                }
                                spinnerDay.setSelection(mPositionDay);
                            }
                        }catch (Exception e){
                        }
                    }
                }else {
                    Toast.makeText(SetDataActivity.this,"指定天数下没有数据",Toast.LENGTH_SHORT).show();
                    if(mPositionDay > 0 && mPositionDay < DAY.length - 1){
                        mPositionDay ++;
                    }
//                    else if(mPositionDay == DAY.length - 1){
//                        mPositionDay = 0;
//                    }
                    spinnerDay.setSelection(mPositionDay);
                }
            }

        }else if(R.id.end_time == id){
            startActivityForResult(new Intent(this,DataKeepStatusActivity.class),DataKeepStatusActivity.REQ_DATA_KEEP);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 将数据保存到本地数据库
     */
    private void setDataToLocal(){
        if(dataInfo != null && !TextUtils.isEmpty(dataInfo.getId())){
            dataInfo.setUseTime(DateUtil.getCurDate());
            Cursor cursor = localDataDao.queryById(new String[]{dataInfo.getId()});
            if(cursor == null || !cursor.moveToFirst()){
                localDataDao.add(this.dataInfo);
            }else{
                localDataDao.update(dataInfo, "id = ? and imei = ?", new String[]{dataInfo.getId(), dataInfo.getDeviceId()});
            }
        }
    }

    /**
     * 保存信息到hashmap
     */
    private void saveDataInfo() {
        jsonPut(XposeUtil.m_deviceId, mDeviceId.getText().toString().trim());
        jsonPut(XposeUtil.m_androidId, mAndroidId.getText().toString().trim());
        jsonPut(XposeUtil.m_bluetoothaddress, mBluetoothAddress.getText().toString().trim());
        jsonPut(XposeUtil.m_fingerprint, mFingerPrint.getText().toString().trim());
        jsonPut(XposeUtil.m_firmwareversion, mFirmwareVersion.getText().toString().trim());
        jsonPut(XposeUtil.m_hardware, mHardware.getText().toString().trim());
        jsonPut(XposeUtil.m_subscriberId, mIMSI.getText().toString().trim());
        jsonPut(XposeUtil.m_macAddress, mMacAddress.getText().toString().trim());
        jsonPut(XposeUtil.m_networkType, mNetType.getText().toString().trim());
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

    private void jsonPut(String key,String value){
        try {
            XposeUtil.configMap.put(key,value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localDataDao.close();

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

        SP.set(SP.KEY_CHANNEL,mPositionChannel);
        SP.set(SP.KEY_DAY,mPositionDay);
        SP.set(SP.KEY_HOUR,mPositionHour);
        SP.set(SP.KEY_HOUR_TO,mPositionHourTo);
        SP.set(SP.KEY_MINUTE,mPositionMinute);
        SP.set(SP.KEY_MINUTE_TO, mPositionMinuteTo);
//        SP.set(SP.KEY_END_AUTO_TODAY, mEndTimeText.getText().toString());
//        SP.set(SP.KEY_SET_END_AUTO,mEndTimeBox.isChecked());

        SP.set(SP.KEY_WEIGHT_DAY2,getDayString(2));
        SP.set(SP.KEY_WEIGHT_DAY3,getDayString(3));
        SP.set(SP.KEY_WEIGHT_DAY4,getDayString(4));
        SP.set(SP.KEY_WEIGHT_DAY5,getDayString(5));
        SP.set(SP.KEY_WEIGHT_DAY6,getDayString(6));
        SP.set(SP.KEY_WEIGHT_DAY7,getDayString(7));
        SP.set(SP.KEY_WEIGHT_DAY8,getDayString(8));
        SP.set(SP.KEY_WEIGHT_DAY9,getDayString(9));
        SP.set(SP.KEY_WEIGHT_DAY10,getDayString(10));

        updateDataInfo();
        setDataToLocal();
        ConfigHelper.saveDataInfo(this, dataInfo);
        saveDataInfo();
        XposeUtil.saveConfigMap();
        Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        Log.d("unlock","刷新数据");
        channelService.getChannelName();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        L.log("onActivityResult"+requestCode);
        if(requestCode == LOCAL_DATA && resultCode == RESULT_OK){
            DataInfo dataInfo = (DataInfo) data.getSerializableExtra(LocalDataDetailActivity.DATA);
            setDataInfo(dataInfo);
        }else if(requestCode == DataKeepStatusActivity.REQ_DATA_KEEP && resultCode == RESULT_OK){
            mDataKeepStatuses = DataKeepStatus.loadDataKeepStatus(this);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.accb_local){
            startActivityForResult(new Intent(this, LocalDataActivity.class), LOCAL_DATA);
        }else if(v.getId() == R.id.accb_random){
            DataInfo random = RandomUtil.getRandom();
            random.setSaveTime(DateUtil.getCurDate());
            random.setDetailTime(System.currentTimeMillis());
            dataInfo = random;
            setDataInfo(dataInfo);
        }else if(v.getId() == R.id.end_time_text){
            showCurTimePickerFragment();
        }
    }


    static Calendar mCalendar = Calendar.getInstance();



    /**
     * 根据参数或者指定文本框的数据
     * @param position
     * @return
     */
    private String getDayString(int position){

        String result = "";
        switch (position){

            case 1:
                result = mDay1.getText().toString().trim();
                break;
            case 2:
                result = mDay2.getText().toString().trim();
                break;
            case 3:
                result = mDay3.getText().toString().trim();
                break;
            case 4:
                result = mDay4.getText().toString().trim();
                break;
            case 5:
                result = mDay5.getText().toString().trim();
                break;
            case 6:
                result = mDay6.getText().toString().trim();
                break;
            case 7:
                result = mDay7.getText().toString().trim();
                break;
            case 8:
                result = mDay8.getText().toString().trim();
                break;
            case 9:
                result = mDay9.getText().toString().trim();
                break;
            case 10:
                result = mDay10.getText().toString().trim();
                break;
        }

        return result;
    }
}
