package com.meiriq.xposehook.bean;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * Created by tian on 16-2-2.
 */
public class SetDay implements Serializable{
    private static final String SETDAY = "set_day";

    private String day2 = "";
    private String day3 = "";
    private String day4 = "";
    private String day5 = "";
    private String day6 = "";
    private String day7 = "";
    private String day8 = "";
    private String day9 = "";
    private String day10 = "";

    private int channel = 0;
    private int day = 0;
    private int hour = 0;
    private int hourto = 0;
    private int minute = 0;
    private int minuteto = 0;

    public SetDay() {
    }

    public SetDay(String day2, String day3, String day4, String day5, String day6, String day7, String day8, String day9, String day10, int channel, int day, int hour, int hourto, int minute, int minuteto) {
        this.day2 = day2;
        this.day3 = day3;
        this.day4 = day4;
        this.day5 = day5;
        this.day6 = day6;
        this.day7 = day7;
        this.day8 = day8;
        this.day9 = day9;
        this.day10 = day10;
        this.channel = channel;
        this.day = day;
        this.hour = hour;
        this.hourto = hourto;
        this.minute = minute;
        this.minuteto = minuteto;
    }

    private static File getFilePath(String filePath){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),ConfigHelper.FILE_PATH);
        if(!file.exists())
            file.mkdirs();
        file = new File(file,File.separator+filePath);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }


    public static SetDay loadSetDay(Context context){
        SetDay setDays = null;
        File file = getFilePath(SETDAY);
        if(file.exists()){
            try {
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(getFilePath(SETDAY)));
                setDays = (SetDay) inputStream.readObject();
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        if(setDays == null){
            return new SetDay();
        }

        return setDays;
    }

    public static void saveSetDay(Context context,SetDay setDays){
        File file = getFilePath(SETDAY);
        if(file.exists()){
            file.delete();
        }
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(getFilePath(SETDAY)));
            outputStream.writeObject(setDays);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDay2() {
        return day2;
    }

    public void setDay2(String day2) {
        this.day2 = day2;
    }

    public String getDay3() {
        return day3;
    }

    public void setDay3(String day3) {
        this.day3 = day3;
    }

    public String getDay4() {
        return day4;
    }

    public void setDay4(String day4) {
        this.day4 = day4;
    }

    public String getDay5() {
        return day5;
    }

    public void setDay5(String day5) {
        this.day5 = day5;
    }

    public String getDay6() {
        return day6;
    }

    public void setDay6(String day6) {
        this.day6 = day6;
    }

    public String getDay7() {
        return day7;
    }

    public void setDay7(String day7) {
        this.day7 = day7;
    }

    public String getDay8() {
        return day8;
    }

    public void setDay8(String day8) {
        this.day8 = day8;
    }

    public String getDay9() {
        return day9;
    }

    public void setDay9(String day9) {
        this.day9 = day9;
    }

    public String getDay10() {
        return day10;
    }

    public void setDay10(String day10) {
        this.day10 = day10;
    }

    public static String getSETDAY() {
        return SETDAY;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getHourto() {
        return hourto;
    }

    public void setHourto(int hourto) {
        this.hourto = hourto;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getMinuteto() {
        return minuteto;
    }

    public void setMinuteto(int minuteto) {
        this.minuteto = minuteto;
    }
}
