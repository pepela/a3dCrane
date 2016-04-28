package com.pepela.a3dcrane;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by giorg_000 on 28.04.2016.
 */
public class SendDataService extends Service {

    static final public String COPA_RESULT = "ylee";

    static final public String COPA_MESSAGE = "didi yle";

    private LocalBroadcastManager broadcaster;

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }


    public void sendResult(String message) {
        Intent intent = new Intent(COPA_RESULT);
        if (message != null)
            intent.putExtra(COPA_MESSAGE, message);
        broadcaster.sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
