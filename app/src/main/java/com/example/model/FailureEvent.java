package com.example.model;

import de.greenrobot.event.util.HasExecutionScope;

/**
 * Created by limw on 16/1/20.
 */
public class FailureEvent implements HasExecutionScope{

    protected final Throwable throwable;
    private final String errorMsg="EventBus发布消息错误!";

    private Object executionScope;


    public FailureEvent(Throwable throwable) {
        this.throwable = throwable;
    }

    public String getErrorMsg() {
        return errorMsg;
    }



    @Override
    public Object getExecutionScope() {
        return executionScope;
    }

    @Override
    public void setExecutionScope(Object executionScope) {
        this.executionScope=executionScope;
    }
}
