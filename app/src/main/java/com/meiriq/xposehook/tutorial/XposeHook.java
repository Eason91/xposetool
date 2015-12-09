package com.meiriq.xposehook.tutorial;

import android.os.*;
import android.telephony.TelephonyManager;

import com.meiriq.xposehook.utils.L;
import com.meiriq.xposehook.utils.RecordFileUtil;
import com.meiriq.xposehook.utils.XposeUtil;

import java.io.File;
import java.io.FileReader;

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
            return ;
        }


        XposeUtil.initConfigMap();
        L.debug("初始花" + loadPackageParam.packageName);
        if(RecordFileUtil.ExternalStorage.length() == 0){
            RecordFileUtil.ExternalStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        addHook(loadPackageParam.packageName, TelephonyManager.class.getName(), loadPackageParam.classLoader, "getDeviceId", new Object[]{});
        addHookConstructor(loadPackageParam.packageName, File.class.getName(), loadPackageParam.classLoader, new Object[]{String.class.getName()});
        addHookConstructor(loadPackageParam.packageName, File.class.getName(), loadPackageParam.classLoader, new Object[]{String.class.getName(), String.class.getName()});
        addHookConstructor(loadPackageParam.packageName, FileReader.class.getName(), loadPackageParam.classLoader, new Object[]{String.class.getName()});
        addHookConstructor(loadPackageParam.packageName, FileReader.class.getName(), loadPackageParam.classLoader, new Object[]{File.class.getName()});
//        new File()
    }

    public void addHook(final String packageName, final String className,ClassLoader classLoader, final String methodName,Object[] parameterTypesAndCallback){

        XC_MethodHook xc_methodHook = new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if("getDeviceId".equals(methodName)){
                    XposeUtil.initConfigMap();
                    String deviceid = XposeUtil.configMap.optString(XposeUtil.deviceId);
                    if(deviceid != null){
                        L.debug("修改deviceid");
                        param.setResult(deviceid);
                    }else{
                        L.debug("获取deviceid为空");
                    }
                }
            }
        };


        Object [] param = new Object[parameterTypesAndCallback.length + 1];
        for (int i = 0; i < param.length; i++) {
            if(i == param.length-1){
                param[param.length - 1] = xc_methodHook;
                XposedHelpers.findAndHookMethod(className, classLoader, methodName,param);
                L.debug("findAndHookMethod" );
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
                    }else if(param.args[1] != null ){
                        String separator = "";
                        if(!param.args[0].toString().endsWith("/"))
                            separator = "/";
                        attr =  param.args[0].toString() + separator + param.args[1].toString();
                        L.debug("attr--2--"+param.args[0].toString()+"--"+ param.args[1].toString());
                    }else{
                        attr = (String) param.args[0];
                        L.debug("attr--3--"+attr);
                    }
                    if (attr.contains(RecordFileUtil.ExternalStorage)
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
