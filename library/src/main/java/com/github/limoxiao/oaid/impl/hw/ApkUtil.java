//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.github.limoxiao.oaid.impl.hw;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.util.Log;

public class ApkUtil {
    public static boolean wasSamePackName(Context var0, String var1) {
        return getPackageInfo(var0, var1) != null;
    }

    public static PackageInfo getPackageInfo(Context var0, String var1) {
        if (TextUtils.isEmpty(var1)) {
            return null;
        } else {
            PackageInfo var2 = null;

            try {
                if (var0 != null) {
                    PackageManager var3 = var0.getPackageManager();
                    if (var3 != null) {
                        var2 = var3.getPackageInfo(var1, PackageManager.GET_META_DATA);
                    }
                }
            } catch (NameNotFoundException var4) {
                Log.w("ApkUtil", "getPackageInfo NameNotFoundException");
            } catch (Exception var5) {
                Log.w("ApkUtil", "getPackageInfo Exception");
            }

            return var2;
        }
    }

    public static String getHuaWeiPackName(Context var0) {
        if (wasSamePackName(var0, "com.huawei.hwid")) {
            return "com.huawei.hwid";
        } else if (wasSamePackName(var0, "com.huawei.hms")) {
            return "com.huawei.hms";
        } else {
            return wasSamePackName(var0, "com.huawei.hwid.tv") ? "com.huawei.hwid.tv" : "com.huawei.hwid";
        }
    }
}
