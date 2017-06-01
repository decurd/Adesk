package kr.co.roonets.adesk.b.utils;

import android.content.Context;


/**
 * 환경 설정 관련 유틸.
 *
 * @author yeonkil choi
 * @since 2017. 3. 27
 */
public class SettingsUtil {
    
    /** The Constant PREF_DATA_NETWORK. */
    private final static String PREF_DATA_NETWORK = "dataNetwork";
    
    /** The Constant PREF_LOCALE. */
    private final static String PREF_LOCALE = "locale";
    
    /** The Constant PREF_LOCALE_COUNTRY. */
    private final static String PREF_COUNTRY = "country";
    
    private final static String PREF_AUTO_LOGIN_USE = "autoLoginUse";
    
    private final static String PREF_PUSH_USE = "pushUse";
    
    private final static String PREF_PUSH_REG_ID = "pushRegId";
    
    private final static String PREF_VIBRATION_USE = "vibration";
    
    private final static String PREF_SOUND_USE = "sound";
    
    /**
     * Checks if is use data network.
     *
     * @param context the context
     * @return true, if is use data network
     */
    public static boolean isUseDataNetwork(Context context){
        return PreferenceUtils.getPreferenceBoolean(context, PREF_DATA_NETWORK, true);
    }
    
    /**
     * Sets the use data network.
     *
     * @param context the context
     * @param enabled the enabled
     */
    public static void setUseDataNetwork(Context context, boolean enabled){
        PreferenceUtils.setPreference(context, PREF_DATA_NETWORK, enabled);
    }
    
    /**
     * Gets the locale.
     *
     * @param context the context
     * @return the locale
     */
    public static String getLocale(Context context){
        return PreferenceUtils.getPreferenceString(context, PREF_LOCALE);
    }

    
    /**
     * Sets the locale.
     *
     * @param context the context
     * @param locale the locale
     */
    public static void setLocale(Context context, String locale){
        PreferenceUtils.setPreference(context, PREF_LOCALE, locale);
    }
    
    /**
     * Gets the country.
     *
     * @param context the context
     * @return the locale
     */
    public static String getCountry(Context context){
        return PreferenceUtils.getPreferenceString(context, PREF_COUNTRY);
    }
    
    public static void setCountry(Context context, String country){
        PreferenceUtils.setPreference(context, PREF_COUNTRY, country);
    }
    
    public static boolean isAutoLoginUse(Context context){
        return PreferenceUtils.getPreferenceBoolean(context, PREF_AUTO_LOGIN_USE, false);
    }
    
    public static void setAutoLoginUse(Context context, boolean useAutoLogin){
        PreferenceUtils.setPreference(context, PREF_AUTO_LOGIN_USE, useAutoLogin);
    }
    
    public static boolean isPushUse(Context context){
        return PreferenceUtils.getPreferenceBoolean(context, PREF_PUSH_USE, false);
    }
    
    public static void setPushUse(Context context, boolean usePush){
        PreferenceUtils.setPreference(context, PREF_PUSH_USE, usePush);
    }
    
    public static String getPushRegistrationId(Context context){
        return PreferenceUtils.getPreferenceString(context, PREF_PUSH_REG_ID);
    }
    
    public static void setPushRegistrationId(Context context, String pushRegId){
        PreferenceUtils.setPreference(context, PREF_PUSH_REG_ID, pushRegId);
    }
    
    public static boolean isVibrationNotification(Context context){
        return PreferenceUtils.getPreferenceBoolean(context, PREF_VIBRATION_USE, true);
    }
    
    public static void setVibrationNotification(Context context, boolean vibrationUse){
        PreferenceUtils.setPreference(context, PREF_VIBRATION_USE, vibrationUse);
    }
    
    public static boolean isSoundNotification(Context context){
        return PreferenceUtils.getPreferenceBoolean(context, PREF_SOUND_USE, true);
    }
    
    public static void setSountNotification(Context context, boolean soundUse){
        PreferenceUtils.setPreference(context, PREF_SOUND_USE, soundUse);
    }
}
