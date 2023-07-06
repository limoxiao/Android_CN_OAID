#################
#项目自定义混淆配置
#################

# 本库模块专用的混淆规则
-keep class repackage.com.uodis.opendevice.aidl.** { *; }
-keep interface repackage.com.uodis.opendevice.aidl.** { *; }
-keep class repackage.com.asus.msa.SupplementaryDID.** { *; }
-keep interface repackage.com.asus.msa.SupplementaryDID.** { *; }
-keep class repackage.com.bun.lib.** { *; }
-keep interface repackage.com.bun.lib.** { *; }
-keep class repackage.com.heytap.openid.** { *; }
-keep interface repackage.com.heytap.openid.** { *; }
-keep class repackage.com.samsung.android.deviceidservice.** { *; }
-keep interface repackage.com.samsung.android.deviceidservice.** { *; }
-keep class repackage.com.zui.deviceidservice.** { *; }
-keep interface repackage.com.zui.deviceidservice.** { *; }
-keep class repackage.com.coolpad.deviceidsupport.** { *; }
-keep interface repackage.com.coolpad.deviceidsupport.** { *; }
-keep class repackage.com.android.creator.** { *; }
-keep interface repackage.com.android.creator.** { *; }
-keep class com.github.limoxiao.oaid.impl.hw.* { *; }
-keep class com.github.limoxiao.oaid.impl.hw.** { *; }
-keep class com.github.limoxiao.oaid.impl.hw.AdvertisingIdClient { *; }
