package com.meiriq.xposehook.bean.util;

import com.meiriq.xposehook.bean.DataInfo;

import org.json.JSONObject;

/**
 * Created by tian on 15-12-10.
 */
public class SetDataUtil {


    public static DataInfo parseJsonObject2DataInfo(JSONObject jsonObject){
        DataInfo dataInfo = new DataInfo();

        dataInfo.setDeviceId(jsonObject.optString("imei"));
        dataInfo.setAndroidId(jsonObject.optString("android_id"));
        dataInfo.setPhoneNum(jsonObject.optString("phone_number"));
        dataInfo.setSimId(jsonObject.optString("sim_card_id"));
        dataInfo.setImsi(jsonObject.optString("imsi"));
        dataInfo.setOperator(jsonObject.optString("operator"));
        dataInfo.setNetTypeName(jsonObject.optString("networks_type_name"));
        dataInfo.setNetType(jsonObject.optString("networks_type_id"));
        dataInfo.setPhoneType(jsonObject.optString("type"));
        dataInfo.setSimStatus(jsonObject.optString("status"));
        dataInfo.setMacAddress(jsonObject.optString("mac"));
        dataInfo.setRouteName(jsonObject.optString("wireless_router_name"));
        dataInfo.setRouteAddress(jsonObject.optString("wireless_router_address"));
        dataInfo.setSystemVersion(jsonObject.optString("android_version"));
        dataInfo.setSystemVersionValue(jsonObject.optString("system_value"));
        dataInfo.setSystemFramework(jsonObject.optString("system_architecture"));
        dataInfo.setScreenSize(jsonObject.optString("resolution"));
        dataInfo.setFirmwareVersion(jsonObject.optString("hardware_version"));
        dataInfo.setPhoneBrand(jsonObject.optString("brand"));
        dataInfo.setPhoneModelNumber(jsonObject.optString("model_number"));
        dataInfo.setProductName(jsonObject.optString("product_name"));
        dataInfo.setProductor(jsonObject.optString("manufacturer"));
        dataInfo.setEquipmentName(jsonObject.optString("device_number"));
        dataInfo.setCpu(jsonObject.optString("cpu"));
        dataInfo.setHardware(jsonObject.optString("hardware"));
        dataInfo.setFingerPrint(jsonObject.optString("fingerprint_key"));
        dataInfo.setPortNumber(jsonObject.optString("serial_interface_number"));
        dataInfo.setBluetoothAddress(jsonObject.optString("bluetooth_address"));
        dataInfo.setInternalIp(jsonObject.optString("local_area_networks_ip"));



        return dataInfo;
    }

}
