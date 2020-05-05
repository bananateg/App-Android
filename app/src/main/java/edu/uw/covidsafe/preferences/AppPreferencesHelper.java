package edu.uw.covidsafe.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferencesHelper {

    public static final String ONBOARDING_PAGER_SHOWN = "onboardingshownalready";
    public static final String BLUETOOTH_ENABLED = "bleEnabled";
    public static final String GPS_ENABLED = "gpsEnabled";
    public static final String NOTIFICATION_ENABLED = "prefs_notifs";
    public static String SHARED_PREFENCE_NAME = "preferences";


    public static final SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
    }

    public static void setOnboardingShownToUser(Context context) {
        getSharedPreferences(context).edit().putBoolean(ONBOARDING_PAGER_SHOWN, true).apply();
    }

    public static boolean isOnboardingShownToUser(Context context) {
        return getSharedPreferences(context).getBoolean(ONBOARDING_PAGER_SHOWN, false);
    }

    public static void setBluetoothEnabled(Context context) {
        setBluetoothEnabled(context, true);
    }

    public static boolean isBluetoothEnabled(Context context) {
        return getSharedPreferences(context).getBoolean(BLUETOOTH_ENABLED, false);
    }

    public static boolean isBluetoothEnabled(Context context, boolean defaultFlag) {
        return getSharedPreferences(context).getBoolean(BLUETOOTH_ENABLED, defaultFlag);
    }

    public static void setBluetoothEnabled(Context context, boolean bleFlagVal) {
        getSharedPreferences(context).edit().putBoolean(BLUETOOTH_ENABLED, bleFlagVal).apply();
    }

    public static void setGPSEnabled(Context context) {
        setGPSEnabled(context, true);
    }

    public static boolean isGPSEnabled(Context context) {
        return getSharedPreferences(context).getBoolean(GPS_ENABLED, false);
    }

    public static boolean isGPSEnabled(Context context, boolean defaultValue) {
        return getSharedPreferences(context).getBoolean(GPS_ENABLED, defaultValue);
    }

    public static void setGPSEnabled(Context context, boolean gpsFlagVal) {
        getSharedPreferences(context).edit().putBoolean(GPS_ENABLED, gpsFlagVal).apply();
    }

    public static void setNotificationEnabled(Context context) {
        setNotificationEnabled(context, true);
    }

    public static boolean areNotificationsEnabled(Context context) {
        return getSharedPreferences(context).getBoolean(NOTIFICATION_ENABLED, false);
    }

    public static boolean areNotificationsEnabled(Context context, boolean defaultValue) {
        return getSharedPreferences(context).getBoolean(NOTIFICATION_ENABLED, defaultValue);
    }

    public static void setNotificationEnabled(Context context, boolean notificationFlagVal) {
        getSharedPreferences(context).edit().putBoolean(NOTIFICATION_ENABLED, notificationFlagVal).apply();
    }
}
