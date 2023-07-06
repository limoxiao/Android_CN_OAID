//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.github.limoxiao.oaid.impl.hw;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.os.RemoteException;
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import repackage.com.uodis.opendevice.aidl.OpenDeviceIdentifierService;


public class AdvertisingIdClient {
    private static final String TAG = "AdvertisingIdClient";
    private static final String SETTINGS_AD_ID = "pps_oaid";
    private static final String SETTINGS_AD_ID_C = "pps_oaid_c";
    private static final String SETTINGS_TRACK_LIMIT = "pps_track_limit";

    public AdvertisingIdClient() {
    }


    public static AdvertisingIdClient.Info getAdvertisingIdInfo(Context var0) throws IOException {
        if (VERSION.SDK_INT >= 24) {
            try {
                String var1 = Global.getString(var0.getContentResolver(), SETTINGS_AD_ID_C);
                if (!TextUtils.isEmpty(var1)) {
                    Info var6 = InfoProviderUtil.getDeviceInfo(var0);
                    if (null != var6) {
                        return var6;
                    }
                    return requestAdvertisingIdInfo(var0);
                }

                String var2 = Global.getString(var0.getContentResolver(), SETTINGS_AD_ID);
                String var3 = Global.getString(var0.getContentResolver(), SETTINGS_TRACK_LIMIT);
                if (!TextUtils.isEmpty(var2) && !TextUtils.isEmpty(var3)) {
                    updateAdvertisingIdInfo(var0);
                    boolean var4 = Boolean.valueOf(var3);
                    return new Info(var2, var4);
                }
            } catch (Throwable var5) {
                Log.w("AdIdClient", "get Id err: " + var5.getClass().getSimpleName());
            }
        }

        return requestAdvertisingIdInfo(var0);
    }


    public static boolean verifyAdId(Context var0, String var1, boolean var2) throws AdIdVerifyException {
        try {
            Info var3 = requestAdvertisingIdInfo(var0);
            if (var3 == null) {
                Log.w("AdIdClient", "info is null");
                return false;
            } else {
                return TextUtils.equals(var1, var3.getId()) && var2 == var3.isLimitAdTrackingEnabled();
            }
        } catch (Throwable var4) {
            throw new AdIdVerifyException("Something wrong with verification, please try later.");
        }
    }

    private static void updateAdvertisingIdInfo(final Context var0) {
        StmUtil.POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AdvertisingIdClient.requestAdvertisingIdInfo(var0);
                } catch (Throwable var2) {
                    Log.w("AdIdClient", "update Id err: " + var2.getClass().getSimpleName());
                }

            }
        });
    }

    private static Info requestAdvertisingIdInfo(Context var0) throws IOException {
        if (InfoProviderUtil.verifyUri(var0)) {
            Log.i("AdvertisingIdClient", "requestAdvertisingIdInfo via provider");
            return InfoProviderUtil.queryInfo(var0);
        } else {
            Log.i("AdvertisingIdClient", "requestAdvertisingIdInfo via aidl");
            return getIdInfoViaAIDL(var0);
        }
    }

    private static Info getIdInfoViaAIDL(Context var0) throws IOException {
        try {
            PackageManager var1 = var0.getPackageManager();
            var1.getPackageInfo(ApkUtil.getHuaWeiPackName(var0), PackageManager.GET_META_DATA);
        } catch (NameNotFoundException var19) {
            throw new IOException("Service not found");
        } catch (Exception var20) {
            throw new IOException("Service not found: Exception");
        }

        PPSSerivceConnection connection = new PPSSerivceConnection();
        Intent intent = new Intent("com.uodis.opendevice.OPENIDS_SERVICE");
        intent.setPackage(ApkUtil.getHuaWeiPackName(var0));
        if (var0.bindService(intent, connection, Context.BIND_AUTO_CREATE)) {
            Info var23;
            try {
                String var4;
                try {
                    OpenDeviceIdentifierService var22 = OpenDeviceIdentifierService.Stub.asInterface(connection.getIBinder());
                    var23 = new Info(var22.getOaid(), var22.isOaidTrackLimited());
                } catch (InterruptedException var16) {
                    var4 = "bind hms service InterruptedException";
                    throw new IOException(var4);
                } catch (RemoteException var17) {
                    var4 = "bind hms service RemoteException";
                    throw new IOException(var4);
                }
            } finally {
                try {
                    var0.unbindService(connection);
                } catch (Throwable var15) {
                    Log.w("AdIdClient", "unbind " + var15.getClass().getSimpleName());
                }

            }

            return var23;
        } else {
            String var3 = "bind failed";
            throw new IOException(var3);
        }
    }


    public static boolean isAdvertisingIdAvailable(Context var0) {
        boolean var1 = true;

        try {
            PackageManager var2 = var0.getPackageManager();
            var2.getPackageInfo(ApkUtil.getHuaWeiPackName(var0), PackageManager.GET_META_DATA);
            Intent var3 = new Intent("com.uodis.opendevice.OPENIDS_SERVICE");
            var3.setPackage(ApkUtil.getHuaWeiPackName(var0));
            List var4 = var2.queryIntentServices(var3, 0);
            if (var4.isEmpty()) {
                var1 = false;
            }
        } catch (NameNotFoundException var5) {
            var1 = false;
        } catch (Exception var6) {
            var1 = false;
        }

        return var1;
    }


    public static final class Info {
        private final String advertisingId;
        private final boolean limitAdTrackingEnabled;


        public Info(String var1, boolean var2) {
            this.advertisingId = var1;
            this.limitAdTrackingEnabled = var2;
        }


        public String getId() {
            return this.advertisingId;
        }


        public boolean isLimitAdTrackingEnabled() {
            return this.limitAdTrackingEnabled;
        }
    }
}
