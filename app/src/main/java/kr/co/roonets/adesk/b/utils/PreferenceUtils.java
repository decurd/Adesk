package kr.co.roonets.adesk.b.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Shared Preference 유틸.
 *
 * @author yeonkil choi
 * @since 2017. 3. 27
 */
public class PreferenceUtils {

    /**
     * String형 저장.
     *
     * @param context
     *            the context
     * @param key
     *            the key
     * @param value
     *            the value
     */
    public static void setPreference(Context context, String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * Boolean형 저장.
     *
     * @param context
     *            the context
     * @param key
     *            the key
     * @param value
     *            the value
     */
    public static void setPreference(Context context, String key, boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * Integer형 저장.
     *
     * @param context
     *            the context
     * @param key
     *            the key
     * @param value
     *            the value
     */
    public static void setPreference(Context context, String key, int value) {
        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * Gets the preference string.
     *
     * @param context
     *            the context
     * @param key
     *            the key
     * @return the preference string
     */
    public static String getPreferenceString(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getString(key, null);
    }

    /**
     * Gets the preference boolean.
     *
     * @param context
     *            the context
     * @param key
     *            the key
     * @param defaultValue
     *            the default value
     * @return the preference boolean
     */
    public static boolean getPreferenceBoolean(Context context, String key,
            boolean defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getBoolean(key, defaultValue);
    }

    /**
     * Gets the preference int.
     *
     * @param context
     *            the context
     * @param key
     *            the key
     * @param defaultValue
     *            the default value
     * @return the preference int
     */
    public static int getPreferenceInt(Context context, String key,
            int defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        return prefs.getInt(key, defaultValue);
    }
}
