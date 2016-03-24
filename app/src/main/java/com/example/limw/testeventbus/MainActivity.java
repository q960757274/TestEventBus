package com.example.limw.testeventbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.limw.model.MessageEvent;
import com.example.limw.model.MessageEvent1;
import com.example.limw.model.MessageEvent2;
import com.example.limw.model.ServiceEvent;
import com.example.limw.utils.EventBusUtils;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import de.greenrobot.event.util.AsyncExecutor;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private FragmentManager fragmentManager;
    private Fragment1 fragment1;
    private Fragment2 fragment2;
    private TextView tv_main_activity;
    private Button btn_main_activity;
    private Button btn_start_thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        startService();


    }

    private void initView() {

        fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragmentTransaction.add(R.id.container1, fragment1).add(R.id.container2, fragment2).commitAllowingStateLoss();

        tv_main_activity = (TextView) findViewById(R.id.tv_main_activity);
        btn_main_activity = (Button) findViewById(R.id.btn_main_activity);
        btn_start_thread = (Button) findViewById(R.id.btn_start_thread);

        btn_main_activity.setOnClickListener(this);
        btn_start_thread.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_main_activity:

//                startMain2Activity();
                sentMsgToFragment();
//                sentMsgToService();

                break;
            case R.id.btn_start_thread:
//                sentMsgToService();
//                postInThread();
                postByAsyncExecutor();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        stopService();
        super.onDestroy();
    }

    //向service发布消息
    private void sentMsgToService() {
        showPostThreadName();
        EventBusUtils.post(new ServiceEvent("from MainActivity to Service"));
    }

    //启动service
    private void startService() {
        startService(new Intent(this, TestService.class));
    }

    //停止service
    private void stopService() {
        stopService(new Intent(this, TestService.class));
    }

    //在异步线程发布消息
    private void postInThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    showPostThreadName();
                    EventBusUtils.post(new MessageEvent("from MainActivity_Background_Thread"));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //发送消息给Fragment1
    private void sentMsgToFragment() {
        showPostThreadName();
        EventBusUtils.post(new MessageEvent1("from MainActivity"));
    }

    //打印发布线程名称
    private void showPostThreadName() {
        Log.v("TAG", "post thread->" + Thread.currentThread().getName());
    }

    //跳转到Main2Activity
    private void startMain2Activity() {
        EventBusUtils.postSticky(new MessageEvent2("from MainActivity by postStickyEvent"));
        startActivity(new Intent(this, Main2Activity.class));
    }

    //使用Executor
    private void postByAsyncExecutor() {
        EventBusUtils.execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                showPostThreadName();
//                Double b=Double.parseDouble("xxx");
                EventBusUtils.post(new MessageEvent1("from MainActivity_AsyncExecutor"));
            }
        });
    }






}
