/*
 * Copyright (c) 2016-present 贵州纳雍穿青人李裕江<1032694760@qq.com>
 *
 * The software is licensed under the Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *     http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 * PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.github.limoxiao.oaid.impl;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;

import com.github.limoxiao.oaid.IGetter;
import com.github.limoxiao.oaid.IOAID;
import com.github.limoxiao.oaid.OAIDException;
import com.github.limoxiao.oaid.OAIDLog;
import com.github.limoxiao.oaid.impl.hw.AdvertisingIdClient;
import com.github.limoxiao.oaid.impl.hw.InfoProviderUtil;

import repackage.com.uodis.opendevice.aidl.OpenDeviceIdentifierService;

/**
 * 参阅华为官方 HUAWEI Ads SDK。
 * <prev>
 * implementation `com.huawei.hms:ads-identifier:3.4.39.302`
 * AdvertisingIdClient.getAdvertisingIdInfo(context).getId()
 * </pre> *
 *
 * @author 大定府羡民（1032694760@qq.com）
 * @since 2020/5/30
 */
class HuaweiImpl implements IOAID {
    private final Context context;
    private String packageName;

    public HuaweiImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean supported() {
        if (context == null) {
            return false;
        }
        boolean ret = false;
        try {
            PackageManager pm = context.getPackageManager();
            if (pm.getPackageInfo("com.huawei.hwid", 0) != null) {
                packageName = "com.huawei.hwid";
                ret = true;
            } else if (pm.getPackageInfo("com.huawei.hwid.tv", 0) != null) {
                packageName = "com.huawei.hwid.tv";
                ret = true;
            } else {
                packageName = "com.huawei.hms";
                ret = pm.getPackageInfo(packageName, 0) != null;
            }
        } catch (Exception e) {
            OAIDLog.print(e);
        }
        return ret;
    }

    @Override
    public void doGet(final IGetter getter) {
        OAIDLog.print("doGet");
        if (context == null || getter == null) {
            OAIDLog.print("doGet context == null || getter == null");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                OAIDLog.print("doGet pps_oaid_c");
                String pps_oaid_c = Settings.Global.getString(context.getContentResolver(), "pps_oaid_c");
                OAIDLog.print("doGet pps_oaid_c :" + pps_oaid_c);
                if (!TextUtils.isEmpty(pps_oaid_c)) {
                    AdvertisingIdClient.Info info = InfoProviderUtil.getDeviceInfo(context);
                    if (null != info ) {
                        OAIDLog.print("doGet pps_oaid_c info oaid:"+info.getId() +" isLimitAdTrackingEnabled:"+info.isLimitAdTrackingEnabled());
                        if(!InfoProviderUtil.EMPTY_OAID.equalsIgnoreCase(info.getId())){
                            getter.onOAIDGetComplete(info.getId());
                            return;
                        }
                    }
                }

                String oaid = Settings.Global.getString(context.getContentResolver(), "pps_oaid");
                if (!TextUtils.isEmpty(oaid)) {
                    OAIDLog.print("doGet Get oaid from global settings: " + oaid);
                    getter.onOAIDGetComplete(oaid);
                    return;
                }
            } catch (Exception e) {
                OAIDLog.print(e);
            }
        }
        if (TextUtils.isEmpty(packageName) && !supported()) {
            OAIDLog.print("doGet Huawei Advertising ID not available");
            getter.onOAIDGetError(new OAIDException("Huawei Advertising ID not available"));
            return;
        }
        if (InfoProviderUtil.verifyUri(context) ){
            OAIDLog.print("doGet via provider");
            AdvertisingIdClient.Info info = InfoProviderUtil.queryInfo(context);
            if (null != info ) {
                OAIDLog.print("doGet  via provider info oaid:"+info.getId() +" isLimitAdTrackingEnabled:"+info.isLimitAdTrackingEnabled());
                if(!InfoProviderUtil.EMPTY_OAID.equalsIgnoreCase(info.getId())){
                    getter.onOAIDGetComplete(info.getId());
                    return;
                }
            }
        }
        Intent intent = new Intent("com.uodis.opendevice.OPENIDS_SERVICE");
        intent.setPackage(packageName);
        OAIDService.bind(context, intent, getter, new OAIDService.RemoteCaller() {
            @Override
            public String callRemoteInterface(IBinder service) throws OAIDException, RemoteException {
                OpenDeviceIdentifierService anInterface = OpenDeviceIdentifierService.Stub.asInterface(service);
                if (anInterface.isOaidTrackLimited()) {
                    // 实测在系统设置中关闭了广告标识符，将获取到固定的一大堆0
                    throw new OAIDException("User has disabled advertising identifier");
                }
                return anInterface.getOaid();
            }
        });
    }

}
