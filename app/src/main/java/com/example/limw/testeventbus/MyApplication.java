package com.example.limw.testeventbus;

import android.app.Application;

import com.example.limw.utils.EventBusUtils;


/**
 * Created by limw on 16/1/9.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {

        //初始化EventBus
        EventBusUtils.initEventBus();
//        if (AppConfig.EVENTBUS_IS_DEBUG) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                    .detectAll()   // or .detectAll() for all detectable problems
//                    .penaltyLog()
//                    .build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                    .detectLeakedSqlLiteObjects()
//                    .detectLeakedClosableObjects()
//                    .penaltyLog()
//                    .penaltyDeath()
//                    .build());
//    }
        super.onCreate();
    }
}
