package com.meiriq.xposehook.tutorial;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.meiriq.xposehook.bean.AppInfo;
import com.meiriq.xposehook.utils.L;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by tian on 15-11-26.
 */
public class AppUtils {
    public final static int APP_TYPE_ALL = 0;
    public final static int APP_TYPE_CUSTOM = 1;

    private AppUtils(){};
    private static AppUtils appUtils;
    private static ExecutorService executorService;

    public static synchronized AppUtils getInstance(){
        if(appUtils == null){
            appUtils = new AppUtils();
            executorService = Executors.newFixedThreadPool(2);
        }
        return appUtils;
    }

    public boolean killAppByPackageName(Context context,AppInfo appInfo){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        for (int i = 0; i < runningAppProcesses.size(); i++) {
            String[] pkgList = runningAppProcesses.get(i).pkgList;
            for (int ij = 0; ij < pkgList.length; ij++) {
                if(pkgList[ij].equals(appInfo.getPname())){

                    Shell.execCommand("kill " + runningAppProcesses.get(i).pid,true);
                    activityManager.killBackgroundProcesses(appInfo.getPname());
                    return true;
                }
            }
        }
        return false;
    }

    public void clearData(final List<AppInfo> appInfos, final Context context, final View view){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < appInfos.size(); i++) {
                    final AppInfo appInfo = appInfos.get(i);
                    if(appInfo.isSelect()){
                        Shell.execCommand("pm clear " + appInfo.getPname(), true);
                        killAppByPackageName(context, appInfo);
                        SystemClock.sleep(300);
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast(context, null, "清除" + appInfo.getAppname() + "完成");
                            }
                        });
                    }
                    if(i == appInfos.size()-1){
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast(context,null,"清除完成");
                                if(callBack != null)
                                    callBack.unInstall();
                            }
                        });
                    }
                }
            }
        });
    }

    public void uninstall(final List<AppInfo> appInfos, final Context context, final View view){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < appInfos.size(); i++) {
                    final AppInfo appInfo = appInfos.get(i);
                    if(appInfo.isSelect()){
                        Shell.execCommand("pm uninstall " + appInfo.getPname(), true);

                        try {
                            int index = 0;
                            while(true){
                                ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(appInfo.getPname(), 128);
                                L.debug("applicationInfo"+(applicationInfo==null));
                                if(applicationInfo == null){
                                    ((Activity)context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showToast(context,view,"卸载" + appInfo.getPname() + "成功");
                                        }
                                    });
                                    break;
                                }
                                if(index>10)
                                    break;
                                index ++;
                                Thread.sleep(500L);
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToast(context,view,"卸载" + appInfo.getAppname() + "成功");
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(i == appInfos.size()-1){
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast(context,null,"卸载完成");
                                if(callBack != null)
                                    callBack.unInstall();
                            }
                        });
                    }
                }
            }
        });
    }

    private static void showToast(Context context,View view,String message){
        if(view != null){
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                    .show();
        }else{
            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 获取安装的程序
     * @param context
     * @param type APP_TYPE_ALL 全部程序，APP_TYPE_CUSTOM　非预装程序
     * @return
     */
    public List<AppInfo> getInstallApps(Context context,int type){
        List<AppInfo> appList = new ArrayList();
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if(packageInfo.packageName.equals("com.meiriq.xposehook")
                    || packageInfo.packageName.equals("de.robv.android.xpose.installer")
                    || packageInfo.packageName.equals("pro.burgerz.wsm.manager")){
                continue;
            }
            if(type == APP_TYPE_CUSTOM){
                if((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0){
                    //非系统应用
                    continue;
                }
            }else if(type == APP_TYPE_ALL){
                //所有应用
            }
            AppInfo appInfo = new AppInfo();
            appInfo.setAppname(packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString()) ;
            appInfo.setPname(packageInfo.packageName);
            appInfo.setVersionName(packageInfo.versionName);
            appInfo.setVersionCode(packageInfo.versionCode);
            appList.add(appInfo);
        }


        return appList;

    }



    public interface CallBack{
        void unInstall();
    }

    CallBack callBack;
    public void setCallBackListener(CallBack callBackListener){
        this.callBack = callBackListener;
    }

}
