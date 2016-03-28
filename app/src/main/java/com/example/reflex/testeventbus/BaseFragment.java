package com.example.reflex.testeventbus;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.model.MessageEvent1;
import com.example.reflex.utils.EventBusUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {


    public BaseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
        return textView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        EventBusUtils.registe(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        EventBusUtils.unregister(this);
        super.onDestroy();
    }

    public void onEvent(MessageEvent1 event){
        Log.v("TAG", "BaseFragment.onEvent->" + event.getObj().toString() + "->" + Thread.currentThread().getName());
    }
}
