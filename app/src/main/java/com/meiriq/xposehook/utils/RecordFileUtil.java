package com.meiriq.xposehook.utils;

import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by tian on 15-12-2.
 */
public class RecordFileUtil {
    public static String ExternalStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static String FILE_PATH_RECORD = "/.xpose/record";
    public static HashMap<String,String> fileMap = new HashMap<>();
    public static boolean isRecord = true;

    public static boolean addFileRecord(String packageName,String record){
        L.debug("添加记录"+packageName+"--"+record);

        if(TextUtils.isEmpty(record))
            return false;

        File directory = Environment.getExternalStorageDirectory();

        File localFile = new File(directory+ FILE_PATH_RECORD);
        if(!localFile.exists())
            localFile.mkdirs();
        localFile = new File(localFile+File.separator+packageName);
        if(!localFile.exists()){
            try {
                localFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(fileMap == null){
            getFileRecord(packageName);
        }

        if(fileMap.get(record)!=null){
            L.debug("已经记录了文件，不需要重复");
            return true;
        }

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(localFile,true)));
            bw.append(record+'\n');
            fileMap.put(record,record);
            bw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(bw != null){
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public static void getFileRecord(String packageName){
        if(fileMap == null){
            fileMap = new HashMap<>();
        }
        String message = "";
        BufferedReader reader = null;
        File file = new File(Environment.getExternalStorageDirectory() + FILE_PATH_RECORD + File.separator + packageName);
        L.debug(file.exists()+file.getAbsolutePath());
        if(!file.exists())
            return ;
        try {
            L.debug("开始读书节");
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            while ((message = reader.readLine())!=null){
                L.debug("message---"+message);
                if(fileMap.get(message) == null){
                    fileMap.put(message, message);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    public static boolean clearFileRecord(String packageName){
        if(fileMap != null){
            fileMap.clear();
        }
        File file = new File(Environment.getExternalStorageDirectory()+ FILE_PATH_RECORD + File.separator + packageName);
        L.debug(file.exists()+file.getAbsolutePath());
        if(file.exists()){
            return file.delete();
        }
        return false;
    }

    public static void deleteFile(){
        Iterator<Map.Entry<String, String>> iterator = fileMap.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, String> entry = iterator.next();
            File file = new File(entry.getKey());
            if(file.exists() && file.isFile()){

                boolean delete = file.delete();
                L.debug("文件删除"+delete+file.getAbsolutePath());
            }
        }
    }


}
