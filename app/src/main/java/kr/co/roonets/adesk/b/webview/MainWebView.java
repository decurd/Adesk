package kr.co.roonets.adesk.b.webview;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import java.util.HashMap;
import java.util.Map;

import kr.co.roonets.adesk.b.client.RoonetsWebChromeClient;
import kr.co.roonets.adesk.b.client.RoonetsWebViewClient;
import kr.co.roonets.adesk.b.dialog.WebViewDialog;
import kr.co.roonets.adesk.b.interfaces.OnWebviewScrollChangedListener;
import kr.co.roonets.adesk.b.interfaces.RoonetsInterface;
import kr.co.roonets.adesk.b.utils.NetworkUtil;
import kr.co.roonets.adesk.b.utils.SettingsUtil;


public class MainWebView extends WebView {

    
    /** 다국어 지원을 위한 http 헤더 키값. */
    private final String HEADER_LANG = "Accept-Language";
    
    private OnWebviewScrollChangedListener onWebviewScrollChangedListener;
    
    private RoonetsWebChromeClient roonetsWebChromeClient;
    
    /** 헤더 맵. */
    private Map<String, String> additionalHttpHeaders;
    
    public MainWebView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    
    public MainWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * 웹뷰 설정 및 진행바 설정.
     * 진행바 사용하지 않는 경우 null
     *
     * @param webViewDialog the web view dialog
     * @param progressBar the progress bar
     */
    public void init(WebViewDialog webViewDialog, OnWebviewScrollChangedListener webviewScrollChangedListener, ProgressBar progressBar){
        this.onWebviewScrollChangedListener = webviewScrollChangedListener;


        
        additionalHttpHeaders = new HashMap<String, String>();
        
        WebSettings webSetting = getSettings();
        //캐시 사용 안함.
        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSetting.setDomStorageEnabled(true);
        String appCachePath = getContext().getCacheDir().getAbsolutePath();
        webSetting.setAppCachePath(appCachePath);
        webSetting.setAllowFileAccess(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setSupportMultipleWindows(true);
        webSetting.setSupportZoom(false);
        webSetting.setBuiltInZoomControls(false);
        webSetting.setLoadsImagesAutomatically(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager cookieManager = CookieManager.getInstance(); 
            cookieManager.setAcceptCookie(true); 
            cookieManager.setAcceptThirdPartyCookies(this, true);
        }

        // @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webSetting.setDisplayZoomControls(false);
        }

        setScrollbarFadingEnabled(true);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        setNetworkAvailable(true);
        setWebViewClient(new RoonetsWebViewClient(getContext(), progressBar));
        roonetsWebChromeClient = new RoonetsWebChromeClient(getContext(), webViewDialog, progressBar);
        setWebChromeClient(roonetsWebChromeClient);
        
        // 웹페이지에서 Javascript로 Java측 클래스의 함수를 호출하기 위한 Interface 추가
        RoonetsInterface roonetsInterface = new RoonetsInterface(getContext());
        // 호출예) RoonetsInterface.APP_NAME-> window.HOTELCAFE_APP.@JavascriptInterface->함수명
        addJavascriptInterface(roonetsInterface, RoonetsInterface.APP_NAME);
    }
    
    public RoonetsWebChromeClient getRoonetsWebChromeClient() {
        return roonetsWebChromeClient;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.webkit.WebView#loadUrl(java.lang.String)
     */
    @Override
    public void loadUrl(String url) {

        // TODO Auto-generated method stub
        if (URLUtil.isJavaScriptUrl(url)) {
            loadUrl(url, additionalHttpHeaders);
        } else {

            if ((!NetworkUtil.isWifiNetworkConnect(getContext()) && !NetworkUtil
                    .isMobileNetworkConnect(getContext()))
                    || NetworkUtil.isAirplaneMode(getContext())) {
                //SectionB_mainFragment.mContext.showNetworkDialog(url);
            } else if (!NetworkUtil.isWifiNetworkConnect(getContext())
                    && NetworkUtil.isMobileNetworkConnect(getContext())
                    && !SettingsUtil.isUseDataNetwork(getContext())) {
                //SectionB_mainFragment.mContext.show3gLteConnectDialog(url);
            } else {
                Uri builtUri = Uri.parse(url);

                String buildUrl = builtUri.toString();

                loadUrl(buildUrl, additionalHttpHeaders);
            }

        }
    }



    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        // TODO Auto-generated method stub
        super.onScrollChanged(l, t, oldl, oldt);
        
        if(onWebviewScrollChangedListener != null){
            onWebviewScrollChangedListener.onWebviewScrollChanged(l, t, oldl, oldt);
        }
    }
}
