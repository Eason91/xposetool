package com.meiriq.xposehook.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by tian on 15-12-11.
 */
public class DateUtil {

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static int DAYTIMR_NO_TIME = 0x1<<1;
    public static int DAYTIMR_START_IN_DAY = 0x1<<2;
    public static int DAYTIMR_END_IN_DAY = 0x1<<3;

    /**
     * 获取日期
     * @param diff 0表示今天，1表示昨天
     *             tag 标示是开始时间还是结束时间
     * @return
     */
    public static String getDateTime(int diff,int tag){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - diff);
        if(tag == DAYTIMR_START_IN_DAY){
            calendar.set(Calendar.HOUR_OF_DAY,0);
        }else if(tag == DAYTIMR_END_IN_DAY){
            calendar.set(Calendar.HOUR_OF_DAY,23);
        }
        return format.format(calendar.getTime());
    }
}
