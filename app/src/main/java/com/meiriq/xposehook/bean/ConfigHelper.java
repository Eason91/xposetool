package com.meiriq.xposehook.bean;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 配置工具类
 * Created by tian on 15-12-2.
 */
public class ConfigHelper {

    private static ConfigHelper configHelper;
    private static final String CONFIG = "config";
    private static final String DATAINFO = "datainfo";
    private static final String CHANNEL = "channel";
    private static Config config ;

    private ConfigHelper(){};

    public ConfigHelper getInstance(){
        if(configHelper == null){
            configHelper = new ConfigHelper();
        }

        return configHelper;
    }

    public static Config initConfig(Context context){
        if(config == null){
            config = loadConfig(context);
            if(config == null){
                config = new Config();
            }
        }
        return config;
    }

    public static Config getConfig(){

        return config;
    }

    public static void saveConfig(Context context,Config config){
        File file = new File(context.getFilesDir(),CONFIG);
        if(file.exists()){
            file.delete();
        }
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(context.openFileOutput(CONFIG,Context.MODE_PRIVATE));
            outputStream.writeObject(config);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Config loadConfig(Context context){
        Config config = null;
        File file = new File(context.getFilesDir(),CONFIG);
        if(file.exists()){
            try {
                ObjectInputStream inputStream = new ObjectInputStream(context.openFileInput(CONFIG));
                config = (Config) inputStream.readObject();
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

        return config;
    }

    public static void saveDataInfo(Context context,DataInfo dataInfo){
        File file = new File(context.getFilesDir(),DATAINFO);
        if(file.exists()){
            file.delete();
        }
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(context.openFileOutput(DATAINFO,Context.MODE_PRIVATE));
            outputStream.writeObject(dataInfo);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DataInfo loadDataInfo(Context context){
        DataInfo dataInfo = null;
        File file = new File(context.getFilesDir(),DATAINFO);
        if(file.exists()){
            try {
                ObjectInputStream inputStream = new ObjectInputStream(context.openFileInput(DATAINFO));
                dataInfo = (DataInfo) inputStream.readObject();
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        if(dataInfo == null)
            dataInfo = new DataInfo();

        return dataInfo;
    }

    public static void saveChannel(Context context,List<Channel>channels){
        File file = new File(context.getFilesDir(), CHANNEL);
        if(file.exists()){
            file.delete();
        }
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(context.openFileOutput(CHANNEL,Context.MODE_PRIVATE));
            outputStream.writeObject(channels);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Channel> loadChannel(Context context){
        List<Channel>channels = null;
        File file = new File(context.getFilesDir(),CHANNEL);
        if(file.exists()){
            try {
                ObjectInputStream inputStream = new ObjectInputStream(context.openFileInput(CHANNEL));
                channels = (List<Channel>) inputStream.readObject();
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
//        if(channels == null)
//            channels = new ArrayList<>();

        return channels;
    }

}
