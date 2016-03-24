package com.example.limw.testeventbus;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.limw.model.MessageEvent;
import com.example.limw.model.MessageEvent1;
import com.example.limw.utils.EventBusUtils;

import de.greenrobot.event.util.ThrowableFailureEvent;

public class Fragment1 extends BaseFragment {


    private TextView tv_frafment1;
    private Button btn2_fragment1;
    private View view;


    private int i = 0;

    public Fragment1() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EventBusUtils.registe(this);
        //EventBusUtils.registerSticky(this);
        //EventBusUtils.registe(this,1);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_fragment1, container, false);
            initView();
        }
        return view;
    }

    private void initView() {
        tv_frafment1 = (TextView) view.findViewById(R.id.tv_fragment1);
        btn2_fragment1 = (Button) view.findViewById(R.id.btn2_fragment1);

        btn2_fragment1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPostThreadName();
                EventBusUtils.post(new MessageEvent("from fragment1"));
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onDestroy() {
//        EventBusUtils.unregister(this);
        super.onDestroy();
    }

//    public void onEvent(MessageEvent1 event) {
//        Log.v("TAG", "Fragment1.onEvent->" + event.getObj().toString() + "->" + Thread.currentThread().getName());
//    }

//    public void onEventMainThread(MessageEvent event) {
//        Log.v("TAG", "Fragment1.onEventMainThread->" + event.getObj().toString() + "->" + Thread.currentThread().getName());
//    }
//
//    public void onEventBackgroundThread(MessageEvent event) {
//        Log.v("TAG", "Fragment1.onEventBackgroundThread->" + event.getObj().toString() + "->" + Thread.currentThread().getName());
//    }
//
//
//    public void onEventAsync(MessageEvent event) {
//        Log.v("TAG", "Fragment1.onEventAsync->" + event.getObj().toString() + "->" + Thread.currentThread().getName());
//    }

    private void showPostThreadName() {
        Log.v("TAG", "post thread->" + Thread.currentThread().getName());
    }

//    public void onEventMainThread(ThrowableFailureEvent event) {
//        // Show error in UI
//        Log.v("TAG", "Fragment1.onEventMainThread->ThrowableFailureEvent->" + event.getThrowable().toString() + "->" + Thread.currentThread().getName());
//        new AlertDialog.Builder(getActivity())
//                .setTitle("提示")
//                .setMessage("123")
//                .setPositiveButton("确定", null)
//                .create().show();
//    }
}
