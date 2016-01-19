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
import java.util.ArrayList;
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

    public static void addWhiteFileRecord(ArrayList<String> records){
        for (int i = 0; i < records.size(); i++) {
            addWhiteFileRecord(records.get(i));
        }
    }

    public static void addWhiteFileRecord(String record){
        addFileRecord("white", record,true);
    }

    public static void addWhiteFolderRecord(ArrayList<String> records){
        for (int i = 0; i < records.size(); i++) {
            addWhiteFolderRecord(records.get(i));
        }
    }

    public static void addWhiteFolderRecord(String record){
        addFileRecord("whitefolder", record,true);
    }

    public static boolean addFileRecord(String packageName,String record,boolean isWhite){
        L.debug("添加记录" + packageName + "--" + record);

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
        if(!isWhite){
            if(fileMap == null){
                getFileRecord(packageName);
            }

            if(fileMap.get(record)!=null){
                L.debug("已经记录了文件，不需要重复");
                return true;
            }
        }


        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(localFile,true)));
            bw.append(record+'\n');
            if(!isWhite)
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

    public static boolean addFileRecord(String packageName,String record){
        return addFileRecord(packageName,record,false);
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
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            while ((message = reader.readLine())!=null){
                fileMap.put(message, message);
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

    public static ArrayList<String> getWhiteFileRecord(){
        File file = new File(Environment.getExternalStorageDirectory() + FILE_PATH_RECORD + File.separator + "white");
        return getStringsFromFile(file);
    }

    private static ArrayList<String> getStringsFromFile( File file) {
        String message;
        ArrayList<String > records = new ArrayList<>();
        BufferedReader reader = null;
        if(!file.exists())
            return records;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            while ((message = reader.readLine())!=null){
                records.add(message);
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
        return records;
    }

    public static ArrayList<String> getWhiteFolderFileRecord(){
        File file = new File(Environment.getExternalStorageDirectory() + FILE_PATH_RECORD + File.separator + "whitefolder");
        return getStringsFromFile(file);
    }



    public static boolean clearFileRecord(String packageName){
        clearFileMap();
        File file = new File(Environment.getExternalStorageDirectory()+ FILE_PATH_RECORD + File.separator + packageName);
        L.debug(file.exists()+file.getAbsolutePath());
        if(file.exists()){
            return file.delete();
        }
        return false;
    }

    public static void clearFileMap(){
        if(fileMap != null){
            fileMap.clear();
        }
    }

    public static boolean deleteWhiteFile(){
        File file = new File(Environment.getExternalStorageDirectory() + FILE_PATH_RECORD + File.separator + "white");
        if(file.exists()){
            return file.delete();
        }
        return false;
    }

    public static boolean deleteWhiteFolderFile(){
        File file = new File(Environment.getExternalStorageDirectory() + FILE_PATH_RECORD + File.separator + "whitefolder");
        if(file.exists()){
            return file.delete();
        }
        return false;
    }

    /**
     * 删除除了白名单的所有记录文件
     */
    public static void deleteFile(){
        Iterator<Map.Entry<String, String>> iterator = fileMap.entrySet().iterator();
        ArrayList whiteFileRecord = getWhiteFileRecord();
        ArrayList<String> whiteFolderFileRecord = getWhiteFolderFileRecord();
        while (iterator.hasNext()){
            Map.Entry<String, String> entry = iterator.next();
            File file = new File(entry.getKey());

            if(file.exists() && file.isFile()){
                String absolutePath = file.getAbsolutePath();
                for (int i = 0; i < whiteFolderFileRecord.size(); i++) {
                    //白名单文件夹和白名单文件
                    if(absolutePath.contains(whiteFolderFileRecord.get(i)) || whiteFileRecord.contains(absolutePath)){
                        break;
                    }
                    //循环到最后一个白名单文件列表项，说明数据可以删除
                    if(i == whiteFolderFileRecord.size() - 1){
                        boolean delete = file.delete();
                        L.debug("文件删除"+delete+absolutePath+"==="+whiteFolderFileRecord.get(i));
                    }
                }
            }
        }
    }


}
