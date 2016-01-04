package com.meiriq.xposehook.tutorial;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.os.*;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import com.meiriq.xposehook.utils.L;
import com.meiriq.xposehook.utils.RecordFileUtil;
import com.meiriq.xposehook.utils.TestUtil;
import com.meiriq.xposehook.utils.XposeUtil;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Random;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by tian on 15-12-2.
 */
public class XposeHook implements IXposedHookLoadPackage{


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        if(android.os.Process.myUid() <= 10000){
            L.debug("系统应用"+loadPackageParam.packageName+android.os.Process.myUid());
            return ;
        }else{
            L.debug("普通应用"+loadPackageParam.packageName+android.os.Process.myUid());
        }

        if(loadPackageParam.packageName.equals("com.meiriq.xposehook")){
            addHook(loadPackageParam.packageName, TestUtil.class.getName(), loadPackageParam.classLoader, "testXpose", new Object[]{});
            return ;
        }


        XposeUtil.initConfigMap();
        L.debug("初始花" + loadPackageParam.packageName);
        if(RecordFileUtil.ExternalStorage.length() == 0){
            RecordFileUtil.ExternalStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
        }

        setSystemData();

        addHook(loadPackageParam.packageName, TelephonyManager.class.getName(), loadPackageParam.classLoader, "getDeviceId", new Object[]{});
        addHook(loadPackageParam.packageName, Settings.Secure.class.getName(), loadPackageParam.classLoader, "getString", new Object[]{ContentResolver.class.getName(), String.class.getName()});
        addHook(loadPackageParam.packageName, Settings.System.class.getName(), loadPackageParam.classLoader, "getString", new Object[]{ContentResolver.class.getName(),String.class.getName()});
        addHook(loadPackageParam.packageName, TelephonyManager.class.getName(), loadPackageParam.classLoader, "getLine1Number", new Object[]{});
        addHook(loadPackageParam.packageName, TelephonyManager.class.getName(), loadPackageParam.classLoader, "getSimSerialNumber", new Object[]{});
        addHook(loadPackageParam.packageName, TelephonyManager.class.getName(), loadPackageParam.classLoader, "getSubscriberId", new Object[]{});
        addHook(loadPackageParam.packageName, TelephonyManager.class.getName(), loadPackageParam.classLoader, "getSimOperator", new Object[]{});
        addHook(loadPackageParam.packageName, TelephonyManager.class.getName(), loadPackageParam.classLoader, "getNetworkOperatorName", new Object[]{});
        addHook(loadPackageParam.packageName, TelephonyManager.class.getName(), loadPackageParam.classLoader, "getNetworkType", new Object[]{});
        addHook(loadPackageParam.packageName, TelephonyManager.class.getName(), loadPackageParam.classLoader, "getPhoneType", new Object[]{});
        addHook(loadPackageParam.packageName, TelephonyManager.class.getName(), loadPackageParam.classLoader, "getSimState", new Object[]{});

        addHook(loadPackageParam.packageName, WifiInfo.class.getName(), loadPackageParam.classLoader, "getMacAddress", new Object[]{});
        addHook(loadPackageParam.packageName, WifiInfo.class.getName(), loadPackageParam.classLoader, "getSSID", new Object[]{});
        addHook(loadPackageParam.packageName, WifiInfo.class.getName(), loadPackageParam.classLoader, "getBSSID", new Object[]{});

        addHook(loadPackageParam.packageName, Build.class.getName(), loadPackageParam.classLoader, "getRadioVersion", new Object[]{});
        addHook(loadPackageParam.packageName, BluetoothAdapter.class.getName(), loadPackageParam.classLoader, "getAddress", new Object[]{});

//        addHook(loadPackageParam.packageName, Display.class.getName(), loadPackageParam.classLoader, "getMetrics", new Object[]{DisplayMetrics.class.getName()});
//        addHook(loadPackageParam.packageName, Display.class.getName(), loadPackageParam.classLoader, "getWidth", new Object[]{});
//        addHook(loadPackageParam.packageName, Display.class.getName(), loadPackageParam.classLoader, "getHeight", new Object[]{});
//        addHook(loadPackageParam.packageName, Resources.class.getName(), loadPackageParam.classLoader, "getDisplayMetrics", new Object[]{});

