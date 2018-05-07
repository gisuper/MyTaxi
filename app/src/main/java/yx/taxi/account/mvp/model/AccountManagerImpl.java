package yx.taxi.account.mvp.model;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;

import rx.functions.Func1;
import yx.taxi.TaxiApplication;
import yx.taxi.account.response.Account;
import yx.taxi.account.response.LoginResponse;
import yx.taxi.common.api.API;
import yx.taxi.common.databus.RxBus;
import yx.taxi.common.http.IHttpClient;
import yx.taxi.common.http.IRequest;
import yx.taxi.common.http.IResponse;
import yx.taxi.common.http.biz.BaseBizResponse;
import yx.taxi.common.http.impl.OkHttpClientImpl;
import yx.taxi.common.http.impl.RequestImpl;
import yx.taxi.common.http.impl.ResponseImpl;
import yx.taxi.common.storage.SharedPreferencesDao;
import yx.taxi.common.util.DevUtil;

/**
 * Created by yangxiong on 2018/5/3/003.
 */

public class AccountManagerImpl implements IAccountManager {
    private static final String TAG = "AccountManagerImpl";
    private Handler mHandler;
    private IHttpClient mHttpClient;

    public AccountManagerImpl(IHttpClient mIHttpClient) {

        this.mHttpClient = mIHttpClient;
    }

    public void setmHandler(Handler mHandler) {

        this.mHandler = mHandler;
    }

    @Override
    public void fetchSMSCode(final String phone) {
        RxBus.getInstance( ).chainProcess(new Func1( ) {
            @Override
            public Object call(Object o) {
                SmsCodeResult smsCodeResult = new SmsCodeResult( );
                String url = API.Config.getDomain( ) + API.GET_SMS_CODE;
                IHttpClient client = new OkHttpClientImpl( );
                IRequest request = new RequestImpl(url);
                request.setBody("phone", phone);
                IResponse response = client.get(request, false);
                int code = response.getCode( );
                if (code == ResponseImpl.STATE_OK) {
                    BaseBizResponse bizRes =
                            new Gson( ).fromJson(response.getData( ), BaseBizResponse.class);
                    if (bizRes.getCode( ) == BaseBizResponse.STATE_OK) {
                        smsCodeResult.setCode(BaseBizResponse.STATE_OK);
                    } else {
                        smsCodeResult.setCode(SMS_SEND_FAIL);
                    }
                } else {
                    smsCodeResult.setCode(SMS_SEND_FAIL);
                }

                return smsCodeResult;
            }
        });

    }

    @Override
    public void checkSmsCode(final String phone, final String smsCode) {
        RxBus.getInstance( ).chainProcess(new Func1( ) {
            @Override
            public Object call(Object o) {
                SmsCodeResult smsCodeResult = new SmsCodeResult( );

                String url = API.Config.getDomain( ) + API.CHECK_SMS_CODE;
                IHttpClient client = new OkHttpClientImpl( );
                IRequest request = new RequestImpl(url);
                request.setBody("phone", phone);
                request.setBody("code", smsCode);
                IResponse response = client.get(request, false);
                int code = response.getCode( );
                if (code == ResponseImpl.STATE_OK) {
                    BaseBizResponse bizRes =
                            new Gson( ).fromJson(response.getData( ), BaseBizResponse.class);
                    if (bizRes.getCode( ) == BaseBizResponse.STATE_OK) {
                        smsCodeResult.setCode(SMS_CHECK_SUC);
                    } else {
                        smsCodeResult.setCode(SMS_CHECK_FAIL);
                    }
                } else {
                    smsCodeResult.setCode(SMS_CHECK_FAIL);
                }


                return smsCodeResult;
            }
        });
    }

    @Override
    public void checkUserExist(final String phone) {
        RxBus.getInstance( ).chainProcess(new Func1( ) {
            @Override
            public Object call(Object o) {
                UserExistResult userExistResult = new UserExistResult( );

                {
                    String url = API.Config.getDomain( ) + API.CHECK_USER_EXIST;
                    IRequest request = new RequestImpl(url);
                    request.setBody("phone", phone);
                    IResponse response = mHttpClient.get(request, false);
                    Log.d(TAG, response.getData( ));
                    if (response.getCode( ) == ResponseImpl.STATE_OK) {
                        BaseBizResponse bizRes =
                                new Gson( ).fromJson(response.getData( ), BaseBizResponse.class);
                        if (bizRes.getCode( ) == BaseBizResponse.STATE_USER_EXIST) {
                            userExistResult.setCode(USER_EXIST);
                        } else if (bizRes.getCode( ) == BaseBizResponse.STATE_USER_NOT_EXIST) {
                            userExistResult.setCode(USER_NOT_EXIST);
                        }
                    } else {
                        userExistResult.setCode(SERVER_FAIL);
                    }

                }


                return userExistResult;
            }
        });


    }

