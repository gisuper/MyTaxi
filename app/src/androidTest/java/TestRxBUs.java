import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import rx.functions.Func1;
import yx.taxi.common.databus.RegisterBus;
import yx.taxi.common.databus.RxBus;

/**
 * Created by yangxiong on 2018/5/7/007.
 */

@RunWith(AndroidJUnit4.class)
public class TestRxBUs {
    private static final String TAG = "TestRxBus";
    Presenter presenter;
    @Before
    public void setup(){
        presenter = new Presenter(new Manager());
        RxBus.getInstance().register(presenter);
    }
    @Test
    public void getUser(){
        presenter.getUser();
    }

    @Test
    public void getOrder(){
        presenter.getOrder();
    }

    /**
     *  模拟 Presenter
     */
    class Presenter {
        Manager manager ;


        public Presenter(Manager manager) {
            this.manager = manager;
        }

        public void getUser(){
            manager.getUser();
        }

        public void getOrder(){
            manager.getOrder();
        }

        /////数据返回

        @RegisterBus
         public void onUser(User user){
            Log.e(TAG, "onUser: " +user.name );
         }
        @RegisterBus
        public void onOrder(Order order){
            Log.e(TAG, "onOrder: " +order.name );
        }
    }


    /**
     *  模拟 MODEL,
     */
    class Manager {
        public void getUser(){
            RxBus.getInstance().chainProcess(new Func1( ) {
                @Override
                public Object call(Object o) {
                    return new User();
                }
            });
        }

        public void getOrder(){
            RxBus.getInstance().chainProcess(new Func1( ) {
                @Override
                public Object call(Object o) {
                    return new Order();
                }
            });
        }
    }


    class User {
public String name = "User";
    }

    class Order {
        public String name = "Order";
    }
}