        addHook(loadPackageParam.packageName, NetworkInfo.class.getName(), loadPackageParam.classLoader, "getTypeName", new Object[]{});
        addHook(loadPackageParam.packageName, NetworkInfo.class.getName(), loadPackageParam.classLoader, "getType", new Object[]{});
        addHook(loadPackageParam.packageName, ActivityManager.class.getName(), loadPackageParam.classLoader, "getRunningAppProcesses", new Object[]{});
        addHook(loadPackageParam.packageName, "android.app.ApplicationPackageManager", loadPackageParam.classLoader, "getInstalledPackages", new Object[]{Integer.TYPE.getName()});
        addHook(loadPackageParam.packageName, "android.app.ApplicationPackageManager", loadPackageParam.classLoader, "getPackageInfo", new Object[]{String.class.getName(),Integer.TYPE.getName()});
        addHook(loadPackageParam.packageName, "android.app.ApplicationPackageManager", loadPackageParam.classLoader, "getApplicationInfo", new Object[]{String.class.getName(),Integer.TYPE.getName()});
        addHook(loadPackageParam.packageName, "android.app.ApplicationPackageManager", loadPackageParam.classLoader, "getInstalledApplications", new Object[]{Integer.TYPE.getName()});

        addHook(loadPackageParam.packageName, "android.os.SystemProperties", loadPackageParam.classLoader, "get", new Object[]{String.class.getName()});
        addHook(loadPackageParam.packageName, "android.content.ContextWrapper", loadPackageParam.classLoader, "getExternalCacheDir", new Object[]{});





