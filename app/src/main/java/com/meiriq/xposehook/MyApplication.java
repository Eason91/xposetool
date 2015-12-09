package com.meiriq.xposehook;

import android.app.Application;

import com.meiriq.xposehook.bean.Config;
import com.meiriq.xposehook.bean.ConfigHelper;
import com.meiriq.xposehook.utils.L;

/**
 * Created by tian on 15-12-2.
 */
public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
         ConfigHelper.initConfig(this);
    }
}
