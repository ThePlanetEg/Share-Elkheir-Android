package eg.com.theplanet.akram.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Date;

import eg.com.theplanet.akram.models.User;
import eg.com.theplanet.akram.utils.Constants;

/**
 * Created by georgenaiem on 5/22/16.
 */
public class UsersManager {


    public static User getDefaultUser(Context mContext){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (preferences.getString(Constants.USER_ID, "").contentEquals(""))
            return null;
        User user = new User();
        user.userID = preferences.getString(Constants.USER_ID, "");
        user.facebookID = preferences.getString(Constants.FACEBOOK_ID, "");
        user.username = preferences.getString(Constants.USER_NAME, "");
        user.imageURL = preferences.getString(Constants.USER_IMAGE, "");
        user.emailAddress = preferences.getString(Constants.USER_EMAIL, "");
        return user;
    }

    public static String getToken(Context mContext){
        return PreferenceManager.getDefaultSharedPreferences(mContext).getString(Constants.ACCESS_TOKEN,"");
    }

    public static void setDefaultUser(Context mContext, User user) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        editor.putString(Constants.USER_ID, user.userID);
        editor.putString(Constants.FACEBOOK_ID, user.facebookID);
        editor.putString(Constants.USER_NAME, user.username);
        editor.putString(Constants.USER_IMAGE, user.imageURL);
        editor.putString(Constants.USER_EMAIL, user.emailAddress);

        editor.apply();
    }

    public static void setToken(Context mContext, String accessToken) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        editor.putString(Constants.ACCESS_TOKEN, accessToken);
        editor.putLong(Constants.LAST_TOKEN_DATE, new Date().getTime());
        editor.apply();
    }
}
