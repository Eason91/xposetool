package com.meiriq.xposehook;

import android.app.ProgressDialog;
import android.content.Context;

import com.meiriq.xposehook.bean.ApkInfo;
import com.meiriq.xposehook.bean.AppInfo;

import java.util.List;

/**
 * Created by tian on 15-11-27.
 */
public class BaseActivity extends ToolbarActivity{

    ProgressDialog progressDialog;

    public void showLoadingProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("加载中...");
        progressDialog.show();
    }

    public void dismissProgressDialog(){
        if(progressDialog!=null && progressDialog.isShowing())
            progressDialog.dismiss();
    }


    /**
     * 删除列表中在白名单中也有的数据
     * @param hadApks
     * @param databaseApks
     * @param delete
     */
    public void setApkTrueWhenLocalHave(List<ApkInfo> hadApks, List<ApkInfo> databaseApks,boolean delete){
        for (ApkInfo whiteAppInfo :
                databaseApks) {
            for (int i = hadApks.size()-1; i >= 0; i--) {

                if (hadApks.get(i).getName().equals(whiteAppInfo.getName())){
                    hadApks.get(i).setIsSelect(true);
                    if(delete)
                        hadApks.remove(i);
                    break;
                }
            }
        }
    }


    /**
     * 将本地app的一些记录添加到新获取的app列表中
     * @param installApps
     * @param databaseApps
     * @param delete 本地有，是否删除
     */
    public void setAppTrueWhenLocalHave(List<AppInfo> installApps, List<AppInfo> databaseApps,boolean delete){
        for (AppInfo whiteAppInfo :
                databaseApps) {
            for (int i = installApps.size()-1; i >= 0; i--) {

                if (installApps.get(i).getPname().equals(whiteAppInfo.getPname())){
                    installApps.get(i).setIsSelect(true);
                    if(delete)
                        installApps.remove(i);
                    break;
                }
            }
        }
    }

    /**
     * 将本地app的一些记录添加到新获取的app列表中
     * @param installApps
     * @param databaseApps
     */
    public void setAppTrueWhenLocalHave(List<AppInfo> installApps, List<AppInfo> databaseApps){
        setAppTrueWhenLocalHave(installApps,databaseApps,true);
    }
}
