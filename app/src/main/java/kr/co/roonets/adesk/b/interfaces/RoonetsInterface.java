package kr.co.roonets.adesk.b.interfaces;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import kr.co.roonets.adesk.MainActivity;
import kr.co.roonets.adesk.b.SectionB_mainFragment;
import kr.co.roonets.adesk.b.location.CurrentLocation;
import kr.co.roonets.adesk.b.utils.NetworkUtil;
import kr.co.roonets.adesk.b.utils.SettingsUtil;

/**
 * 웹페이지에서 자바스크트로 자바측 함수를 호출할 클래스 정의.
 *
 * @author yeonkil choi
 * @since 2017. 3. 27
 */
public class RoonetsInterface {

    /** The context. */
    private Context context;

    
    /** Javascript interface name. */
    //public static final String APP_NAME = "BENIKEA_APP";
    public static final String APP_NAME = "HOTELCAFE_APP";

    /** The Constant SVR_NAME. */
    //public static final String SVR_NAME = "BENIKEA_SVR";
    public static final String SVR_NAME = "HOTELCAFE_SVR";

    /** The Constant JAVASCRIPT_PREFIX. */
    public static final String JAVASCRIPT_PREFIX = "javascript:" + SVR_NAME
            + ".";

    /**
     * Instantiates a new smart tour interface.
     *
     * @param context
     */
    public RoonetsInterface(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    /**
     * Checks if is wifi enabled.
     *
     * @return true, if is wifi enabled
     */
    @JavascriptInterface
    public boolean isWifiEnabled() {
        return NetworkUtil.getConnectivityStatus(context) == NetworkUtil.TYPE_WIFI;
    }

    @JavascriptInterface
    public void openMainDrawer() {                      // 웹뷰에서 MainActivity의 좌측메뉴 오픈 (javascript:window.HOTELCAFE_APP.openMainDrawer())
        ((MainActivity)MainActivity.mContext).mDrawerActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((MainActivity)MainActivity.mContext).mDrawer.openDrawer();
            }
        }, 0);
    }
    
    @JavascriptInterface
    public String getLocaleLanguage(){
        String localeLanguage = Locale.KOREAN.toString();
        
        String language = SettingsUtil.getLocale(context);
        String country = SettingsUtil.getCountry(context);
        
        if(!language.equalsIgnoreCase("zh")){
            localeLanguage = language;
        }else{
            if(country.equalsIgnoreCase("CN")){
                localeLanguage = "zh-Hans";
            }else if(country.equalsIgnoreCase("TW")){
                localeLanguage = "zh-Hant";
            }
        }
        
        return localeLanguage;
    }

    /**
     * Gets the platform.
     *
     * @return the platform
     */
    @JavascriptInterface
    public String getPlatform() {
        return "ANDROID";
    }

    /**
     * Exit.
     */
    @JavascriptInterface
    public void exit() {
        System.exit(0);
    }

    /**
     * 단말에 3G / LTE 사용 유무.
     *
     * @return true, if is default network use
     */
    @JavascriptInterface
    public boolean isDefaultNetworkUse() {
        return NetworkUtil.isMobileNetworkAvailableWithSettings(context);
    }

    /**
     * Sets the default network use.
     *
     * @param enabled the new default network use
     */
    @JavascriptInterface
    public void setDefaultNetworkUse(boolean enabled) {
        SectionB_mainFragment mainFragment = new SectionB_mainFragment();
        if (enabled) {
            if (!NetworkUtil.isMobileDataEnabled(context)) {
                if (context instanceof MainActivity) {
                    mainFragment.show3gLteSettingDialog();
                    // ((MainActivity) context).show3gLteSettingDialog();
                }
            } else {
                SettingsUtil.setUseDataNetwork(context, enabled);
            }
        } else {
            SettingsUtil.setUseDataNetwork(context, false);
        }
    }

    /**
     * Gets the gps.
     *
     * @return the gps
     */
    @JavascriptInterface
    public String getGPS() {
        CurrentLocation currentLocation = new CurrentLocation(context, null);
        Location location = currentLocation.getLastKnownLocation();
        JSONObject jsonObject = new JSONObject();
        
        if (location != null) {
            try {
                jsonObject.put("latitude", location.getLatitude());
                jsonObject.put("longitude", location.getLongitude());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            try {
                jsonObject.put("latitude", "");
                jsonObject.put("longitude", "");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return jsonObject.toString();
    }

    /**
     * Gets the app version.
     *
     * @return the app version
     */
    @JavascriptInterface
    public String getAppVersion() {
        String version = null;
        try {
           PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
           version = pi.versionName;
        } catch(NameNotFoundException e) {
            e.printStackTrace();
        }
        
        return version;
    }
    
    @JavascriptInterface
    public boolean isAutoLoginUse() {
        return SettingsUtil.isAutoLoginUse(context);
    }
    
    @JavascriptInterface
    public void setAutoLoginUse(boolean useAutoLogin) {
        SettingsUtil.setAutoLoginUse(context, useAutoLogin);
    }

}
