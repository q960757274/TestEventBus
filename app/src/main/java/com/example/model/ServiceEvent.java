package com.example.model;

import de.greenrobot.event.util.HasExecutionScope;

/**
 * 与Service通信的Event
 */
public class ServiceEvent implements HasExecutionScope {

    private int what=-1;
    private int arg0=-1;
    private int arg1=-1;
    private Object obj;

    public ServiceEvent(Object obj) {
        this.obj = obj;
    }

    public ServiceEvent(int what, int arg0, int arg1, Object obj) {
        this.what = what;
        this.arg0 = arg0;
        this.arg1 = arg1;
        this.obj = obj;
    }

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }

    public int getArg0() {
        return arg0;
    }

    public void setArg0(int arg0) {
        this.arg0 = arg0;
    }

    public int getArg1() {
        return arg1;
    }

    public void setArg1(int arg1) {
        this.arg1 = arg1;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    @Override
    public Object getExecutionScope() {
        return null;
    }

    @Override
    public void setExecutionScope(Object executionScope) {

    }
}
