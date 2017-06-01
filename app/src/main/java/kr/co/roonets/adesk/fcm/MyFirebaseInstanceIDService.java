package kr.co.roonets.adesk.fcm;

import android.telephony.TelephonyManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import kr.co.roonets.adesk.constant.NetworkConstant;
import kr.co.roonets.adesk.utils.PreferenceUtil;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private PreferenceUtil mPref;
    private TelephonyManager telephonyManager;

    private static String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    /*
    * 새로 토큰이 업데이트 되었을 때 여기로 Callback이 옵니다 (최초설치하거나, 지웠다 다시 깔아도 실행됨)
    */
    @Override
    public void onTokenRefresh() {

        PreferenceUtil.initializeInstance(getApplicationContext());
        mPref = PreferenceUtil.getInstance();

        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();
        mPref.setString(PreferenceUtil.PREF_TOKEN, token);
        mPref.setString(PreferenceUtil.PREF_SEND_INFO, "N");
        mPref.setString(PreferenceUtil.PREF_LOGIN_ID, "");
        mPref.setString(PreferenceUtil.PREF_LOGIN_PASSWD, "");
        mPref.setString(PreferenceUtil.PREF_AUTO_LOGIN, "false");

        // 생성등록된 토큰을 개인 앱서버에 보내 저장해 두었다가 추가로 뭔가를 하고싶으면 할 수 있도록 한다.
        // sendRegistrationToServer(token);
    }

    public void sendRegistrationToServer(String token, String imei_number, String phone_number) {   // TODO I2UrlHelper 로 처리할 지 뒤에 고민
        // Add custom implementation, as needed.

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Token", token)
                .add("Imei", imei_number)
                .add("Phone_number", phone_number)
                .build();

        //request
        Request request = new Request.Builder()
                .url(NetworkConstant.SERVER_HOST + NetworkConstant.TOKEN_REGIST_URL)
                .post(body)
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}