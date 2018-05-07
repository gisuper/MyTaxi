package yx.taxi.account.mvp.presenter;

import yx.taxi.account.mvp.model.IAccountManager;
import yx.taxi.account.mvp.model.SmsCodeResult;
import yx.taxi.account.mvp.model.UserExistResult;
import yx.taxi.account.mvp.view.ISmsCodeDialogView;
import yx.taxi.common.databus.RegisterBus;

/**
 * Created by yangxiong on 2018/5/3/003.
 */

public class SmsCodeDialogPresenterImpl implements ISmsCodeDialogPresenter {
    private static final String TAG = "SmsCodeDialogP";
    private ISmsCodeDialogView smsCodeDialogView;
    private IAccountManager accountManager;

    public SmsCodeDialogPresenterImpl(ISmsCodeDialogView smsCodeDialogView, IAccountManager accountManager){

        this.smsCodeDialogView = smsCodeDialogView;
        this.accountManager = accountManager;
    }

    @RegisterBus
    public void smsCode(SmsCodeResult result){
        switch (result.getCode()){
            case IAccountManager.SMS_SEND_SUC:
                smsCodeDialogView.showCountDownTimer();
                break;
            case IAccountManager.SMS_SEND_FAIL:
                smsCodeDialogView.showError(IAccountManager.SMS_SEND_FAIL, "");
                break;
            case IAccountManager.SMS_CHECK_SUC:
               smsCodeDialogView.showSmsCodeCheckState(true);
                break;
            case IAccountManager.SMS_CHECK_FAIL:
                smsCodeDialogView.showError(IAccountManager.SMS_CHECK_FAIL, "");
                break;
            case IAccountManager.SERVER_FAIL:
                smsCodeDialogView.showError(IAccountManager.SERVER_FAIL, "");
                break;
        }
    }

    @RegisterBus
    public void userExist(UserExistResult result){
        switch (result.getCode()){
            case IAccountManager.USER_EXIST:
               smsCodeDialogView.showUserExist(true);
                break;
            case IAccountManager.USER_NOT_EXIST:
               smsCodeDialogView.showUserExist(false);
                break;
            case IAccountManager.SERVER_FAIL:
               smsCodeDialogView.showError(IAccountManager.SERVER_FAIL, "");
                break;
        }
    }
    
    @Override
    public void requestSendSmsCode(String phone) {
        accountManager.fetchSMSCode(phone);
    }

    @Override
    public void requestCheckSmsCode(String phone, String smsCode) {
        accountManager.checkSmsCode(phone,smsCode);
    }

    @Override
    public void requestCheckUserExist(String phone) {
        accountManager.checkUserExist(phone);
    }
}
