package com.example.limw.utils;



import com.example.limw.model.AppConfig;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.util.AsyncExecutor;

/**
 * Eventbus 辅助工具类
 */
public class EventBusUtils {

    private static AsyncExecutor asyncExecutor = null;

    /**
     * 注册EventBus
     *
     * @param object 订阅者:eg:上下文
     */
    public static void registe(Object object) {
        registe(object, 0);
    }

    /**
     * 注册EventBus
     *
     * @param object   订阅者:eg:上下文
     * @param priority 优先级,默认为0
     */
    public static void registe(Object object, int priority) {
        try {
            if (!EventBus.getDefault().isRegistered(object)) {
                EventBus.getDefault().register(object, priority);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册粘性事件
     * @param object  订阅者:上下文
     */
    public static void registerSticky(Object object) {
        registerSticky(object,0);
    }

    /**
     * 注册粘性事件
     * @param object  订阅者:上下文
     * @param priority 优先级
     */
    public static void registerSticky(Object object, int priority) {
        try {
            if(!EventBus.getDefault().isRegistered(object)){
                EventBus.getDefault().registerSticky(object,priority);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 反注册EventBus
     *
     * @param object 订阅者:eg:上下文
     */
    public static void unregister(Object object) {
        try {
            if (EventBus.getDefault().isRegistered(object)) {
                EventBus.getDefault().unregister(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 初始化默认的EventBus实例配置.当为true时是测试环境,使用EventBus抛异常时会崩溃,便于调试;false不崩溃.
     */
    public static void initEventBus() {
        EventBus.builder().throwSubscriberException(AppConfig.EVENTBUS_IS_DEBUG).installDefaultEventBus();

    }

    /**
     * 取消事件的分发,这个方法必须在onEvent方法中才起作用
     *
     * @param event
     */
    public static void cancelEventDelivery(Object event) {
        EventBus.getDefault().cancelEventDelivery(event);
    }

    /**
     * 发布一个粘性事件,同类型的event只会保留最后一次post的值
     *
     * @param event
     */
    public static void postSticky(Object event) {
        EventBus.getDefault().postSticky(event);
    }

    /**
     * 发布事件,首先检测这个事件是否有订阅者,如果没有则不发布事件
     *
     * @param event
     */
    public static void post(Object event) {
        if (EventBus.getDefault().hasSubscriberForEvent(event.getClass())) {
            EventBus.getDefault().post(event);
        }
    }

    /**
     * 根据事件类型获取粘性事件
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getStickyEvent(Class clazz) {
        return (T) EventBus.getDefault().getStickyEvent(clazz);
    }

    /**
     * 根据事件的具体对象移除粘性事件
     *
     * @param object 具体的事件对象,这个方法一般在onEvent...中执行
     */
    public static void removeStickyEventByObject(Object object) {
        EventBus.getDefault().removeStickyEvent(object);
    }

    /**
     * 根据事件的类型移除粘性事件
     *
     * @param clazz 事件的类型,这个方法可以在任何地方调用
     */
    public static void removeStickyEventByClass(Class clazz) {
        EventBus.getDefault().removeStickyEvent(clazz);
    }

    /**
     * 移除所有的粘性事件
     */
    public static void removeAllStickyEvents() {
        EventBus.getDefault().removeAllStickyEvents();
    }

    /**
     * 使用AsyncExecutor去执行任务
     *
     * @param runnableEx 继承自RunnableEx的任务类
     */
    public static void execute(AsyncExecutor.RunnableEx runnableEx) {
        if (asyncExecutor == null) {
            asyncExecutor = AsyncExecutor.create();
        }
        asyncExecutor.execute(runnableEx);
    }


}
