package com.example.lmw.annotation.testeventbus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.lmw.annotation.utils.EventBusUtils;
import com.example.model.MessageEvent2;
import com.example.reflex.testeventbus.R;

import eventbus.Subscribe;
import eventbus.ThreadMode;


public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        EventBusUtils.registe(this);
//        EventBusUtils.postSticky(new MessageEvent2("from Main2Activity by postStickyEvent"));
        init();
    }

    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    public void onEvent(MessageEvent2 event) {
        Log.v("TAG", "Main2Activity.onEvent->" + event.getObj().toString() + "->" + Thread.currentThread().getName());
    }

    private void init() {
        MessageEvent2 event = EventBusUtils.getStickyEvent(MessageEvent2.class);
        Log.v("TAG", "Main2Activity-->getStickyEvent By event.class->" + event.getObj().toString());
        EventBusUtils.post(new MessageEvent2("from MainActivity by postStickyEvent"));
    }


    @Override
    protected void onDestroy() {
        EventBusUtils.unregister(this);
        super.onDestroy();

    }
}
