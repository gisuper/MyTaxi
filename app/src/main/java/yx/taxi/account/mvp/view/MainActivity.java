package yx.taxi.account.mvp.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.yangxiong.mytaxi.R;

import yx.taxi.account.PhoneInputDialog;
import yx.taxi.account.mvp.model.AccountManagerImpl;
import yx.taxi.account.mvp.presenter.IMainPresenter;
import yx.taxi.account.mvp.presenter.MainPrenterImpl;
import yx.taxi.common.databus.RxBus;
import yx.taxi.common.http.IHttpClient;
import yx.taxi.common.http.impl.OkHttpClientImpl;
import yx.taxi.common.util.ToastUtil;

public class MainActivity extends AppCompatActivity implements IMainView {

    private static final int REQUEST_READ_PHONE_STATE = 12312;
    private static final String TAG = "MainActivity";
    private IHttpClient mOkHttpClient;
    private IMainPresenter mPrenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mOkHttpClient = new OkHttpClientImpl( );
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            //TODO
        }

        mPrenter = new MainPrenterImpl(this,new AccountManagerImpl(mOkHttpClient));
        RxBus.getInstance().register(mPrenter);
        mPrenter.checkLoginStateByToken();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy( );
        RxBus.getInstance().unRegister(mPrenter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                }
                break;

            default:
                break;
        }
    }


    private void showPhoneInputDlg() {
        PhoneInputDialog dlg = new PhoneInputDialog(this);
        dlg.show();
    }


    @Override
    public void showLoading() {

    }

    @Override
    public void showError(int Code, String msg) {
        ToastUtil.show(MainActivity.this,
                getString(R.string.error_server));
    }

    @Override
    public void loginSuc() {
        ToastUtil.show(MainActivity.this,
                getString(R.string.login_suc));
    }

    @Override
    public void showInput() {
        showPhoneInputDlg();
    }
}
