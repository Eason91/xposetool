package com.meiriq.xposehook.utils;

import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by tian on 15-12-7.
 */
public class XposeUtil {
    public static String FILE_PATH_XPOSR = "/.xpose/";
    public static String FileRecordPackageName = "FileRecordPackageName";
    public static String FileRecordPackageNameSwitch = "FileRecordPackageNameSwitch";
    public static String deviceId = "deviceId";//设备id

    public static JSONObject configMap = new JSONObject();

    public static void saveConfigMap(){

        new Thread(){
            @Override
            public void run() {
                saveFileData("xposeDevice.txt",configMap.toString());
            }
        }.start();
    }

    public static void initConfigMap(){
        try {
            configMap = new JSONObject(getFileData("xposeDevice.txt"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void saveFileData(String fileName,String value){
        File localFile = new File(Environment.getExternalStorageDirectory()+FILE_PATH_XPOSR,fileName);
        if(!localFile.exists()){
            try {
                localFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        L.debug("写入文本"+value);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(localFile);
            fos.write(value.getBytes("UTF-8"));
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (fos != null)
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    private static String getFileData(String fileName){

        File localFile = new File(Environment.getExternalStorageDirectory()+FILE_PATH_XPOSR,fileName);
        if(!localFile.exists()){
            try {
                localFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        BufferedReader reader = null;
        String result = "";
        try {
            reader = new BufferedReader(new FileReader(localFile));
            result = reader.readLine();
            if(result == null)
                return "";
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return result;
    }



}
