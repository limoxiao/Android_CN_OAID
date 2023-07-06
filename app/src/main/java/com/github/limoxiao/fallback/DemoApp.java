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

package com.github.limoxiao.fallback;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.github.limoxiao.oaid.BuildConfig;
import com.github.limoxiao.oaid.DeviceIdentifier;
import com.github.limoxiao.oaid.OAIDLog;

/**
 * @author 大定府羡民（1032694760@qq.com）
 * @since 2020/5/20
 */
public class DemoApp extends Application {
    private boolean privacyPolicyAgreed = false;

    static {
        if (BuildConfig.DEBUG) {
            OAIDLog.enable();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        privacyPolicyAgreed = true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //注意APP合规性，若最终用户未同意隐私政策则不要调用
        if (privacyPolicyAgreed) {
            DeviceIdentifier.register(this);
        }
    }

}