    @Override
    public void register(final String phonePhone, final String password) {
        RxBus.getInstance( ).chainProcess(new Func1( ) {
            @Override
            public Object call(Object o) {
                RegisterResult registerResult = new RegisterResult( );

                {
                    String url = API.Config.getDomain( ) + API.REGISTER;
                    IRequest request = new RequestImpl(url);
                    request.setBody("phone", phonePhone);
                    request.setBody("password", password);
                    request.setBody("uid", DevUtil.UUID(TaxiApplication.geTaxiApplication( )));

                    IResponse response = mHttpClient.post(request, false);
                    Log.d(TAG, response.getData( ));
                    if (response.getCode( ) == ResponseImpl.STATE_OK) {
                        BaseBizResponse bizRes =
                                new Gson( ).fromJson(response.getData( ), BaseBizResponse.class);
                        if (bizRes.getCode( ) == BaseBizResponse.STATE_OK) {
                            registerResult.setCode(REGISTER_SUC);
                        } else {
                            registerResult.setCode(SERVER_FAIL);
                        }
                    } else {
                        registerResult.setCode(SERVER_FAIL);
                    }

                }

                return registerResult;
            }
        });


    }

    @Override
    public void login(final String mPhoneStr, final String password) {
        RxBus.getInstance( ).chainProcess(new Func1( ) {
            @Override
            public Object call(Object o) {
                LoginResult loginResult = new LoginResult( );

                {
                    String url = API.Config.getDomain( ) + API.LOGIN;
                    IRequest request = new RequestImpl(url);
                    request.setBody("phone", mPhoneStr);
                    request.setBody("password", password);


                    IResponse response = mHttpClient.post(request, false);
                    Log.d(TAG, response.getData( ));
                    if (response.getCode( ) == ResponseImpl.STATE_OK) {
                        LoginResponse bizRes =
                                new Gson( ).fromJson(response.getData( ), LoginResponse.class);
                        if (bizRes.getCode( ) == BaseBizResponse.STATE_OK) {
                            // 保存登录信息
                            Account account = bizRes.getData( );
                            SharedPreferencesDao dao =
                                    new SharedPreferencesDao(TaxiApplication.geTaxiApplication( ), SharedPreferencesDao.FILE_ACCOUNT);
                            dao.save(SharedPreferencesDao.KEY_ACCOUNT, account);

                            // 通知 UI
                            loginResult.setCode(LOGIN_SUC);
                        } else {
                            loginResult.setCode(SERVER_FAIL);
                        }
                    } else {
                        loginResult.setCode(SERVER_FAIL);
                    }

                }

                return loginResult;
            }
        });

    }

    @Override
    public void loginByToken() {


        // 获取本地登录信息

        SharedPreferencesDao dao =
                new SharedPreferencesDao(TaxiApplication.geTaxiApplication( ),
                        SharedPreferencesDao.FILE_ACCOUNT);
        final Account account =
                (Account) dao.get(SharedPreferencesDao.KEY_ACCOUNT, Account.class);


        boolean isTokenValid = false;
        if (account != null) {
            if (account.getExpired( ) > System.currentTimeMillis( )) {
                // token 有效
                isTokenValid = true;
            }
        }
        //token信息有效 请求网络完成自动登录
        // 请求网络，完成自动登录
        final boolean finalIsTokenValid = isTokenValid;
        RxBus.getInstance( ).chainProcess(new Func1( ) {
            @Override
            public Object call(Object o) {
                LoginResult loginResult = new LoginResult( );

                if (finalIsTokenValid) {
                    String url = API.Config.getDomain( ) + API.LOGIN_BY_TOKEN;
                    IRequest request = new RequestImpl(url);
                    request.setBody("token", account.getToken( ));
                    IResponse response = mHttpClient.post(request, false);
                    Log.d(TAG, response.getData( ));
                    if (response.getCode( ) == ResponseImpl.STATE_OK) {
                        LoginResponse bizRes =
                                new Gson( ).fromJson(response.getData( ), LoginResponse.class);
                        if (bizRes.getCode( ) == BaseBizResponse.STATE_OK) {
                            // 保存登录信息
                            Account account = bizRes.getData( );
                            // todo: 加密存储
                            SharedPreferencesDao dao =
                                    new SharedPreferencesDao(TaxiApplication.geTaxiApplication( ),
                                            SharedPreferencesDao.FILE_ACCOUNT);
                            String value = new Gson( ).toJson(account);
                            Log.d(TAG, "run: " + value);
                            dao.save(SharedPreferencesDao.KEY_ACCOUNT, account);


                            loginResult.setCode(IAccountManager.LOGIN_SUC);
                        }
                        if (bizRes.getCode( ) == BaseBizResponse.STATE_TOKEN_INVALID) {
                            loginResult.setCode(IAccountManager.TOKEN_INVALID);
                        }
                    } else {
                        loginResult.setCode(IAccountManager.SERVER_FAIL);
                    }

                } else {//过期 跳转到电话输入界面登陆框或注册框
                    loginResult.setCode(IAccountManager.TOKEN_INVALID);
                }

                return loginResult;
            }

        });
    }
}
