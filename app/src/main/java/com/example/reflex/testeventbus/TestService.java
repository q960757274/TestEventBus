package com.example.reflex.testeventbus;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.model.ServiceEvent;
import com.example.reflex.utils.EventBusUtils;

public class TestService extends Service {
    public TestService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        EventBusUtils.registe(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        EventBusUtils.unregister(this);
        super.onDestroy();
    }

    //接收消息
    public void onEventMainThread(ServiceEvent event) {
        Log.v("TAG", "TestService.onEvent->" + event.getObj().toString() + "->" + Thread.currentThread().getName());
    }
}
