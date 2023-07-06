//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.github.limoxiao.oaid.impl.hw;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Log;

import java.io.Closeable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import java.util.concurrent.TimeUnit;

public abstract class StmUtil {
    public static final ThreadPoolExecutor POOL_EXECUTOR = new ThreadPoolExecutor(0, 3, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue(2048), new DiscardPolicy());


    public static Context getProtectedContext(Context var0) {
        Context var1;
        if (isGreatN()) {
            Context var2 = var0.createDeviceProtectedStorageContext();
            var1 = var2;
        } else {
            var1 = var0;
        }

        return var1;
    }

    private static boolean isGreatN() {
        return VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    public static void closeAble(Closeable var0) {
        if (null != var0) {
            try {
                var0.close();
            } catch (Throwable var2) {
                Log.w("StmUt", "close " + var2.getClass().getSimpleName());
            }
        }

    }

    public static Integer getPpsKitVerCode(Context var0) {
        if (null == var0) {
            return null;
        } else {
            try {
                ApplicationInfo var1 = var0.getPackageManager().getApplicationInfo(ApkUtil.getHuaWeiPackName(var0), PackageManager.GET_META_DATA);
                if (var1 != null && var1.metaData != null) {
                    String var2 = null;
                    Object var3 = var1.metaData.get("ppskit_ver_code");
                    if (var3 != null) {
                        var2 = var3.toString();
                        return Integer.valueOf(var2);
                    }
                }
            } catch (Throwable var4) {
                Log.i("StmUt", "getPpsKitVerCode ex: " + var4.getClass().getSimpleName());
            }

            return null;
        }
    }

    public static boolean verifyProvider(Context var0, Uri var1) {
        if (var0 != null && var1 != null) {
            PackageManager var2 = var0.getPackageManager();
            ProviderInfo var3 = var2.resolveContentProvider(var1.getAuthority(), 0);
            if (var3 == null) {
                Log.e("StmUt", "verify provider invalid param");
                return false;
            } else {
                ApplicationInfo var4 = var3.applicationInfo;
                if (null == var4) {
                    return false;
                } else {
                    String var5 = var4.packageName;
                    if (TextUtils.isEmpty(var5)) {
                        return false;
                    } else {
                        return var2.checkSignatures(var0.getPackageName(), var5) == PackageManager.SIGNATURE_MATCH || (var4.flags & 1) == 1;
                    }
                }
            }
        } else {
            return false;
        }
    }


}
