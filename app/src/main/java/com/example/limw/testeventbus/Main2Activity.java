package com.example.limw.testeventbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.limw.model.MessageEvent;
import com.example.limw.model.MessageEvent2;
import com.example.limw.utils.EventBusUtils;

import de.greenrobot.event.EventBus;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        EventBusUtils.registerSticky(this);
//        EventBusUtils.postSticky(new MessageEvent2("from Main2Activity by postStickyEvent"));
//        init();
    }

    public void onEvent(MessageEvent2 event){
        Log.v("TAG", "Main2Activity.onEvent->" + event.getObj().toString() + "->" + Thread.currentThread().getName());
    }

    private void init() {
        MessageEvent2 event = EventBus.getDefault().getStickyEvent(MessageEvent2.class);
        Log.v("TAG", "Main2Activity-->getStickyEvent By event.class->" + event.getObj().toString());
    }


    @Override
    protected void onDestroy() {
        EventBusUtils.unregister(this);
        super.onDestroy();

    }
}