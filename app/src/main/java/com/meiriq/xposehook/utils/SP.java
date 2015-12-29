package com.meiriq.xposehook.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tian on 15-12-15.
 */
public class SP {

    private static final String XPOSE = "xpose";

    public static final String KEY_CHANNEL = "channel";
    public static final String KEY_DAY = "day";
    public static final String KEY_HOUR = "hour";
    public static final String KEY_HOUR_TO = "hourto";
    public static final String KEY_MINUTE = "minute";
    public static final String KEY_MINUTE_TO = "minuteto";

    private static SharedPreferences preferences;
    public static void init(Context context){
        if(preferences == null)
            preferences = context.getSharedPreferences(XPOSE, Context.MODE_PRIVATE);

    }

    public static int getInt(Context context,String key){
        init(context);
        return preferences.getInt(key,0);
    }

    public static void set(String key,Object value){
        if(value instanceof Integer){
            preferences.edit().putInt(key, (Integer) value).commit();
        }else if(value instanceof String){
            preferences.edit().putString(key, (String) value).commit();
        }

    }
}
