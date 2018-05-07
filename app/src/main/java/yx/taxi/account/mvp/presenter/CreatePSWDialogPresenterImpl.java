package yx.taxi.account.mvp.presenter;

import yx.taxi.account.mvp.model.IAccountManager;
import yx.taxi.account.mvp.model.LoginResult;
import yx.taxi.account.mvp.model.RegisterResult;
import yx.taxi.account.mvp.view.ICreatePSWDialogView;
import yx.taxi.common.databus.RegisterBus;

/**
 * Created by yangxiong on 2018/5/4/004.
 */

public class CreatePSWDialogPresenterImpl implements ICreatePSWDialogPresenter {
    private ICreatePSWDialogView view;
    private IAccountManager manager;

    public CreatePSWDialogPresenterImpl(ICreatePSWDialogView view, IAccountManager manager){
        this.view = view;
        this.manager = manager;
    }
    @Override
    public void submitRegister(String phone, String name) {
        manager.register(phone,name);
    }

    @Override
    public void login(String phone,String psw) {
        manager.login(phone,psw);
    }

    @RegisterBus
    public void login(LoginResult result){
        switch (result.getCode()){
            case IAccountManager.LOGIN_SUC:
                view.loginSuc(true);
                break;
            case IAccountManager.SERVER_FAIL:
                view.showError(IAccountManager.PW_ERROR,"SERVER_FAIL");
                break;
        }
    }

    @RegisterBus
    public void register(RegisterResult result){
        switch (result.getCode()){
            case IAccountManager.REGISTER_SUC:
                view.showRegisterSuc(true);
                break;

            case IAccountManager.SERVER_FAIL:
               view.showError(IAccountManager.PW_ERROR,"SERVER_FAIL");
                break;
        }
    }

}
