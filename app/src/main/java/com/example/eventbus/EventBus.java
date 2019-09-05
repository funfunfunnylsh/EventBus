package com.example.eventbus;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class EventBus {

    private Map<Object, List<SubscribeMethod>> cacheMap;

    private static volatile EventBus instance;

    private EventBus(){
        cacheMap = new HashMap<>();
    }

    public static EventBus getDefault() {
        if(instance == null){
            synchronized (EventBus.class){
                if(instance == null){
                    instance = new EventBus();
                }
            }
        }
        return  instance;
    }

    public  void regist(Object subject) {
        List<SubscribeMethod> methodList = cacheMap.get(subject);
        if(null == methodList){
            methodList = new ArrayList<>();
            //由反射机制获取所有方法
            Class<?> aClass = subject.getClass();
            while (null != aClass){
                //找父类的时候，需要先判断是否是系统级别的父类
                String name = aClass.getName();
                if(name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.")){
                    break;
                }
                Method[] methods = aClass.getDeclaredMethods();
                //遍历获取被注解的方法存入List
                for (int i = 0; i <methods.length; i++) {
                    Subscribe subscribe = methods[i].getAnnotation(Subscribe.class);
                    if(null == subscribe){
                        continue;
                    }

                    //判断带有Subscribe注解方法中参数类型
                    Class<?>[] types = methods[i].getParameterTypes();
                    if(types.length != 1){
                        Log.e("错误","EventBus only accept one parameter");
                    }

                    ThreadMode threadMode= subscribe.threadMode();

                    SubscribeMethod subscribeMethod = new SubscribeMethod(methods[i], threadMode, types[0]);
                    methodList.add(subscribeMethod);

                }
                aClass = aClass.getSuperclass();
            }
            cacheMap.put(subject,methodList);
        }
    }


    public void post(Object object) {
        Iterator<Object> iterator = cacheMap.keySet().iterator();
        while (iterator.hasNext()){
            Object next = iterator.next();
            List<SubscribeMethod> subscribeMethods = cacheMap.get(next);
            for (SubscribeMethod method : subscribeMethods) {
                //类信息是否对应
                if(method.getType().isAssignableFrom(object.getClass())){
                    try {
                        method.getmMethod().invoke(next,object);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


}
