package yx.taxi.common.databus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by yangxiong on 2018/5/7/007.
 */

public class RxBus {
    private static RxBus instance = null;
    private Set<Object> subscribers;

    private RxBus() {
        subscribers= new CopyOnWriteArraySet<>();
    }
    public static RxBus getInstance() {
        if (instance == null) {
            synchronized (RxBus.class) {
                if (instance == null) {
                    instance = new RxBus( );
                }
            }
        }
        return instance;
    }

    public synchronized void register(Object  subscriber){
        subscribers.add(subscriber);
    }
    public synchronized void unRegister(Object  subscriber){
        subscribers.remove(subscriber);
    }
    public void chainProcess(Func1 func1){
        Observable.just("")
                .subscribeOn(Schedulers.io())
                .map(func1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1( ) {
                    @Override
                    public void call(Object data) {
                        for (Object subscriber : subscribers) {
                            callMethodByAnnotiation(subscriber,data);
                        }
                    }
                });
    }
    /**
     * 反射获取对象方法列表，判断：
     * 1 是否被注解修饰
     * 2 参数类型是否和 data 类型一致
     * @param target
     * @param data
     */
    private void callMethodByAnnotiation(Object target, Object data) {
        Method[] methodArray = target.getClass().getDeclaredMethods();
        for (int i = 0; i < methodArray.length; i++) {
            try {
                if (methodArray[i].isAnnotationPresent(RegisterBus.class)) {
                    // 被 @RegisterBus 修饰的方法
                    Class paramType = methodArray[i].getParameterTypes()[0];
                    if (data.getClass().getName().equals(paramType.getName())) {
                        // 参数类型和 data 一样，调用此方法
                        methodArray[i].invoke(target, new Object[]{data});
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}