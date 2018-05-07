import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by yangxiong on 2018/5/5/005.
 */

@RunWith(AndroidJUnit4.class)
public class TestRxJava {
    @Test
    public void testMapInAndroid() {
        Observable.just("yangxiong") //数据输入
                .subscribeOn(Schedulers.io( )) //指定下一个流处理的线程
                .map(new Func1<String, User>( ) {//数据处理
                    @Override
                    public User call(String s) {// 处理完返回给下一个使用
                        System.out.println("process User call in tread:" +
                                Thread.currentThread( ).getName( ));
                        Log.e("TestRxJava", "call: "+Thread.currentThread( ).getName( ));
                        return new User(18, s);
                    }
                }).subscribeOn(Schedulers.io())
                .map(new Func1<User, User>( ) {
                    @Override
                    public User call(User user) {
                        user.setAge(11);
                        return user;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread( )) // 指定消费的线程
                .subscribe(new Action1<User>( ) {//拿到处理后返回的数据
                               @Override
                               public void call(User user) {
                                   System.out.println("receive User call in tread:"
                                           + Thread.currentThread( ).getName( ));
                                   Log.e("TestRxJava", "call: "+user.getName( ));
                                   Log.e("TestRxJava", "call: "+Thread.currentThread( ).getName( ));
                                   Log.e("TestRxJava", "call: "+user.getAge( ));
                               }
                           }
                );
    }

    class User {
        private int age;
        private String name;

        public User(int age, String name) {
            this.age = age;
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
