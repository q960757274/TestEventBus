package com.example.reflex.testeventbus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class Fragment2 extends BaseFragment {

    private View view;
    private TextView tv_fragment2;

    public Fragment2() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        EventBusUtils.registe(this,1);
//        EventBusUtils.registe(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view==null){
            view=inflater.inflate(R.layout.fragment_fragment2, container, false);
        }
        initView();
        return view;
    }

    private void initView(){
        tv_fragment2= (TextView) view.findViewById(R.id.tv_fragment2);
    }

    @Override
    public void onDestroy() {
//        EventBusUtils.unregister(this);
        super.onDestroy();
    }

//    public void onEvent(MessageEvent1 event) {
////        EventBusUtils.cancelEventDelivery(event);
//        Log.v("TAG", "Fragment2.onEvent->" + event.getObj().toString() + "->" + Thread.currentThread().getName());
//    }

//    public void onEventMainThread(MessageEvent1 event) {
//        Log.v("TAG", "Fragment2.onEventMainThread->" + event.getObj().toString() + "->" + Thread.currentThread().getName());
//    }
//
//    public void onEventBackgroundThread(MessageEvent event) {
//        Log.v("TAG", "Fragment2.onEventBackgroundThread->" + event.getObj().toString() + "->" + Thread.currentThread().getName());
//    }
//
//
//    public void onEventAsync(MessageEvent event) {
//        Log.v("TAG", "Fragment2.onEventAsync->" + event.getObj().toString() + "->" + Thread.currentThread().getName());
//    }
}
