//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.github.limoxiao.oaid.impl.hw;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public abstract class AESUtils {
    private static final byte[] a = new byte[0];
    private static final byte[] b = new byte[0];
    private static SoftReference<byte[]> workKeySoft;

    public static final String IDENTIFIER_HIAD_STR_2 ="245d64e65dc9fe70d4d62aa6b941221fa92a3fb07db7a4858e43bf1dbf2972e9";
    public static final String IDENTIFIER_HIAD_STR_3 ="9b38b1ce5d9b5bba1a6539ad75eae153555c74f5b95e6cdfe5019a6a0e56f466";

    public static String decrypt(String var0, String var1) {
        if (!TextUtils.isEmpty(var0) && !TextUtils.isEmpty(var1)) {
            synchronized(a) {
                String var10000;
                try {
                    byte[] var3 = hex2Byte(var1);
                    var10000 = decrypt32(var0, var3);
                } catch (Throwable var5) {
                    Log.w("Aes128", "decrypt oaid ex: " + var5.getClass().getSimpleName());
                    return null;
                }

                return var10000;
            }
        } else {
            return null;
        }
    }

    public static String decrypt32(String var0, byte[] var1) {
        if (!TextUtils.isEmpty(var0) && var0.length() >= 32 && null != var1 && 0 != var1.length) {
            try {
                if (isGreatKitkat()) {
                    return decrypt(var0, var1);
                }
            } catch (Throwable var3) {
                Log.w("Aes128", "fail to decrypt: " + var3.getClass().getSimpleName());
            }

            return "";
        } else {
            return "";
        }
    }

    public static String generate(String var0, byte[] var1) {
        if (!TextUtils.isEmpty(var0) && null != var1 && 0 != var1.length) {
            try {
                if (isGreatKitkat()) {
                    return getSecureRandom(var0, var1);
                }
            } catch (Exception var3) {
                Log.w("Aes128", "fail to cipher: " + var3.getClass().getSimpleName());
            } catch (Throwable var4) {
                Log.w("Aes128", "fail to cipher: " + var4.getClass().getSimpleName());
            }

            return "";
        } else {
            return "";
        }
    }

    private static String getSecureRandom(String var0, byte[] var1) {
        if (!TextUtils.isEmpty(var0) && var1 != null && var1.length >= 16 && isGreatKitkat()) {
            byte[] var2 = generateByte(12);
            byte[] var3 = decrypt(var0, var1, var2);
            if (var3 != null && var3.length != 0) {
                String var4 = toHexString(var2);
                String var5 = toHexString(var3);
                return var4 + var5;
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public static String toHexString(byte[] var0) {
        if (null != var0 && var0.length != 0) {
            StringBuilder var1 = new StringBuilder();

            for(int var2 = 0; var2 < var0.length; ++var2) {
                String var3 = Integer.toHexString(255 & var0[var2]);
                if (var3.length() == 1) {
                    var1.append('0');
                }

                var1.append(var3);
            }

            return var1.toString();
        } else {
            return "";
        }
    }

    private static byte[] decrypt(String var0, byte[] var1, byte[] var2) {
        if (!TextUtils.isEmpty(var0) && byteGrate16(var1) && byteGrate12(var2) && isGreatKitkat()) {
            try {
                return decrypt(var0.getBytes("UTF-8"), var1, var2);
            } catch (UnsupportedEncodingException var4) {
                Log.e("Aes128", "GCM encrypt data error" + var4.getMessage());
                return new byte[0];
            }
        } else {
            Log.i("Aes128", "gcm encrypt param is not right");
            return new byte[0];
        }
    }

    public static byte[] decrypt(byte[] var0, byte[] var1, byte[] var2) {
        if (null != var0 && var0.length != 0) {
            if (null != var1 && var1.length >= 16) {
                if (!isGreatKitkat()) {
                    Log.i("Aes128", "encrypt, osVersion too low.");
                    return new byte[0];
                } else if (null != var2 && var2.length >= 12) {
                    try {
                        SecretKeySpec var3 = new SecretKeySpec(var1, "AES");
                        Cipher cliper = Cipher.getInstance("AES/GCM/NoPadding");
                        AlgorithmParameterSpec var5 = getAlgorithmParameterSpec(var2);
                        cliper.init(1, var3, var5);
                        return cliper.doFinal(var0);
                    } catch (GeneralSecurityException var6) {
                        Log.e("Aes128", "GCM encrypt data error" + var6.getMessage());
                        return new byte[0];
                    }
                } else {
                    Log.i("Aes128", "encrypt, random invalid.");
                    return new byte[0];
                }
            } else {
                Log.i("Aes128", "encrypt, keyBytes invalid.");
                return new byte[0];
            }
        } else {
            Log.i("Aes128", "encrypt, contentBytes invalid.");
            return new byte[0];
        }
    }

    public static byte[] generateByte(int var0) {
        SecureRandom var1 = getSecureRandom();
        byte[] var2 = new byte[var0];
        var1.nextBytes(var2);
        return var2;
    }

    public static byte[] generate16Byte() {
        return generateByte(16);
    }

    private static SecureRandom getSecureRandom() {
        SecureRandom var0 = null;

        try {
            if (VERSION.SDK_INT >= 26) {
                var0 = SecureRandom.getInstanceStrong();
            } else {
                var0 = SecureRandom.getInstance("SHA1PRNG");
            }
        } catch (Exception var2) {
            Log.w("Aes128", "getInstanceStrong, exception: " + var2.getClass().getSimpleName());
        }

        if (var0 == null) {
            var0 = new SecureRandom();
        }

        return var0;
    }

    private static boolean byteGrate12(byte[] var0) {
        return var0 != null && var0.length >= 12;
    }

    private static boolean byteGrate16(byte[] var0) {
        return var0 != null && var0.length >= 16;
    }

    public static byte[] getWorkKeyBytes(Context var0) {
        synchronized(b) {
            byte[] var2 = null;
            if (workKeySoft != null) {
                var2 = (byte[]) workKeySoft.get();
            }

            if (var2 == null) {
                try {
                    var2 = hex2ByteWithException(generate(var0));
                } catch (UnsupportedEncodingException var5) {
                    Log.w("Aes128", "getWorkKeyBytes UnsupportedEncodingException");
                    var2 = regenerateWorkKey(var0);
                } catch (Throwable var6) {
                    Log.w("Aes128", "getWorkKeyBytes " + var6.getClass().getSimpleName());
                    var2 = regenerateWorkKey(var0);
                }

                workKeySoft = new SoftReference(var2);
            }

            return var2;
        }
    }

    private static String generate(Context var0) {
        if (var0 == null) {
            return "";
        } else {
            synchronized(b) {
                SpHelper var2 = SpHelper.getInstance(var0);
                String var4 = var2.getABook();
                String var3;
                if (null == var4) {
                    var3 = saveGenerateBook(var0, var2);
                } else {
                    var3 = decrypt32(var4, getUserRootKey(var0));
                    if (TextUtils.isEmpty(var3)) {
                        var3 = saveGenerateBook(var0, var2);
                    }
                }

                return var3;
            }
        }
    }

    private static byte[] regenerateWorkKey(Context var0) {
        Log.i("Aes128", "regenerateWorkKey");
        SpHelper var1 = SpHelper.getInstance(var0);
        var1.saveABook("");
        return hex2Byte(generate(var0));
    }

    private static String saveGenerateBook(Context var0, SpHelper var1) {
        byte[] var2 = generate();
        String var3 = toHexString(var2);
        String var4 = generate(var3, getUserRootKey(var0));
        var1.saveABook(var4);
        return var3;
    }

    public static byte[] generate() {
        return generateByte(16);
    }

    private static byte[] getUserRootKey(Context var0) {
        if (var0 == null) {
            return new byte[0];
        } else {
            SpHelper var1 = SpHelper.getInstance(var0);
            byte[] var2 = getBytes(var0);
            String var3 = var1.getCatchCat();
            byte[] var4 = null;

            try {
                var4 = generateSecret(toHexString(var2).toCharArray(), hex2Byte(var3));
            } catch (NoSuchAlgorithmException var6) {
                Log.w("Aes128", "get userRootKey NoSuchAlgorithmException");
            } catch (InvalidKeySpecException var7) {
                Log.w("Aes128", "get userRootKey InvalidKeySpecException");
            }

            return var4;
        }
    }

    private static byte[] getBytes(Context var0) {
        String var1 = get64Chapter(var0);
        return getBytes(var0, var1);
    }

    private static byte[] getBytes(Context var0, String var1) {
        String var2 = IDENTIFIER_HIAD_STR_2;
        String var3 = IDENTIFIER_HIAD_STR_3;
        return getBytes(var1, var2, var3);
    }

    private static byte[] getBytes(String var0, String var1, String var2) {
        byte[] var3 = hex2Byte(var0);
        byte[] var4 = hex2Byte(var1);
        byte[] var5 = hex2Byte(var2);
        return getBytes(getBytes(var3, var4), var5);
    }

    private static byte[] getBytes(byte[] var0, byte[] var1) {
        byte[] longByte = var0;
        byte[] shortByte;
        if (var0.length > var1.length) {
            shortByte = var1;
        } else {
            longByte = var1;
            shortByte = var0;
        }

        int longLength = longByte.length;
        int smallLength = shortByte.length;
        byte[] var6 = new byte[longLength];

        int i;
        for(i = 0; i < smallLength; ++i) {
            var6[i] = (byte)(shortByte[i] ^ longByte[i]);
        }

        while(i < longByte.length) {
            var6[i] = longByte[i];
            ++i;
        }

        return var6;
    }

    /**
     * 获取一个64为字符串
     *  先从sp读取 没有则 先生成 再保存到sp
     * @param var0
     * @return
     */
    private static String get64Chapter(Context var0) {
        final SpHelper var1 = SpHelper.getInstance(var0);
        String var2 = var1.getSecondChapter();
        if (TextUtils.isEmpty(var2)) {
            final String var3 = generate(64);
            var2 = var3;
            StmUtil.POOL_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    var1.saveSecondChapter(var3);
                }
            });
        }

        return var2;
    }

    private static String generate(int var0) {
        try {
            SecureRandom var1 = getSecureRandom();
            StringBuilder var2 = new StringBuilder();

            for(int var3 = 0; var3 < var0; ++var3) {
                var2.append(Integer.toHexString(var1.nextInt(16)));
            }

            return var2.toString();
        } catch (Throwable var4) {
            Log.w("Aes128", "generate aes key1 err:" + var4.getClass().getSimpleName());
            return "";
        }
    }

    public static byte[] generateSecret(char[] var0, byte[] var1) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec var2 = new PBEKeySpec(var0, var1, 10000, 256);
        SecretKeyFactory var3 = null;
        if (VERSION.SDK_INT > 26) {
            var3 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        } else {
            var3 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        }

        return var3.generateSecret(var2).getEncoded();
    }

    private static String decrypt(String var0, byte[] var1) {
        if (!TextUtils.isEmpty(var0) && var1 != null && var1.length >= 16 && isGreatKitkat()) {
            try {
                SecretKeySpec var2 = new SecretKeySpec(var1, "AES");
                Cipher var3 = Cipher.getInstance("AES/GCM/NoPadding");
                String var4 = getStringPre24(var0);
                String var5 = getSubstring24(var0);
                if (!TextUtils.isEmpty(var4) && !TextUtils.isEmpty(var5)) {
                    AlgorithmParameterSpec var6 = getAlgorithmParameterSpec(hex2Byte(var4));
                    var3.init(2, var2, var6);
                    byte[] var7 = var3.doFinal(hex2Byte(var5));
                    return new String(var7, "UTF-8");
                } else {
                    Log.i("Aes128", "ivParameter or encrypedWord is null");
                    return "";
                }
            } catch (GeneralSecurityException | UnsupportedEncodingException var8) {
                Log.e("Aes128", "GCM decrypt data exception: " + var8.getMessage());
                return "";
            }
        } else {
            return "";
        }
    }

    public static byte[] hex2Byte(String var0) {
        byte[] var1 = new byte[0];

        try {
            var1 = hex2ByteWithException(var0);
        } catch (Throwable var3) {
            Log.e("Aes128", "hex string 2 byte: " + var3.getClass().getSimpleName());
        }

        return var1;
    }

    public static byte[] hex2ByteWithException(String var0) throws UnsupportedEncodingException, NumberFormatException {
        if (TextUtils.isEmpty(var0)) {
            return new byte[0];
        } else {
            String var1 = var0.toUpperCase(Locale.ENGLISH);
            byte[] var2 = new byte[var1.length() / 2];
            byte[] var3 = var1.getBytes("UTF-8");

            for(int var4 = 0; var4 < var2.length; ++var4) {
                byte var5 = Byte.decode("0x" + new String(new byte[]{var3[var4 * 2]}, "UTF-8"));
                var5 = (byte)(var5 << 4);
                byte var6 = Byte.decode("0x" + new String(new byte[]{var3[var4 * 2 + 1]}, "UTF-8"));
                var2[var4] = (byte)(var5 ^ var6);
            }

            return var2;
        }
    }

    private static boolean isGreatKitkat() {
        return VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    private static AlgorithmParameterSpec getAlgorithmParameterSpec(byte[] var0) {
        return (AlgorithmParameterSpec)(VERSION.SDK_INT < 21 ? new IvParameterSpec(var0) : new GCMParameterSpec(128, var0));
    }

    private static String getStringPre24(String var0) {
        if (!TextUtils.isEmpty(var0) && var0.length() >= 24) {
            return var0.substring(0, 24);
        } else {
            Log.i("Aes128", "IV is invalid.");
            return "";
        }
    }

    private static String getSubstring24(String var0) {
        return !TextUtils.isEmpty(var0) && var0.length() >= 24 ? var0.substring(24) : "";
    }

    public static class SpHelper {
        private static final Long a = 120000L;
        private SharedPreferences bookSp = null;
        private SharedPreferences rockSp = null;
        private SharedPreferences stoneSp = null;
        private final byte[] e = new byte[0];
        private final byte[] f = new byte[0];
        private final byte[] g = new byte[0];
        private static final byte[] h = new byte[0];
        private static volatile SpHelper instance;
        private Context context;

        private SpHelper(Context var1) {
            try {
                this.context = var1.getApplicationContext();
                Context var2 = StmUtil.getProtectedContext(var1);
                this.bookSp = var2.getSharedPreferences("identifier_sp_story_book_file", Context.MODE_MULTI_PROCESS);
                this.rockSp = var2.getSharedPreferences("identifier_hiad_sp_bed_rock_file", Context.MODE_MULTI_PROCESS);
                this.stoneSp = var2.getSharedPreferences("identifier_hiad_sp_red_stone_file", Context.MODE_MULTI_PROCESS);
            } catch (Throwable var3) {
                Log.w("Aes128", "get SharedPreference error: " + var3.getClass().getSimpleName());
            }

        }

        public static SpHelper getInstance(Context var0) {
            if (null == instance) {
                synchronized(h) {
                    if (instance == null) {
                        instance = new SpHelper(var0);
                    }

                    return instance;
                }
            } else {
                return instance;
            }
        }

        public void saveFirstChapter(String var1) {
            synchronized(this.f) {
                if (null != this.stoneSp) {
                    byte[] var3 = getWorkKeyBytes(this.context);
                    Editor var4 = this.stoneSp.edit();
                    var4.putString("read_first_chapter", generate(var1, var3)).apply();
                }
            }
        }

        public String getFirstChapter() {
            synchronized(this.f) {
                if (null == this.stoneSp) {
                    return "";
                } else {
                    String var2 = this.stoneSp.getString("read_first_chapter", "");
                    if (TextUtils.isEmpty(var2)) {
                        return var2;
                    } else {
                        byte[] var3 = getBytes(this.context);
                        return decrypt(var2, var3);
                    }
                }
            }
        }

        public void saveReadFirstChapterTime() {
            synchronized(this.g) {
                if (null != this.bookSp) {
                    Editor var2 = this.bookSp.edit();
                    var2.putLong("read_first_chapter_time", System.currentTimeMillis()).apply();
                }
            }
        }

        public boolean haveReadFirstChapter() {
            synchronized(this.g) {
                if (null == this.bookSp) {
                    return false;
                } else {
                    long var2 = this.bookSp.getLong("read_first_chapter_time", -1L);
                    if (var2 < 0L) {
                        return false;
                    } else {
                        return var2 + a > System.currentTimeMillis();
                    }
                }
            }
        }

        public void saveHasFirstChapter() {
            synchronized(this.g) {
                if (null != this.bookSp) {
                    Editor var2 = this.bookSp.edit();
                    var2.putBoolean("has_read_first_chapter", true).apply();
                }
            }
        }

        public boolean getHasFirstChapter() {
            synchronized(this.g) {
                return null == this.bookSp ? false : this.bookSp.getBoolean("has_read_first_chapter", false);
            }
        }

        void saveABook(String var1) {
            synchronized(this.e) {
                if (null != this.rockSp) {
                    Editor var3 = this.rockSp.edit();
                    var3.putString("get_a_book", var1).commit();
                }
            }
        }

        String getABook() {
            synchronized(this.e) {
                return null == this.rockSp ? null : this.rockSp.getString("get_a_book", (String)null);
            }
        }

        void saveCatchCat(String var1) {
            synchronized(this.g) {
                if (null != this.bookSp) {
                    Editor var3 = this.bookSp.edit();
                    var3.putString("catch_a_cat", var1).commit();
                }
            }
        }

        String getCatchCat() {
            synchronized(this.g) {
                if (null == this.bookSp) {
                    return null;
                } else {
                    String var2 = this.bookSp.getString("catch_a_cat", (String)null);
                    if (null == var2) {
                        var2 = toHexString(generate16Byte());
                        this.saveCatchCat(var2);
                    }

                    return var2;
                }
            }
        }

        public String getSecondChapter() {
            synchronized(this.g) {
                return this.bookSp.getString("read_second_chapter", "");
            }
        }

        public void saveSecondChapter(String var1) {
            synchronized(this.g) {
                Editor var3 = this.bookSp.edit();
                var3.putString("read_second_chapter", var1).apply();
            }
        }
    }
}
