//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.github.limoxiao.oaid.impl.hw;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.provider.Settings.Global;
import android.text.TextUtils;

import com.github.limoxiao.oaid.OAIDLog;
import com.github.limoxiao.oaid.impl.hw.AdvertisingIdClient.Info;


public class InfoProviderUtil {
    public static final String EMPTY_OAID = "00000000-0000-0000-0000-000000000000";
    private static final Uri OAID_SCP_URI = (new Builder()).scheme("content").authority("com.huawei.hwid.pps.apiprovider").path("/oaid_scp/get").build();
    private static final Uri QUERY_OAID_URI = (new Builder()).scheme("content").authority("com.huawei.hwid.pps.apiprovider").path("/oaid/query").build();

    public static Info getDeviceInfo(final Context var0) {
        if (null != var0 && verifyUri(var0, OAID_SCP_URI)) {
            String var1 = Global.getString(var0.getContentResolver(), "pps_oaid_c");
            if (TextUtils.isEmpty(var1)) {
                return null;
            } else {
                final AESUtils.SpHelper var2 = AESUtils.SpHelper.getInstance(var0);
                String scp = var2.getFirstChapter();
                if (TextUtils.isEmpty(scp)) {
                    OAIDLog.print("InfoProviderUtil scp is empty");
                    StmUtil.POOL_EXECUTOR.execute(new Runnable() {
                        @Override
                        public void run() {
                            if (var2.haveReadFirstChapter()) {
                                OAIDLog.print("InfoProviderUtil within key update interval.");
                            } else {
                                OAIDLog.print("InfoProviderUtil saveReadFirstChapterTime");
                                var2.saveReadFirstChapterTime();
                                String var1 = queryOpWk(var0);
                                var2.saveFirstChapter(var1);
                            }
                        }
                    });
                    if (var2.getHasFirstChapter()) {
                        return new Info("00000000-0000-0000-0000-000000000000", true);
                    } else {
                        var2.saveHasFirstChapter();
                        return null;
                    }
                } else {
                    String oaid = AESUtils.decrypt(var1, scp);
                    if (TextUtils.isEmpty(oaid)) {
                        OAIDLog.print("InfoProviderUtil decrypt oaid failed.");
                        StmUtil.POOL_EXECUTOR.execute(new Runnable() {
                            @Override
                            public void run() {
                                String var1 = queryOpWk(var0);
                                var2.saveFirstChapter(var1);
                            }
                        });
                        return null;
                    } else {
                        boolean var5 = "00000000-0000-0000-0000-000000000000".equalsIgnoreCase(oaid);
                        return new Info(oaid, var5);
                    }
                }
            }
        } else {
            return null;
        }
    }

    public static Info queryInfo(Context var0) {
        if (null != var0 && verifyUri(var0)) {
            Cursor var1 = null;

            try {
                var1 = var0.getContentResolver().query(QUERY_OAID_URI, (String[]) null, (String) null, (String[]) null, (String) null);
                if (null != var1 && var1.moveToFirst()) {
                    int var12 = var1.getColumnIndexOrThrow("oaid");
                    int var3 = var1.getColumnIndexOrThrow("limit_track");
                    String var4 = var1.getString(var12);
                    boolean var5;
                    if ("00000000-0000-0000-0000-000000000000".equalsIgnoreCase(var4)) {
                        var5 = true;
                    } else {
                        var5 = Boolean.valueOf(var1.getString(var3));
                    }

                    Info var6 = new Info(var4, var5);
                    return var6;
                }

                Info var2 = new Info("00000000-0000-0000-0000-000000000000", true);
                return var2;
            } catch (Throwable var10) {
                OAIDLog.print("InfoProviderUtil query oaid via provider ex: " + var10.getClass().getSimpleName());
            } finally {
                StmUtil.closeAble(var1);
            }

            return new Info("00000000-0000-0000-0000-000000000000", true);
        } else {
            return new Info("00000000-0000-0000-0000-000000000000", true);
        }
    }

    private static String queryOpWk(Context var0) {
        if (null == var0) {
            return "";
        } else {
            Cursor var1 = null;

            try {
                var1 = var0.getContentResolver().query(OAID_SCP_URI, (String[]) null, (String) null, (String[]) null, (String) null);
                if (null == var1 || !var1.moveToFirst()) {
                    String var9 = "";
                    return var9;
                }

                int var2 = var1.getColumnIndexOrThrow("op_wk");
                String var3 = var1.getString(var2);
                return var3;
            } catch (Throwable var7) {
                OAIDLog.print("InfoProviderUtil get remote key ex: " + var7.getClass().getSimpleName());
            } finally {
                StmUtil.closeAble(var1);
            }

            return "";
        }
    }

    public static boolean verifyUri(Context var0) {
        return verifyUri(var0, QUERY_OAID_URI);
    }

    private static boolean verifyUri(Context var0, Uri var1) {
        if (null != var0 && null != var1) {
            Integer var2 = StmUtil.getPpsKitVerCode(var0);
            return null != var2 && 30462100 <= var2 ? StmUtil.verifyProvider(var0, var1) : false;
        } else {
            return false;
        }
    }
}
