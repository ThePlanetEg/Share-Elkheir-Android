package eg.com.theplanet.akram.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import eg.com.theplanet.akram.R;
import eg.com.theplanet.akram.models.Contribution;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by georgenaiem on 5/25/16.
 */
public class Utils {

    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else {
            if (connectivityManager != null) {
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);
        paint.setAntiAlias(true);
        Canvas c = new Canvas(circleBitmap);
        c.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);

        return circleBitmap;
    }

    public static RequestBody toRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    public static String getUriPath(Uri uri, Context mContext) {

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);

        int column_index = 0;
        if (cursor != null) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        } else {
            return "";
        }

    }

    public static AlertDialog showLocationIsNotRequestedPopup(final Activity activity) {

        return new AlertDialog.Builder(activity)
                .setMessage(activity.getString(R.string.gps_network_not_enabled))
                .setPositiveButton(R.string.open_location_settings, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        activity.startActivity(myIntent);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public static String formatDateString(Context mContext, String string) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        String timeString = "";
        try {
            Date date = dateFormat.parse(string);
            Calendar time = Calendar.getInstance();
            time.setTimeInMillis(date.getTime());
            Calendar now = Calendar.getInstance();
            final String timeFormatString = "h:mm aa";
            final String dateFormatString = "dd-MM-yyyy";


            long diff = now.getTimeInMillis() - time.getTimeInMillis();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (now.get(Calendar.DATE) == time.get(Calendar.DATE) && days < 1) {
                timeString = mContext.getString(R.string.today_at) + " " + DateFormat.format(timeFormatString, time);
            } else if (days < 2) {
                timeString = mContext.getString(R.string.yesterday);
            } else if (days < 8) {
                timeString = String.format(Locale.getDefault(), "%s %s", Math.abs(now.get(Calendar.DATE) - time.get(Calendar.DATE)), mContext.getString(R.string.days_ago));
            } else {
                timeString = (String) DateFormat.format(dateFormatString, time);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timeString;
    }

    public static void setDefaultLocale(Context mContext, String localeString) {
        Locale locale = new Locale(localeString);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        mContext.getResources().updateConfiguration(config,
                mContext.getResources().getDisplayMetrics());
    }

    public static void share(Context mContext, Contribution contribution) {

        String justContributed = mContext.getString(R.string.just_contributed);
        String type;

        if (contribution.type.contentEquals(Constants.TYPE_FOOD_BOXES))
            type = mContext.getString(R.string.boxes_of_food);
        else if (contribution.type.contentEquals(Constants.TYPE_CLOTHES))
            type = mContext.getString(R.string.clothes);
        else
            type = mContext.getString(R.string._hot_meals);


        String viaAkram = mContext.getString(R.string.via_akram);
        String joinMe = mContext.getString(R.string.join_me);

        String message = justContributed + type + " " + viaAkram + ", " + joinMe + " http://theplanetlabs.com/akram_redirect/";
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);

        mContext.startActivity(Intent.createChooser(share, message));
    }

    public static String addressLineFromAddress(Address address) {
        if (address.getMaxAddressLineIndex() < 2)
            return "";

        String line = address.getAddressLine(1);

        for (int i = 2; i < address.getMaxAddressLineIndex() ; i++){
            line+= ", "+address.getAddressLine(i);
        }

        return line;
    }
}
