package com.meiriq.xposehook.bean;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * 留存类
 * Created by tian on 16-1-19.
 */
public class DataKeepStatus implements Serializable{

    private static final String DATASTATUS = "data_status";

    private int keepCount;//该留存天数下可以获取的数据数量
    private String keepTime;//最后时间timeout
    private int keepDayStatus;//留存天数

    private String useDay;


    public String getUseDay() {
        return useDay;
    }

    public void setUseDay(String useDay) {
        this.useDay = useDay;
    }

    public static List<DataKeepStatus> loadDataKeepStatus(Context context){
        List<DataKeepStatus> dataKeepStatus = null;
        File file = new File(context.getFilesDir(),DATASTATUS);
        if(file.exists()){
            try {
                ObjectInputStream inputStream = new ObjectInputStream(context.openFileInput(DATASTATUS));
                dataKeepStatus = (List<DataKeepStatus>) inputStream.readObject();
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

        return dataKeepStatus;
    }

    public static void saveDataKeepStatus(Context context,List<DataKeepStatus> loadDataKeepStatus){
        File file = new File(context.getFilesDir(),DATASTATUS);
        if(file.exists()){
            file.delete();
        }
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(context.openFileOutput(DATASTATUS,Context.MODE_PRIVATE));
            outputStream.writeObject(loadDataKeepStatus);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getKeepCount() {
        return keepCount;
    }

    public void setKeepCount(int keepCount) {
        this.keepCount = keepCount;
    }

    public String getKeepTime() {
        return keepTime;
    }

    public void setKeepTime(String keepTime) {
        this.keepTime = keepTime;
    }

    public int getKeepDayStatus() {
        return keepDayStatus;
    }

    public void setKeepDayStatus(int keepDayStatus) {
        this.keepDayStatus = keepDayStatus;
    }

    @Override
    public String toString() {
        return "DataKeepStatus{" +
                "keepCount=" + keepCount +
                ", keepTime='" + keepTime + '\'' +
                ", keepDayStatus=" + keepDayStatus +
                ", useDay='" + useDay + '\'' +
                '}';
    }
}
