package kr.co.roonets.adesk.b;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import kr.co.roonets.adesk.MainActivity;
import kr.co.roonets.adesk.R;
import kr.co.roonets.adesk.b.constant.Conts;
import kr.co.roonets.adesk.b.constant.Urls;
import kr.co.roonets.adesk.b.interfaces.OnWebviewScrollChangedListener;
import kr.co.roonets.adesk.b.location.CurrentLocation;
import kr.co.roonets.adesk.b.utils.CommonUtils;
import kr.co.roonets.adesk.b.utils.NetworkUtil;
import kr.co.roonets.adesk.b.utils.SettingsUtil;
import kr.co.roonets.adesk.b.webview.MainWebView;
import kr.co.roonets.adesk.common.PermissionRequester;

public class SectionB_mainFragment extends Fragment implements OnWebviewScrollChangedListener, View.OnClickListener {

    private MainWebView webView;
    private AppCompatActivity acActivity;
    public static MainWebView mWv;
    private CurrentLocation currentLocation;
    public static SectionB_mainFragment mContext;
    private Animation slideUpAnim;
    private Animation slideDownAnim;
    private LinearLayout bottomLayout;
    private boolean isBottomVisible = true;
    private int prevOldt = 0;
    private int slop;


    private TextView homeTextView;
    private TextView searchTextView;
    private TextView eventTextView;
    private TextView reservationDetailsTextView;
    private TextView settingsTextView;

    private ImageView homeImageView;
    private ImageView searchImageView;
    private ImageView eventImageView;
    private ImageView reservationImageView;
    private ImageView settingImageView;

    private LinearLayout homeBtn;
    private LinearLayout searchBtn;
    private LinearLayout eventBtn;
    private LinearLayout reservationDetailsBtn;
    private LinearLayout settingsBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        String startUrl = Urls.HOME_URL;
        acActivity = (AppCompatActivity) getActivity();
        currentLocation = new CurrentLocation(acActivity, null);

