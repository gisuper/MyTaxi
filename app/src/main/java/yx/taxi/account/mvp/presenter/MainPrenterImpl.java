package yx.taxi.account.mvp.presenter;

import yx.taxi.account.mvp.model.IAccountManager;
import yx.taxi.account.mvp.model.LoginResult;
import yx.taxi.account.mvp.view.IMainView;
import yx.taxi.common.databus.RegisterBus;

/**
 * Created by yangxiong on 2018/5/5/005.
 */

public class MainPrenterImpl implements IMainPresenter {
    private static final String TAG = "MainPrenterImpl";
    private IMainView view;
    private IAccountManager manager;

    public MainPrenterImpl(IMainView view, IAccountManager manager){

        this.view = view;
        this.manager = manager;
    }

    @RegisterBus
    public void login(LoginResult result){
        switch (result.getCode()){
            case IAccountManager.LOGIN_SUC:
                view.loginSuc();
                break;
            case IAccountManager.TOKEN_INVALID:
                view.showInput();
                break;
            case IAccountManager.SERVER_FAIL:
                view.showError(IAccountManager.SERVER_FAIL,"server error");
                break;
        }
    }

    @Override
    public void checkLoginStateByToken() {
        manager.loginByToken();
    }

}
