package com.example.model;

import de.greenrobot.event.util.HasExecutionScope;

/**
 * 信息Event
 */
public class MessageEvent1 implements HasExecutionScope {

    private int what;
    private Object obj;

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
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

    public MessageEvent1(Object obj) {
        this.obj = obj;
    }
}