        mContext = SectionB_mainFragment.this;

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            //mPostMode = bundle.getString(MODE, "FOLLOW");
            //title= bundle.getString(CodeConstant.TITLE, getString(R.string.connect_community));
        }

        View v = inflater.inflate(R.layout.webview_main, container, false);
        ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progress_horizontal);

        homeTextView = (TextView) v.findViewById(R.id.homeTextView);
        searchTextView = (TextView) v.findViewById(R.id.searchTextView);
        eventTextView = (TextView) v.findViewById(R.id.eventTextView);
        reservationDetailsTextView = (TextView) v.findViewById(R.id.reservationDetailsTextView);
        settingsTextView = (TextView) v.findViewById(R.id.settingsTextView);

        homeImageView = (ImageView) v.findViewById(R.id.homeImageView);
        searchImageView = (ImageView) v.findViewById(R.id.searchImageView);
        eventImageView = (ImageView) v.findViewById(R.id.eventImageView);
        reservationImageView = (ImageView) v.findViewById(R.id.reservationImageView);
        settingImageView = (ImageView) v.findViewById(R.id.settingImageView);

        homeBtn = (LinearLayout) v.findViewById(R.id.homeBtn);
        searchBtn = (LinearLayout) v.findViewById(R.id.searchBtn);
        eventBtn = (LinearLayout) v.findViewById(R.id.eventBtn);
        reservationDetailsBtn = (LinearLayout) v.findViewById(R.id.reservationDetailsBtn);
        settingsBtn = (LinearLayout) v.findViewById(R.id.settingsBtn);

        homeBtn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        eventBtn.setOnClickListener(this);
        reservationDetailsBtn.setOnClickListener(this);
        settingsBtn.setOnClickListener(this);


        bottomLayout = (LinearLayout) v.findViewById(R.id.bottomLayout);
        webView = (MainWebView) v.findViewById(R.id.webView);
        mWv = webView;
        webView.init(null, this, progressBar);


        webView.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if(event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    WebView webView = (WebView) v;

                    switch(keyCode)
                    {
                        case KeyEvent.KEYCODE_BACK:
                            ((MainActivity)getActivity()).closeDrawer();
                            // 해당 URL의 홈이 리다이렉팅 되어서 계속 이전 페이지가 있다고 나옴 해서 해당 로직일때는 이전페이지로 가는게 아니라 MainActivity.onBackPressed 로 간다
                            if (webView.getUrl().equals(Urls.REDIRECT_SERVER_URL)) {
                                return false;
                            } else {
                                if (((MainActivity)getActivity()).mDrawer.isDrawerOpen()) {
                                    return false;
                                } else {
                                    if (webView.canGoBack()) {
                                        webView.goBack();
                                        return true;
                                    }
                                }

                            }

                            break;
                    }
                }
                return false;
            }
        });

        ViewConfiguration viewConfig = ViewConfiguration.get(getActivity());
        slop = viewConfig.getScaledTouchSlop();

        slideUpAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        slideUpAnim.setAnimationListener(slideUpAnimListener);
        slideDownAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        slideDownAnim.setAnimationListener(slideDownAnimListener);

        webView.loadUrl(startUrl);

        return v;
    }


    private BroadcastReceiver changeLoadedUrlReciever = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent == null) return;
            String action = intent.getAction();

            if(action.equals(Conts.ACTION_LOADED_URL_CHANGED)){
                if(webView != null){
                    String url = webView.getUrl();
                    if(url.startsWith(Urls.HOME_URL) || url.startsWith(Urls.SEARCH_URL) ||
                            url.startsWith(Urls.EVENT_URL) || url.startsWith(Urls.RESERVATION_DETAILS_URL) || url.startsWith(Urls.SETTINGS_URL)){
                        homeImageView.setSelected(false);
                        searchImageView.setSelected(false);
                        eventImageView.setSelected(false);
                        reservationImageView.setSelected(false);
                        settingImageView.setSelected(false);

                        if(url.startsWith(Urls.HOME_URL)){
                            homeImageView.setSelected(true);
                        }else if(url.startsWith(Urls.SEARCH_URL)){
                            searchImageView.setSelected(true);
                        }else if(url.startsWith(Urls.EVENT_URL)){
                            eventImageView.setSelected(true);
                        }else if(url.startsWith(Urls.RESERVATION_DETAILS_URL)){
                            reservationImageView.setSelected(true);
                        }else if(url.startsWith(Urls.SETTINGS_URL)){
                            settingImageView.setSelected(true);
                        }
                    }
                }
            }
        }
    };

    @Override
    public void onWebviewScrollChanged(int l, int t, int oldl, int oldt) {
        if(Math.abs(t - prevOldt) < slop){
            return;
        }
        if(isBottomVisible && t - prevOldt > 0){
            //메뉴 사라짐
            isBottomVisible = false;
            if(bottomLayout != null) bottomLayout.startAnimation(slideDownAnim);
        }else if(!isBottomVisible && t - prevOldt < 0){
            //메뉴 나타남
            isBottomVisible = true;
            if(bottomLayout != null) bottomLayout.startAnimation(slideUpAnim);
        }
        prevOldt = oldt;
    }

    private AnimationListener slideUpAnimListener = new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
            // TODO Auto-generated method stub
            if(bottomLayout != null) bottomLayout.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            // TODO Auto-generated method stub

        }
    };

    private AnimationListener slideDownAnimListener = new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            // TODO Auto-generated method stub
            if(bottomLayout != null) bottomLayout.setVisibility(View.GONE);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(acActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {   // 사용권한이 없을경우
            PermissionRequester.Builder requester = new PermissionRequester.Builder(acActivity);
            requester.create().request(Manifest.permission.ACCESS_COARSE_LOCATION, 10000, new PermissionRequester.OnClickDenyButtonListener() {
                @Override
                public void onClick(Activity activity) {
                    Toast.makeText(activity, "권한을 얻지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            currentLocation.startLocationUpdate();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        currentLocation.stopLocationUpdate();
    }


    /**
     * Show network dialog.
     *
     * @param url the url
     */
    public void showNetworkDialog(final String url) {
        AlertDialog alertDialog = CommonUtils.createYesNoDialog(getActivity(), null,
                getString(R.string.alert_not_connect_internet),
                getString(R.string.retry), getString(R.string.exit_app),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                webView.loadUrl(url);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                //finish();
                                break;
                            default:
                                break;
                        }
                    }
                });

        alertDialog.show();
    }

    /**
     * Show3g lte connect dialog.
     *
     * @param url the url
     */
    public void show3gLteConnectDialog(final String url) {
        AlertDialog alertDialog = CommonUtils.createYesNoDialog(getActivity(),
                null,
                getString(R.string.use_3g_lte_message), getString(R.string.use_3g_lte),
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                SettingsUtil.setUseDataNetwork(getActivity(), true);
                                webView.loadUrl(url);
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                //finish();
                                break;
                            default:
                                break;
                        }
                    }
                });

        alertDialog.show();
    }

    /**
     * Show3g lte setting dialog.
     */
    public void show3gLteSettingDialog() {
        String yesMessage;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            yesMessage = getString(R.string.network_setting);
        } else {
            yesMessage = getString(R.string.use_3g_lte);
        }
        AlertDialog alertDialog = CommonUtils.createYesNoDialog(getActivity(),
                null,
                getString(R.string.network_warning_message), yesMessage,
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                NetworkUtil.useMobileNetwork(
                                        getActivity(), true);

                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                            default:
                                break;
                        }
                    }
                });

        alertDialog.show();
    }

    /**
     * Show quit dialog.
     */
    private void showQuitDialog() {
        AlertDialog alertDialog = CommonUtils.createYesNoDialog(getActivity(), null,
                getString(R.string.quit), getString(R.string.message_yes),
                getString(R.string.message_no),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //finish();
                                break;
                            default:
                                break;
                        }
                    }
                });

        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.homeBtn:
                webView.loadUrl(Urls.HOME_URL);
                break;
            case R.id.searchBtn:
                webView.loadUrl(Urls.SEARCH_URL);
                break;
            case R.id.eventBtn:
                webView.loadUrl(Urls.EVENT_URL);
                break;
            case R.id.reservationDetailsBtn:
                webView.loadUrl(Urls.RESERVATION_DETAILS_URL);
                break;
            case R.id.settingsBtn:
                webView.loadUrl(Urls.SETTINGS_URL);
                break;
            default:
                break;
        }
    }

}


