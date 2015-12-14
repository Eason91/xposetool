package com.meiriq.xposehook.net.control;

import android.content.Context;

import com.android.volley.VolleyError;
import com.meiriq.xposehook.bean.Constant;
import com.meiriq.xposehook.bean.util.SetDataUtil;
import com.meiriq.xposehook.net.BaseService;
import com.meiriq.xposehook.net.VolleyErrorHandler;
import com.meiriq.xposehook.net.VolleyListener;
import com.meiriq.xposehook.utils.DateUtil;
import com.meiriq.xposehook.utils.L;

import org.json.JSONObject;

/**
 * Created by tian on 15-12-8.
 */
public class DataService extends BaseService{
    public DataService(Context context) {
        super(context);
    }


    public void getSetDateNew(String channel){
        mCallback.onStart();
        String param = "used=" + channel ;
        String url = Constant.url_getDataRandom + "?" + param;
        L.debug("随机"+url);
        getStringRequest(url, null, new VolleyListener() {
            @Override
            public void onComplete(JSONObject jsonObject) {
                if (jsonObject.optString("data").equals("null")) {
                    mCallback.onSuccess(null);
                } else {
                    mCallback.onSuccess(SetDataUtil.parseJsonObject2DataInfo(jsonObject.optJSONObject("data")));
                }
                L.debug("----" + jsonObject.toString());
            }

            @Override
            public void onError(VolleyError error) {
                mCallback.onError(VolleyErrorHandler.wrap2ErrorObject(error));
            }
        });
    }

    /**
     * 获取数据
     * @param channel　渠道
     * @param diff 时间差，0为今天,-1为昨天
     */
    public void getSetDataUsed(String channel, int diff) {
        mCallback.onStart();

        String param = "used=" + channel + "&beginDate=" + DateUtil.getDateTime(diff,DateUtil.DAYTIMR_START_IN_DAY)
                    + "&endDate=" + DateUtil.getDateTime(diff,DateUtil.DAYTIMR_END_IN_DAY);
        String url = Constant.url_getDataUsed + "?" + param;
        L.debug("用过" +"---"+url);
        getStringRequest(url, null, new VolleyListener() {
            @Override
            public void onComplete(JSONObject jsonObject) {
                if (jsonObject.optString("data").equals("null")) {
                    mCallback.onSuccess(null);
                } else {
                    mCallback.onSuccess(SetDataUtil.parseJsonObject2DataInfo(jsonObject.optJSONObject("data")));
                }
                L.debug("----" + jsonObject.toString());
            }

            @Override
            public void onError(VolleyError error) {
                mCallback.onError(VolleyErrorHandler.wrap2ErrorObject(error));
            }
        });


    }
}
