package com.meiriq.xposehook.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * Created by tian on 15-12-10.
 */
public class DialogUtil {

    public static void showOkDialog(Context context,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.support.v7.appcompat.R.style.Base_Theme_AppCompat_Light_Dialog_Alert);
        builder.setMessage(message);
        builder.setPositiveButton("确定",null);
        builder.show();
    }


}
