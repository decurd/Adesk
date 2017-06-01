package kr.co.roonets.adesk.b.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 네트워크 상태 체크.
 *
 * @author yeonkil choi
 * @since 2017. 3. 27
 */
public class NetworkUtil {

    /** The type wifi. */
    public static int TYPE_WIFI = 1;

    /** The type mobile. */
    public static int TYPE_MOBILE = 2;

    /** The type not connected. */
    public static int TYPE_NOT_CONNECTED = 0;

    /**
     * Gets the connectivity status.
     *
     * @param context
     *            the context
     * @return the connectivity status
     */
    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    /**
     * Checks if is wifi network connect.
     *
     * @param context the context
     * @return true, if is wifi network connect
     */
    public static boolean isWifiNetworkConnect(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null) {
            return wifiNetwork.isConnected();
        }
        return false;
    }

    /**
     * Checks if is mobile network available with settings.
     *
     * @param context the context
     * @return true, if is mobile network available with settings
     */
    public static boolean isMobileNetworkAvailableWithSettings(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null) {
            return mobileNetwork.isAvailable()
                    && SettingsUtil.isUseDataNetwork(context);
        }
        return false;
    }
    
    /**
     * Checks if is mobile network available.
     *
     * @param context the context
     * @return true, if is mobile network available
     */
    public static boolean isMobileNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        
        if (mobileNetwork != null) {
            return mobileNetwork.isAvailable();
        }
        return false;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static boolean isMobileDataEnabled(Context context){
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean)method.invoke(cm);
        } catch (Exception e) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
        }
        return mobileDataEnabled;
    }

    /**
     * Checks if is mobile network connect with settings.
     *
     * @param context the context
     * @return true, if is mobile network connect with settings
     */
    public static boolean isMobileNetworkConnectWithSettings(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null) {
            return mobileNetwork.isConnected()
                    && SettingsUtil.isUseDataNetwork(context);
        }
        return false;
    }
    
    /**
     * Checks if is mobile network connect.
     *
     * @param context the context
     * @return true, if is mobile network connect
     */
    public static boolean isMobileNetworkConnect(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null) {
            return mobileNetwork.isConnected();
        }
        return false;
    }
    
    /**
     * Checks if is mobile network connected or connecting.
     *
     * @param context the context
     * @return true, if is mobile network connected or connecting
     */
    public static boolean isMobileNetworkConnectedOrConnecting(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null) {
            return mobileNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    /**
     * Use mobile network.
     *
     * @param context the context
     * @param enabled the enabled
     * @return true, if successful
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static boolean useMobileNetwork(Context context, boolean enabled) {
        boolean result = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (enabled && !NetworkUtil.isMobileNetworkConnectWithSettings(context)) {
                try {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setClassName("com.android.settings",
                            "com.android.settings.Settings$DataUsageSummaryActivity");
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    result = false;
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            try {
                ConnectivityManager cm = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                Class conmanClass = Class.forName(cm.getClass().getName());

                Field iConnectivityManagerField = conmanClass
                        .getDeclaredField("mService");
                iConnectivityManagerField.setAccessible(true);
                Object iConnectivityManager = iConnectivityManagerField.get(cm);
                Class iConnectivityManagerClass = Class
                        .forName(iConnectivityManager.getClass().getName());
                Method setMobileDataEnabledMethod = iConnectivityManagerClass
                        .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
                setMobileDataEnabledMethod.setAccessible(true);

                setMobileDataEnabledMethod
                        .invoke(iConnectivityManager, enabled);
            } catch (ClassNotFoundException | NoSuchFieldException
                    | IllegalAccessException | IllegalArgumentException
                    | NoSuchMethodException | InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                result = false;
            }
        }
        
        if(result){
            SettingsUtil.setUseDataNetwork(context, enabled);
        }

        return result;
    }
    
    /**
     * Checks if is airplane mode.
     *
     * @param context the context
     * @return true, if is airplane mode
     */
    public static boolean isAirplaneMode(Context context){
    	boolean isEnabled = Settings.System.getInt(

    		      context.getContentResolver(), 

    		      Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
    	
    	return isEnabled;
    }
}
