package kr.co.roonets.adesk;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import kr.co.roonets.adesk.component.BaseAppCompatActivity;
import kr.co.roonets.adesk.utils.DialogUtil;
import kr.co.roonets.adesk.utils.PreferenceUtil;

public class LoginActivity extends BaseAppCompatActivity {

    static String TAG = LoginActivity.class.getSimpleName();

    protected EditText etLoginID, etLoginPassword;
    protected CheckBox cbAutoLogin;
    protected Button btnLogin;
    private PreferenceUtil mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Handle Toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("로그인설정");
        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        setSupportActionBar(toolbar);

        mPref = PreferenceUtil.getInstance();

        etLoginID = (EditText) findViewById(R.id.et_login_id);
        etLoginPassword = (EditText) findViewById(R.id.et_login_password);
        etLoginPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (v.getId() == etLoginPassword.getId() && actionId == EditorInfo.IME_ACTION_DONE) {
                }

                return false;
            }
        });

        cbAutoLogin = (CheckBox) findViewById(R.id.cb_auto_login);

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProcess();
            }
        });


        //자동로그인 설정값 불러오기
        final Boolean boolAutoLogin = Boolean.valueOf(mPref.getString(PreferenceUtil.PREF_AUTO_LOGIN));
        cbAutoLogin.setChecked(boolAutoLogin);

        String loginID = mPref.getString(PreferenceUtil.PREF_LOGIN_ID);
        String loginPW = mPref.getString(PreferenceUtil.PREF_LOGIN_PASSWD);

        if (loginID != null && loginID != "") {
            etLoginID.setText(loginID);
        }

        if (loginPW != null && loginPW != "") {
            etLoginPassword.setText(loginPW);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle the click on the back arrow click
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkAutoLogin(boolean autoLogin) {
        if (autoLogin) {
            mPref = PreferenceUtil.getInstance();
            String loginID = mPref.getString(PreferenceUtil.PREF_LOGIN_ID);
            String loginPW = mPref.getString(PreferenceUtil.PREF_LOGIN_PASSWD);

            etLoginID.setText(loginID);
            etLoginPassword.setText(loginPW);

            loginProcess();
        } else {
            //showLoginLayout();
        }
    }

    protected void loginProcess() {

        final String loginId = etLoginID.getText().toString().trim();
        final String loginPw = etLoginPassword.getText().toString().trim();
        if (TextUtils.isEmpty(loginId) || TextUtils.isEmpty(loginPw)) {
            DialogUtil.showErrorDialog(LoginActivity.this, "로그인 정보를 입력해주시기 바랍니다");
            return;
        }

        mPref = PreferenceUtil.getInstance();
        mPref.setString(PreferenceUtil.PREF_LOGIN_ID, loginId);
        mPref.setString(PreferenceUtil.PREF_LOGIN_PASSWD, loginPw);
        boolean autoLogin;

        if (cbAutoLogin.isChecked()) {
            autoLogin = true;
            mPref.setString(PreferenceUtil.PREF_AUTO_LOGIN, "true");
        } else {
            autoLogin = false;
            mPref.setString(PreferenceUtil.PREF_AUTO_LOGIN, "false");
        }

        DialogUtil.showSaveInfo(LoginActivity.this, autoLogin);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
