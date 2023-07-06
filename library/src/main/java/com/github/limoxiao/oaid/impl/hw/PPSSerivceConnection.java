//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.github.limoxiao.oaid.impl.hw;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import java.util.concurrent.TimeUnit;

public final class PPSSerivceConnection implements ServiceConnection {
    public static final ThreadPoolExecutor EXECUTOR;

    static {
        EXECUTOR = new ThreadPoolExecutor(0, 3, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue(2048), new DiscardPolicy());
    }


    boolean b = false;
    private final LinkedBlockingQueue<IBinder> binderLinkedBlockingQueue = new LinkedBlockingQueue(1);

    public PPSSerivceConnection() {
    }

    @Override
    public void onServiceConnected(ComponentName var1, final IBinder var2) {
        Log.d("PPSSerivceConnection", "onServiceConnected");
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("PPSSerivceConnection", "onServiceConnected " + System.currentTimeMillis());
                    PPSSerivceConnection.this.binderLinkedBlockingQueue.offer(var2);
                } catch (Throwable var2x) {
                    Log.w("PPSSerivceConnection", "onServiceConnected  " + var2x.getClass().getSimpleName());
                }

            }
        });
    }

    @Override
    public void onServiceDisconnected(ComponentName var1) {
        Log.d("PPSSerivceConnection", "onServiceDisconnected " + System.currentTimeMillis());
    }

    public IBinder getIBinder() throws InterruptedException {
        if (this.b) {
            throw new IllegalStateException();
        } else {
            this.b = true;
            return (IBinder)this.binderLinkedBlockingQueue.take();
        }
    }


}
