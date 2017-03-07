package mshttp.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * Created by mvalencia on 3/6/17.
 */

public class PreferenceData
{
    static final String PREF_LOGGEDIN_USER = "logged_in_email";
    static final String PREF_USER_LOGGEDIN_STATUS = "logged_in_status";
    static final String PREF_USER_JWT = "jwt";

    public static SharedPreferences getSharedPreferences(Context ctx)
    {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setLoggedInUser(Context ctx, String email)
    {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_LOGGEDIN_USER, email);
        editor.commit();
    }

    public static String getLoggedInUser(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_LOGGEDIN_USER, "");
    }

    public static void setUserLoggedInStatus(Context ctx, boolean status)
    {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putBoolean(PREF_USER_LOGGEDIN_STATUS, status);
        editor.commit();
    }

    public static boolean getUserLoggedInStatus(Context ctx)
    {
        return getSharedPreferences(ctx).getBoolean(PREF_USER_LOGGEDIN_STATUS, false);
    }

    public static void setJwt(Context ctx, String jwt)
    {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_JWT, jwt);
        editor.commit();
    }

    public static String getJwt(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_JWT, "");
    }

    public static void clearLoggedInUser(Context ctx)
    {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(PREF_LOGGEDIN_USER);
        editor.remove(PREF_USER_LOGGEDIN_STATUS);
        editor.commit();
    }

    public static void clearJwt(Context ctx)
    {
        Editor editor = getSharedPreferences(ctx).edit();
        editor.remove(PREF_USER_JWT);
        editor.commit();
    }
}