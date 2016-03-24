/*
 * Copyright (C) 2012 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.greenrobot.event;

import android.util.Log;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 订阅者方法寻找类
 */
class SubscriberMethodFinder {
    //方法前缀
    private static final String ON_EVENT_METHOD_NAME = "onEvent";

    /*
     * In newer class files, compilers may add methods. Those are called bridge or synthetic methods.
     * EventBus must ignore both. There modifiers are not public but defined in the Java class file format:
     * http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.6-200-A.1
     */
    private static final int BRIDGE = 0x40;
    private static final int SYNTHETIC = 0x1000;

    private static final int MODIFIERS_IGNORE = Modifier.ABSTRACT | Modifier.STATIC | BRIDGE | SYNTHETIC;
    //储存一个key为订阅者,value为这个订阅者里边所有的订阅事件的方法描述的集合
    private static final Map<Class<?>, List<SubscriberMethod>> methodCache = new HashMap<Class<?>, List<SubscriberMethod>>();

    private final Map<Class<?>, Class<?>> skipMethodVerificationForClasses;

    SubscriberMethodFinder(List<Class<?>> skipMethodVerificationForClassesList) {
        skipMethodVerificationForClasses = new ConcurrentHashMap<Class<?>, Class<?>>();
        if (skipMethodVerificationForClassesList != null) {
            for (Class<?> clazz : skipMethodVerificationForClassesList) {
                skipMethodVerificationForClasses.put(clazz, clazz);
            }
        }
    }

    /**
     * 通过反射获取订阅者中的onEvent开头的方法
     * @param subscriberClass 订阅者
     * @return
     */
    List<SubscriberMethod> findSubscriberMethods(Class<?> subscriberClass) {
        List<SubscriberMethod> subscriberMethods;
        //先获取缓存中是否有这个订阅者对应的订阅方法描述,同步获取,避免异步造成的数据不统一
        synchronized (methodCache) {
            subscriberMethods = methodCache.get(subscriberClass);
        }
        if (subscriberMethods != null) {
            return subscriberMethods;
        }
        subscriberMethods = new ArrayList<SubscriberMethod>();
        Class<?> clazz = subscriberClass;
        HashMap<String, Class> eventTypesFound = new HashMap<String, Class>();
        StringBuilder methodKeyBuilder = new StringBuilder();
        while (clazz != null) {
            String name = clazz.getName();
            if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.")) {
                // Skip system classes, this just degrades performance
                break;
            }

            // Starting with EventBus 2.2 we enforced methods to be public (might change with annotations again)
            try {
                // This is faster than getMethods, especially when subscribers a fat classes like Activities
                Method[] methods = clazz.getDeclaredMethods();
                filterSubscriberMethods(subscriberMethods, eventTypesFound, methodKeyBuilder, methods);
            } catch (Throwable th) {
                // Workaround for java.lang.NoClassDefFoundError, see https://github.com/greenrobot/EventBus/issues/149
                Method[] methods = subscriberClass.getMethods();
                subscriberMethods.clear();
                eventTypesFound.clear();
                filterSubscriberMethods(subscriberMethods, eventTypesFound, methodKeyBuilder, methods);
                break;
            }
            clazz = clazz.getSuperclass();
        }
        if (subscriberMethods.isEmpty()) {
            throw new EventBusException("Subscriber " + subscriberClass + " has no public methods called "
                    + ON_EVENT_METHOD_NAME);
        } else {
            synchronized (methodCache) {
                methodCache.put(subscriberClass, subscriberMethods);
            }
            return subscriberMethods;
        }
    }

    /**
     * 找到订阅者中的onEvent方法,
     * 1.方法必须以onEvent开头
     * 2.方法的修饰符必须为public
     * @param subscriberMethods  订阅者中的事件描述类的集合
     * @param eventTypesFound    储存事件类型的map,key为EventBus订阅事件的方法名>所订阅的事件的名称(eg:onEvent>MessageEvent)
     * @param methodKeyBuilder   用于生成方法描述
     * @param methods            订阅者中的所有方法
     */
    private void filterSubscriberMethods(List<SubscriberMethod> subscriberMethods,
                                         HashMap<String, Class> eventTypesFound, StringBuilder methodKeyBuilder,
                                         Method[] methods) {
        for (Method method : methods) {
            String methodName = method.getName();
            if (methodName.startsWith(ON_EVENT_METHOD_NAME)) {
                int modifiers = method.getModifiers();
                Class<?> methodClass = method.getDeclaringClass();
                //过滤出来public方法
                if ((modifiers & Modifier.PUBLIC) != 0 && (modifiers & MODIFIERS_IGNORE) == 0) {
                    //获取参数
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    //参数只能是一个
                    if (parameterTypes.length == 1) {
                        //得到线程模式
                        ThreadMode threadMode = getThreadMode(methodClass, method, methodName);
                        //得不到就遍历下一个
                        if (threadMode == null) {
                            continue;
                        }
                        //获取Event的类型
                        Class<?> eventType = parameterTypes[0];
                        //清空StringBuilder
                        methodKeyBuilder.setLength(0);
                        //追加方法名(onEvent...)
                        methodKeyBuilder.append(methodName);
                        //追加类名(具体的订阅事件的类名)
                        methodKeyBuilder.append('>').append(eventType.getName());
                        //生成方法的key
                        String methodKey = methodKeyBuilder.toString();
                        //添加到map中,储存事件类型的map,key为EventBus订阅事件的方法名>所订阅的事件的名称(eg:onEvent>MessageEvent),value为订阅者class
                        Class methodClassOld = eventTypesFound.put(methodKey, methodClass);
                        //如果在map中没有这个key的值或者要添加的类是key对应的旧值的子类就加入集合中,
                        //也就是如果父类和子类如果同时都定义了同样线程模式,同样事件的话,只有子类会加入结合中
                        if (methodClassOld == null || methodClassOld.isAssignableFrom(methodClass)) {
                            // Only add if not already found in a sub class
                            subscriberMethods.add(new SubscriberMethod(method, threadMode, eventType));
                        } else {
                            // Revert the put, old class is further down the class hierarchy
                            eventTypesFound.put(methodKey, methodClassOld);
                        }
                    }
                } else if (!skipMethodVerificationForClasses.containsKey(methodClass)) {
                    Log.d(EventBus.TAG, "Skipping method (not public, static or abstract): " + methodClass + "."
                            + methodName);
                }
            }
        }
    }

    private ThreadMode getThreadMode(Class<?> clazz, Method method, String methodName) {
        //截取onEvent方法,获得onEvent之后的部分
        String modifierString = methodName.substring(ON_EVENT_METHOD_NAME.length());
        ThreadMode threadMode;
        //如果截取后没有是发布线程模式
        if (modifierString.length() == 0) {
                threadMode = ThreadMode.PostThread;
        //主线程
        } else if (modifierString.equals("MainThread")) {
            threadMode = ThreadMode.MainThread;
        //子线程
        } else if (modifierString.equals("BackgroundThread")) {
            threadMode = ThreadMode.BackgroundThread;
        //异步线程
        } else if (modifierString.equals("Async")) {
            threadMode = ThreadMode.Async;
        } else {
            if (!skipMethodVerificationForClasses.containsKey(clazz)) {
                throw new EventBusException("Illegal onEvent method, check for typos: " + method);
            } else {
                threadMode = null;
            }
        }
        return threadMode;
    }

    static void clearCaches() {
        synchronized (methodCache) {
            methodCache.clear();
        }
    }

}
