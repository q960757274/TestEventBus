package com.example.lmw.annotation.testeventbus;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.lmw.annotation.utils.EventBusUtils;
import com.example.model.ServiceEvent;

import eventbus.Subscribe;
import eventbus.ThreadMode;


public class TestService extends Service {
    public TestService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        EventBusUtils.registe(this);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        EventBusUtils.unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEvent(ServiceEvent event) {
        Log.v("TAG", "TestService.onEvent->" + event.getObj().toString());
    }
}
