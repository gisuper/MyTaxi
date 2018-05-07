package yx.taxi.account.mvp.presenter;

import yx.taxi.account.mvp.model.IAccountManager;
import yx.taxi.account.mvp.model.LoginResult;
import yx.taxi.account.mvp.view.ILoginView;
import yx.taxi.common.databus.RegisterBus;

/**
 * Created by yangxiong on 2018/5/4/004.
 */

public class LoginDialogPresenterImpl implements ILoginDialogPresenter {
    private static final String TAG ="LoginPresenter" ;
    private ILoginView view;
    private IAccountManager accountManager;

    public LoginDialogPresenterImpl(ILoginView view, IAccountManager accountManager) {
        this.view = view;
        this.accountManager = accountManager;

    }

    @Override
    public void requestLogin(String mPhoneStr, String password) {
        accountManager.login(mPhoneStr,password);
    }

    @RegisterBus
    public void login(LoginResult result ){
        switch (result.getCode()){
            case IAccountManager.LOGIN_SUC:
                view.showLoginSuc();
                break;
        }
    }

}