        addHookConstructor(loadPackageParam.packageName, File.class.getName(), loadPackageParam.classLoader, new Object[]{String.class.getName()});
        addHookConstructor(loadPackageParam.packageName, File.class.getName(), loadPackageParam.classLoader, new Object[]{String.class.getName(), String.class.getName()});
        addHookConstructor(loadPackageParam.packageName, FileReader.class.getName(), loadPackageParam.classLoader, new Object[]{String.class.getName()});
        addHookConstructor(loadPackageParam.packageName, FileReader.class.getName(), loadPackageParam.classLoader, new Object[]{File.class.getName()});
//        new File()
    }

    private void setSystemData() {

        if(!TextUtils.isEmpty(XposeUtil.configMap.optString(XposeUtil.m_RELEASE))){
            XposedHelpers.setStaticObjectField(Build.VERSION.class,"RELEASE",XposeUtil.configMap.optString(XposeUtil.m_RELEASE));
        }
        if(!TextUtils.isEmpty(XposeUtil.configMap.optString(XposeUtil.m_SDK))){
//            XposedHelpers.setStaticObjectField(Build.VERSION.class, "SDK_INT", XposeUtil.configMap.optInt(XposeUtil.m_SDK));
            XposedHelpers.setStaticObjectField(Build.VERSION.class, "SDK", XposeUtil.configMap.optString(XposeUtil.m_SDK));
        }
        if(!TextUtils.isEmpty(XposeUtil.configMap.optString(XposeUtil.m_framework))){
            String[] split = XposeUtil.configMap.optString(XposeUtil.m_framework).split("_");
            if(split.length == 2){
                XposedHelpers.setStaticObjectField(Build.class,"CPU_ABI",split[0]);
                XposedHelpers.setStaticObjectField(Build.class,"CPU_ABI2",split[1]);
            }
        }
        if(!TextUtils.isEmpty(XposeUtil.configMap.optString(XposeUtil.m_brand))){
            XposedHelpers.setStaticObjectField(Build.class, "BRAND", XposeUtil.configMap.optString(XposeUtil.m_brand));
        }
        if(!TextUtils.isEmpty(XposeUtil.configMap.optString(XposeUtil.m_model))){
            XposedHelpers.setStaticObjectField(Build.class, "MODEL", XposeUtil.configMap.optString(XposeUtil.m_model));
        }
        if(!TextUtils.isEmpty(XposeUtil.configMap.optString(XposeUtil.m_product))){
            XposedHelpers.setStaticObjectField(Build.class, "PRODUCT", XposeUtil.configMap.optString(XposeUtil.m_product));
        }
        if(!TextUtils.isEmpty(XposeUtil.configMap.optString(XposeUtil.m_manufacture))){
            XposedHelpers.setStaticObjectField(Build.class, "MANUFACTURER", XposeUtil.configMap.optString(XposeUtil.m_manufacture));
        }
        if(!TextUtils.isEmpty(XposeUtil.configMap.optString(XposeUtil.m_hardware))){
            XposedHelpers.setStaticObjectField(Build.class, "HARDWARE", XposeUtil.configMap.optString(XposeUtil.m_hardware));
        }
        if(!TextUtils.isEmpty(XposeUtil.configMap.optString(XposeUtil.m_fingerprint))){
            XposedHelpers.setStaticObjectField(Build.class, "FINGERPRINT", XposeUtil.configMap.optString(XposeUtil.m_fingerprint));
        }
        if(!TextUtils.isEmpty(XposeUtil.configMap.optString(XposeUtil.m_serial))){
            XposedHelpers.setStaticObjectField(Build.class, "SERIAL", XposeUtil.configMap.optString(XposeUtil.m_serial));
        }

    }

    public void addHook(final String packageName, final String className,ClassLoader classLoader, final String methodName,Object[] parameterTypesAndCallback){

        XC_MethodHook xc_methodHook = new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if("getPackageInfo".equals(methodName) ){
                    if(param.args[0].equals(XposeUtil.pkg1) || param.args[0].equals(XposeUtil.pkg2) || param.args[0].equals(XposeUtil.pkg3)){
                        param.args[0] = "yyyy.mmmm.aaaa.xxxx";
                    }
                }else
                if("getApplicationInfo".equals(methodName) ){
                    if(param.args[0].equals(XposeUtil.pkg1) || param.args[0].equals(XposeUtil.pkg2) || param.args[0].equals(XposeUtil.pkg3)){
                        param.args[0] = "yyyy.mmmm.aaaa.xxxx";
                    }
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                if("getTypeName".equals(methodName) || "getType".equals(methodName)){
                    try{
                        String deviceid = XposeUtil.configMap.optString(XposeUtil.m_deviceId);
                        if(!TextUtils.isEmpty(deviceid)){
                            double anInt = Double.parseDouble(deviceid);
                            if(anInt % 2 == 0){
                                if("getTypeName".equals(methodName))
                                    param.setResult("MOBILE");
                                else
                                    param.setResult(ConnectivityManager.TYPE_MOBILE);
                            }
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        L.log(e.getMessage());
                    }
                }else

                if("getExternalCacheDir".equals(methodName) ){

                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"xpdownload");
                    param.setResult(file);

                }else

                if("get".equals(methodName) && className.equals("android.os.SystemProperties")){
//                    L.log("android.os.SystemProperties");
                    if(param.args[0].equals("ro.serialno")){
//                        L.log("android.os.SystemProperties获取序列号");
                        String serial = XposeUtil.configMap.optString(XposeUtil.m_serial);
                        if(!TextUtils.isEmpty(serial)){
                            param.setResult(serial);
                        }
                    }
                }else

//
                if("getInstalledApplications".equals(methodName) ){
                    List<ApplicationInfo> installedApplications = (List<ApplicationInfo>) param.getResult();
                    for (int i = installedApplications.size() - 1; i >= 0 ; i--) {
                        ApplicationInfo applicationInfo = installedApplications.get(i);
                        if(applicationInfo.equals(XposeUtil.pkg1) || applicationInfo.equals(XposeUtil.pkg2) || applicationInfo.equals(XposeUtil.pkg3)){
                            installedApplications.remove(i);
                        }
                    }
                    param.setResult(installedApplications);
                }else
                //
                if("getRunningAppProcesses".equals(methodName) ){
                    List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = (List<ActivityManager.RunningAppProcessInfo>) param.getResult();
                    for (int i = runningAppProcesses.size() - 1; i >= 0; i--) {
                        ActivityManager.RunningAppProcessInfo runningAppProcessInfo = runningAppProcesses.get(i);
                        if(runningAppProcessInfo.processName.equals(XposeUtil.pkg1) || runningAppProcessInfo.processName.equals(XposeUtil.pkg2) || runningAppProcessInfo.processName.equals(XposeUtil.pkg3)){
                            L.debug("getRunningAppProcesses+移除"+runningAppProcessInfo   );
                            runningAppProcesses.remove(i);
                        }
                    }
                    param.setResult(runningAppProcesses);
                }else
                if("getInstalledPackages".equals(methodName) ){
                    List<PackageInfo> installedPackages = (List<PackageInfo>) param.getResult();
                    for (int i = installedPackages.size() - 1; i >= 0; i--) {
                        String s = installedPackages.get(i).packageName;
                        if(s.equals(XposeUtil.pkg1) || s.equals(XposeUtil.pkg2) || s.equals(XposeUtil.pkg3)){
                            L.debug("getInstalledPackages+移除"+s);
                            installedPackages.remove(i);
                        }
                    }
                    param.setResult(installedPackages);
                }else

//                //屏幕大小
//                if("getWidth".equals(methodName) && className.equals(Display.class.getName())){
//                    String[] split = XposeUtil.configMap.optString(XposeUtil.m_screenSize).split("x");
//                    if(split.length == 2){
//                        param.setResult(Integer.parseInt(split[0]));
//                    }
//                }else
//                //屏幕大小
//                if("getHeight".equals(methodName) && className.equals(Display.class.getName())){
//                    String[] split = XposeUtil.configMap.optString(XposeUtil.m_screenSize).split("x");
//                    if(split.length == 2){
//                        param.setResult(Integer.parseInt(split[1]));
//                    }
//                }else
                //屏幕大小
//                if("getDisplayMetrics".equals(methodName)){
//                    String[] split = XposeUtil.configMap.optString(XposeUtil.m_screenSize).split("x");
//                    if(split.length == 2){
//                        DisplayMetrics displayMetrics = (DisplayMetrics) param.getResult();
//                        displayMetrics.heightPixels = Integer.parseInt(split[0]);
//                        displayMetrics.widthPixels = Integer.parseInt(split[1]);
//                        param.setResult(displayMetrics);
//                    }
//                }else
//                //屏幕大小
//                if("getMetrics".equals(methodName)){
//                    String[] split = XposeUtil.configMap.optString(XposeUtil.m_screenSize).split("x");
//                    if(split.length == 2){
//                        Object arg = param.args[0];
//                        ((DisplayMetrics)arg).heightPixels = Integer.parseInt(split[0]);
//                        ((DisplayMetrics)arg).widthPixels = Integer.parseInt(split[1]);
//                    }
//                }else
                //蓝牙地址
                if("getAddress".equals(methodName)){
//                    XposeUtil.initConfigMap();
                    String m_bluetoothaddress = XposeUtil.configMap.optString(XposeUtil.m_bluetoothaddress);
                    if(!TextUtils.isEmpty(m_bluetoothaddress)){
                        L.debug("修改m_bluetoothaddress");
                        param.setResult(m_bluetoothaddress);
                    }else{
                        L.debug("获取m_bluetoothaddress为空");
                    }
                }else
                //固件版本
                if("getRadioVersion".equals(methodName)){
//                    XposeUtil.initConfigMap();
                    String m_firmwareversion = XposeUtil.configMap.optString(XposeUtil.m_firmwareversion);
                    if(!TextUtils.isEmpty(m_firmwareversion)){
                        L.debug("修改m_firmwareversion");
                        param.setResult(m_firmwareversion);
                    }else{
                        L.debug("获取m_firmwareversion为空");
                    }
                }else
                //无线路由地址
                if("getBSSID".equals(methodName)){
//                    XposeUtil.initConfigMap();
                    String m_BSSID = XposeUtil.configMap.optString(XposeUtil.m_BSSID);
                    if(!TextUtils.isEmpty(m_BSSID)){
                        L.debug("修改m_BSSID");
                        param.setResult(m_BSSID);
                    }else{
                        L.debug("获取m_BSSID为空");
                    }
                }else
                //无线路由名
                if("getSSID".equals(methodName)){
//                    XposeUtil.initConfigMap();
                    String m_SSID = XposeUtil.configMap.optString(XposeUtil.m_SSID);
                    if(!TextUtils.isEmpty(m_SSID)){
                        L.debug("修改m_SSID");
                        param.setResult(m_SSID);
                    }else{
                        L.debug("获取m_SSID为空");
                    }
                }else
                //mac地址
                if("getMacAddress".equals(methodName)){
//                    XposeUtil.initConfigMap();
                    String m_macAddress = XposeUtil.configMap.optString(XposeUtil.m_macAddress);
                    if(!TextUtils.isEmpty(m_macAddress)){
                        L.debug("修改m_macAddress");
                        param.setResult(m_macAddress);
                    }else{
                        L.debug("获取m_macAddress为空");
                    }
                }else
                //手机卡状态
                if("getSimState".equals(methodName)){
//                    XposeUtil.initConfigMap();
                    int m_simState = XposeUtil.configMap.optInt(XposeUtil.m_simState, -1);
                    if(m_simState != -1)
                        param.setResult(5);

                }else
                //手机类型
                if("getPhoneType".equals(methodName)){
//                    XposeUtil.initConfigMap();
                    int m_phoneType = XposeUtil.configMap.optInt(XposeUtil.m_phoneType,-1);
                        if(m_phoneType != -1)
                        param.setResult(m_phoneType);

                }else
                //网络类型
                if("getNetworkType".equals(methodName)){

                    int m_networkType = XposeUtil.configMap.optInt(XposeUtil.m_networkType, -1);
                    if(m_networkType != -1)
                        param.setResult(m_networkType);

                }else
                //网络类型名
                if("getNetworkOperatorName".equals(methodName)){
//                    XposeUtil.initConfigMap();
                    String networkOperatorName = XposeUtil.configMap.optString(XposeUtil.m_networkOperatorName);
                    if(!TextUtils.isEmpty(networkOperatorName)){
                        L.debug("修改networkOperatorName");
                        param.setResult(networkOperatorName);
                    }else{
                        L.debug("获取networkOperatorName为空");
                    }
                }else
                //运营商
                if("getSimOperator".equals(methodName)){
//                    XposeUtil.initConfigMap();
                    String simOperator = XposeUtil.configMap.optString(XposeUtil.m_simOperator);
                    if(!TextUtils.isEmpty(simOperator)){
                        L.debug("修改simOperatord");
                        param.setResult(simOperator);
                    }else{
                        L.debug("获取simOperator为空");
                    }
                }else
                //IMSI
                if("getSubscriberId".equals(methodName)){
//                    XposeUtil.initConfigMap();
                    String subscriberId = XposeUtil.configMap.optString(XposeUtil.m_subscriberId);
                    if(!TextUtils.isEmpty(subscriberId)){
                        L.debug("修改subscriberId");
                        param.setResult(subscriberId);
                    }else{
                        L.debug("获取subscriberId为空");
                    }
                }else
                //手机卡序列号
                if("getSimSerialNumber".equals(methodName)){
//                    XposeUtil.initConfigMap();
                    String simSerialNumber = XposeUtil.configMap.optString(XposeUtil.m_simSerialNumber);
                    if(!TextUtils.isEmpty(simSerialNumber)){
                        L.debug("修改simSerialNumber");
                        param.setResult(simSerialNumber);
                    }else{
                        L.debug("获取simSerialNumber为空");
                    }
                }else
                //电话号码
                if("getLine1Number".equals(methodName)){
//                    XposeUtil.initConfigMap();
                    String phoneNum = XposeUtil.configMap.optString(XposeUtil.m_phoneNum);
                    if(!TextUtils.isEmpty(phoneNum)){
                        L.debug("修改phoneNum");
                        param.setResult(phoneNum);
                    }else{
                        L.debug("获取phoneNum为空");
                    }
                }else
                //android_id
                if("getString".equals(methodName) && param.args[1].equals("android_id")){
//                    XposeUtil.initConfigMap();
                    String androidId = XposeUtil.configMap.optString(XposeUtil.m_androidId);
                    if(!TextUtils.isEmpty(androidId)){
                        L.debug("修改androidId");
                        param.setResult(androidId);
                    }else{
                        L.debug("获取androidId为空");
                    }
                }else
                //device_id
                if("getDeviceId".equals(methodName)){
                    L.debug("packageName" + packageName + "configMap" + XposeUtil.configMap.toString());
                    XposeUtil.initConfigMap();
                    String deviceid = XposeUtil.configMap.optString(XposeUtil.m_deviceId);
                    if(!TextUtils.isEmpty(deviceid)){
                        L.debug("修改deviceid");
                        param.setResult(deviceid);
                    }else{
                        L.debug("获取deviceid为空");

                    }
                }else
                if("testXpose".equals(methodName)){
                    param.setResult(1);
                }

            }
        };


        Object [] param = new Object[parameterTypesAndCallback.length + 1];
        for (int i = 0; i < param.length; i++) {
            if(i == param.length-1){
                param[param.length - 1] = xc_methodHook;
                XposedHelpers.findAndHookMethod(className, classLoader, methodName, param);
                return ;
            }
            param[i] = parameterTypesAndCallback[i];
        }
    }


    public void addHookConstructor(final String packageName,String className,ClassLoader classLoader,Object[] parameterTypesAndCallback){

        XC_MethodHook xc_methodHook = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);


                if (XposeUtil.configMap.optBoolean(XposeUtil.FileRecordPackageNameSwitch) && XposeUtil.configMap.optString(XposeUtil.FileRecordPackageName).contains(packageName)) {
                    String attr = "";
                    if(param.args[0]instanceof File){
                        attr = ((File) param.args[0]).getAbsolutePath();
                        L.debug("attr--1--"+attr);
                    }else if(param.args.length > 1 && param.args[1] != null ){
                        String separator = "";
                        if(!param.args[0].toString().endsWith("/"))
                            separator = "/";
                        attr =  param.args[0].toString() + separator + param.args[1].toString();
                        L.debug("attr--2--"+param.args[0].toString()+"--"+ param.args[1].toString());
                    }else{
                        attr = (String) param.args[0];
                        L.debug("attr--3--"+attr);
                    }
                    if (attr.contains(RecordFileUtil.ExternalStorage) && !attr.contains("xpose")
                            && !(attr.startsWith(RecordFileUtil.ExternalStorage+RecordFileUtil.FILE_PATH_RECORD))
                                    && RecordFileUtil.addFileRecord(packageName, attr)) ;
                }
            }
        };

        Object [] param = new Object[parameterTypesAndCallback.length + 1];
        for (int i = 0; i < param.length; i++) {
            if(i == param.length-1){
                param[param.length - 1] = xc_methodHook;
                XposedHelpers.findAndHookConstructor(className,classLoader,param);
                return ;
            }
            param[i] = parameterTypesAndCallback[i];
        }



    }


}
