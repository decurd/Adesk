package kr.co.roonets.adesk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.internal.LinkedTreeMap;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;
import kr.co.roonets.adesk.a.SectionA_mainFragment;
import kr.co.roonets.adesk.b.SectionB_mainFragment;
import kr.co.roonets.adesk.b.constant.Urls;
import kr.co.roonets.adesk.b.utils.NetworkUtil;
import kr.co.roonets.adesk.common.PermissionRequester;
import kr.co.roonets.adesk.component.BaseAppCompatActivity;
import kr.co.roonets.adesk.constant.AppConstant;
import kr.co.roonets.adesk.constant.CodeConstant;
import kr.co.roonets.adesk.constant.NetworkConstant;
import kr.co.roonets.adesk.i2api.I2ConnectApi;
import kr.co.roonets.adesk.i2api.I2ResponseParser;
import kr.co.roonets.adesk.i2api.I2UrlHelper;
import kr.co.roonets.adesk.utils.DialogUtil;
import kr.co.roonets.adesk.utils.FileUtil;
import kr.co.roonets.adesk.utils.PreferenceUtil;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends BaseAppCompatActivity {

    static String TAG = MainActivity.class.getSimpleName();

    private PreferenceUtil mPref;
    private Fragment currentfragment;
    private long backKeyPressedTime = 0;
    private final long FINISH_INTERVAL_TIME = 2000;
    private Toast toast;
    private List<LinkedTreeMap<String, Object>> mJsonMapMenu;
    public static Context mContext;
    public Drawer mDrawer = null;
    public final Handler mDrawerActionHandler = new Handler();
    private String mSection = "";
    private Intent intent = null;
    private final static String FRAGMENT_TAG = "FRAGMENTB_TAG";
    private Boolean mBoolAutoLogin = false;
    private TelephonyManager telephonyManager;
    private String IMEI_Number;
    private String PHONE_Number;
    private String TOKEN_Number;
    private String INFO_Send;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.activity_main);

        mContext = this;

        PreferenceUtil.initializeInstance(getApplicationContext());
        mPref = PreferenceUtil.getInstance();

        Intent intent = getIntent();
        if (intent != null) {       // TODO 푸쉬 메세지 데이터로 내부적인 처리
            String temp = intent.getStringExtra("message");
            Log.d(TAG, "onCreate: message = " + temp);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {   // 사용권한이 없을경우
            PermissionRequester.Builder requester = new PermissionRequester.Builder(MainActivity.this);
            requester.create().request(Manifest.permission.READ_PHONE_STATE, 10000, new PermissionRequester.OnClickDenyButtonListener() {
                @Override
                public void onClick(Activity activity) {
                    Toast.makeText(activity, "권한을 얻지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        //자동로그인 설정값 불러오기
        mBoolAutoLogin = Boolean.valueOf(mPref.getString(PreferenceUtil.PREF_AUTO_LOGIN));

        // 웹서비스에서 app의 사용 용도를 결정한다.
        // checkToXML();

        // Drawer Layout을 호출
        makeDrawerMenu();

        // FCM Token 전송
        sendFcmToken();

        initStartPage("B");         // A:Native Page, B:Web Page

    }

    public void beginAutoLogin(boolean boolAutoLogin) {
        if (boolAutoLogin) {
            mDrawerActionHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String loginID = mPref.getString(PreferenceUtil.PREF_LOGIN_ID);
                    String loginPW = mPref.getString(PreferenceUtil.PREF_LOGIN_PASSWD);
                    WebView mWebView = ((SectionB_mainFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG)).mWv;
                    try {
                        Log.e(TAG, "URI = ?uid="+loginID+"&upw="+loginPW+"&pno="+CodeConstant.PNO+"&loginType=mlogin&token="+mPref.getString(PreferenceUtil.PREF_TOKEN));
                        String str = "uid=" + URLEncoder.encode(loginID, "UTF-8") + "&loginPW=" + URLEncoder.encode(loginPW, "UTF-8")
                                + "&pno=" + URLEncoder.encode(CodeConstant.PNO, "UTF-8") + "&loginType=" + URLEncoder.encode("mlogin", "UTF-8")
                                + "&token" + URLEncoder.encode(mPref.getString(PreferenceUtil.PREF_TOKEN), "UTF-8");
                        mWebView.postUrl(Urls.AUTO_LOGIN_URL, str.getBytes());
                        //mWebView.loadUrl(Urls.AUTO_LOGIN_URL+"?uid="+loginID+"&upw="+loginPW+"&pno="+CodeConstant.PNO+"&loginType=mlogin&token="+TOKEN_Number);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }, 500);
        }
    }

    private void checkToXML() {
        DialogUtil.showCircularProgressDialog(MainActivity.this);

        I2ConnectApi.requestJSON2XML(MainActivity.this,
                I2UrlHelper.ADESK.getSelectInitPage())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<JSONObject>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "requestJSON onCompleted");
                        DialogUtil.removeCircularProgressDialog();

                        initStartPage(mSection);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError = " + e.getMessage());
                        //Error dialog 표시
                        DialogUtil.removeCircularProgressDialog();
                        e.printStackTrace();
                        DialogUtil.showErrorDialogWithValidateSession(MainActivity.this, e);
                    }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                        Log.d(TAG, "requestJSON onNext");
                        JSONObject statusInfo = I2ResponseParser.getStatusInfo(jsonObject);

                        if (statusInfo != null) {
                            try {
                                mSection = statusInfo.getString("RES_MSG");
                                if (mSection.equals("") || mSection == null) {
                                    mSection = "C";
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void sendFcmToken() {
        /**
         * 아래 구문이 실행되면 FCM 서버에 "주제" 에 추가됨
         * */
        FirebaseMessaging.getInstance().subscribeToTopic("event");
        FirebaseMessaging.getInstance().subscribeToTopic("notice");
        FirebaseMessaging.getInstance().subscribeToTopic("ad...");
        FirebaseMessaging.getInstance().subscribeToTopic("news");

    }

    @SuppressLint("HardwareIds")
    @Override
    public void onResume() {

        super.onResume();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            IMEI_Number = telephonyManager.getDeviceId();
            if (IMEI_Number == null) {
                IMEI_Number = "000000000000";
            }
            PHONE_Number = telephonyManager.getLine1Number();
            TOKEN_Number = mPref.getString(PreferenceUtil.PREF_TOKEN);
            INFO_Send = mPref.getString(PreferenceUtil.PREF_SEND_INFO);

            if ("".equals(TOKEN_Number) || TOKEN_Number == null || INFO_Send.equals("N")) {
                Log.e(TAG, "onResume: token=" + TOKEN_Number + ", imei=" + IMEI_Number + ", phone_no=" + PHONE_Number);

                if (NetworkUtil.isWifiNetworkConnect(this) || NetworkUtil.isMobileNetworkConnect(this)) {

                    new Thread() {
                        public void run() {
                            String savedToken = mPref.getString(PreferenceUtil.PREF_TOKEN);
                            OkHttpClient client = new OkHttpClient();
                            RequestBody body = new FormBody.Builder()
                                    .add("mtkey", savedToken)
                                    .add("dev_no", IMEI_Number)
                                    .add("hp_no", PHONE_Number)
                                    .add("pno", CodeConstant.PNO)
                                    .build();

                            Request request = new Request.Builder()
                                    .url(NetworkConstant.SERVER_HOST + NetworkConstant.TOKEN_REGIST_URL)
                                    .post(body)
                                    .build();


                            try {
                                Response responses = client.newCall(request).execute();
                                String xmlString = responses.body().string();
                                XmlToJson jsonString = new XmlToJson.Builder(xmlString).build();
                                JSONObject status = new JSONObject(jsonString.toString());
                                status = status.getJSONObject("FDESK");
                                if (status.getString("RES_MSG2").equals("OK")) {
                                    Log.e(TAG, "run: success");
                                    mPref.setString(PreferenceUtil.PREF_IMEI_NO, IMEI_Number);
                                    mPref.setString(PreferenceUtil.PREF_USR_PHONE, PHONE_Number);
                                    mPref.setString(PreferenceUtil.PREF_SEND_INFO, "Y");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "run: e = " + e.getLocalizedMessage() );
                                mPref.setString(PreferenceUtil.PREF_SEND_INFO, "N");
                                e.printStackTrace();
                            }
                        }
                    }.start();

                } else {
                    mPref.setString(PreferenceUtil.PREF_SEND_INFO, "N");
                    DialogUtil.showInformationDialog(this, "통신오류", "인터넷이 연결되지 않았습니다");
                }
            }
        }
    }

    private void initStartPage(String section) {
        DialogUtil.showCircularProgressDialog(MainActivity.this);
        if (section.equals("A")) {
            Fragment fragment = new SectionA_mainFragment();
            Bundle bundle = new Bundle();
            fragment.setArguments(bundle);
            changeMainFragment(fragment);
        } else if (section.equals("B")) {
            Fragment fragment = new SectionB_mainFragment();
            Bundle bundle = new Bundle();
            fragment.setArguments(bundle);
            changeMainFragment(fragment);
        } else if (section.equals("C")) {
            Fragment fragment = new SectionB_mainFragment();
            Bundle bundle = new Bundle();
            fragment.setArguments(bundle);
            changeMainFragment(fragment);
        }
        DialogUtil.removeCircularProgressDialog();
        Log.e(TAG, "initStartPage: mBoolAutoLogin=" + mBoolAutoLogin);
        beginAutoLogin(mBoolAutoLogin);
    }

    public void changeMainFragment(Fragment fragment) {

        if (fragment != null) {
            currentfragment = fragment;
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.main_fragment, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    public void makeDrawerMenu() {
        // TODO 현재 Json에서 가져오는 메뉴가 없으므로 향후 로직 변경을 해야 한다.
        if (AppConstant.MENU_LOCAL_JSON_ENABLED) {  // read from local
            //read from local json in assets
            Map<String, Object> status = FileUtil.readMenuFromAsset(MainActivity.this, "menu.json");
            Map<String, Object> statusInfo = (Map<String, Object>) status.get("statusInfo");
            mJsonMapMenu = (List<LinkedTreeMap<String, Object>>) statusInfo.get("list_menu");
            drawerMenu();
        } else { //read from server
            // getUpdateMenuFromServer();
        }
    }

    private void drawerMenu() {
        final ArrayList<IDrawerItem> drawerItems = new ArrayList<>();

        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withHeader(R.layout.nav_header)
                //.withHeader(R.layout.drawer_header)
                .withHasStableIds(true)
                .withItemAnimator(new AlphaCrossFadeAnimator())
                //.withDrawerItems(drawerItems)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("LOG IN").withIcon(GoogleMaterial.Icon.gmd_account_circle).withIdentifier(10).withSelectable(false),
                        new DividerDrawerItem(),
                        new ExpandableDrawerItem().withName("Reservation").withIsExpanded(true).withIcon(GoogleMaterial.Icon.gmd_assignment).withIdentifier(20).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName("호텔검색").withLevel(2).withIcon(GoogleMaterial.Icon.gmd_stars).withIdentifier(2001).withSelectable(false),
                                new SecondaryDrawerItem().withName("추천호텔").withLevel(2).withIcon(GoogleMaterial.Icon.gmd_stars).withIdentifier(2002).withSelectable(false),
                                new SecondaryDrawerItem().withName("오늘특가세일").withLevel(2).withIcon(GoogleMaterial.Icon.gmd_stars).withIdentifier(2003).withSelectable(false),
                                new SecondaryDrawerItem().withName("금주특가세일").withLevel(2).withIcon(GoogleMaterial.Icon.gmd_stars).withIdentifier(2004).withSelectable(false),
                                new SecondaryDrawerItem().withName("이벤트").withLevel(2).withIcon(GoogleMaterial.Icon.gmd_stars).withIdentifier(2005).withSelectable(false),
                                new SecondaryDrawerItem().withName("특별기획상품").withLevel(2).withIcon(GoogleMaterial.Icon.gmd_stars).withIdentifier(2006).withSelectable(false)
                        ),
                        new DividerDrawerItem(),
                        new ExpandableDrawerItem().withName("고객센터").withIcon(GoogleMaterial.Icon.gmd_help).withIdentifier(30).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName("공지사항").withLevel(2).withIcon(GoogleMaterial.Icon.gmd_stars).withIdentifier(3001).withSelectable(false),
                                new SecondaryDrawerItem().withName("FAQ").withLevel(2).withIcon(GoogleMaterial.Icon.gmd_stars).withIdentifier(3002).withSelectable(false),
                                new SecondaryDrawerItem().withName("회원문의").withLevel(2).withIcon(GoogleMaterial.Icon.gmd_stars).withIdentifier(3003).withSelectable(false)
                        ),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("친구에게 알리기").withIcon(GoogleMaterial.Icon.gmd_share).withIdentifier(40).withSelectable(false),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("받은 메세지").withIcon(GoogleMaterial.Icon.gmd_message).withIdentifier(50).withSelectable(false)
                )
                .withStickyFooter(R.layout.nav_footer)
                .withStickyFooterShadow(false)
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        LinearLayout lo_setting = (LinearLayout) drawerView.findViewById(R.id.lo_setting);
                        lo_setting.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                    }
                })
                .withOnDrawerItemClickListener(onDrawerItemClickListener)
                .build();
        // 로그아웃 버튼 체크색상표시를 위함

        mDrawerActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDrawer.setStickyFooterSelectionAtPosition(0, false);
            }
        }, 500);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected Drawer.OnDrawerItemClickListener onDrawerItemClickListener = new Drawer.OnDrawerItemClickListener() {
        @Override
        public boolean onItemClick(View view, int m, IDrawerItem iDrawerItem) {
            if (iDrawerItem == null) return false;

            final int menuNo = (int) iDrawerItem.getIdentifier();

            WebView mWebView = ((SectionB_mainFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG)).mWv;

            switch (menuNo) {
                case CodeConstant.LOGOUT:
                    DialogUtil.showLogoutDialog(MainActivity.this);
                    break;
                case 10:
                    mWebView.loadUrl(Urls.LOGIN_URL);
                    break;
                case 2001:
                    mWebView.loadUrl(Urls.SEARCH_URL);
                    break;
                case 2002:
                    mWebView.loadUrl(Urls.RECOMMEND_URL);
                    break;
                case 2003:
                    mWebView.loadUrl(Urls.SALELIST_URL);
                    break;
                case 2004:
                    mWebView.loadUrl(Urls.SALELIST_URL);
                    break;
                case 2005:
                    mWebView.loadUrl(Urls.EVENT_URL);
                    break;
                case 2006:
                    mWebView.loadUrl(Urls.PROMOTION_URL);
                    break;
                case 3001:
                    mWebView.loadUrl(Urls.NEWS_URL);
                    break;
                case 3002:
                    mWebView.loadUrl(Urls.FAQ_URL);
                    break;
                case 3003:
                    mWebView.loadUrl(Urls.CUSTOMER_URL);
                    break;
                case 40:
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_extra_subject));
                    intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_extra_text) +
                            Uri.parse(getString(R.string.share_extra_uri)));

                    Intent chooser = Intent.createChooser(intent, getString(R.string.share_extra_chooser));
                    startActivity(chooser);
                    break;
                case 50:
                    intent = new Intent(MainActivity.this, MessageListActivity.class);
                    startActivity(intent);
                    break;
                default:

                    break;
            }

            return false;
        }
    };


    public void closeDrawer() {
        if (mDrawer != null && mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
        }
    }

    @Override
    public void onBackPressed() {
        if (((SectionB_mainFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG)).mWv.getUrl().equals(Urls.REDIRECT_SERVER_URL)) {
            if (System.currentTimeMillis() > backKeyPressedTime + FINISH_INTERVAL_TIME) {
                backKeyPressedTime = System.currentTimeMillis();
                showGuide();
                return;
            }

            if (System.currentTimeMillis() <= backKeyPressedTime + FINISH_INTERVAL_TIME) {
                toast.cancel();

                Intent t = new Intent(MainActivity.this, MainActivity.class);
                MainActivity.this.startActivity(t);
                MainActivity.this.moveTaskToBack(true); // 실행중인 어플리케이션을 백그라운도로 전환
                MainActivity.this.finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
    }


    // TODO Toast -> Dialog로 변환
    public void showGuide() {
        toast = Toast.makeText(MainActivity.this, "한번 더 누르시면 종료됩니다!", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        DialogUtil.destoryDialogs();
    }

}
