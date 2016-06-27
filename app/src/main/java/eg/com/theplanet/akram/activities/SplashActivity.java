package eg.com.theplanet.akram.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import eg.com.theplanet.akram.R;
import eg.com.theplanet.akram.managers.APIManager;
import eg.com.theplanet.akram.managers.UsersManager;
import eg.com.theplanet.akram.models.User;
import eg.com.theplanet.akram.models.responses.LoginResponse;
import eg.com.theplanet.akram.utils.Constants;
import eg.com.theplanet.akram.utils.Utils;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLanguage();
        setContentView(R.layout.activity_splash);

        String userID = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString(Constants.USER_ID, "");

        final boolean showTutorial = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getBoolean(Constants.SHOW_TUTORIAL, true);

        Handler handler = new Handler();
        if (userID.contentEquals("")) {
            Runnable runnable = new Runnable() {

                @Override
                public void run() {

                    if (showTutorial) {
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                .edit().putBoolean(Constants.SHOW_TUTORIAL, false).apply();
                        openTutorialActivity();
                    } else
                        openLoginActivity();
                }
            };
            handler.postDelayed(runnable, 2000);
        } else {
            long date = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .getLong(Constants.LAST_TOKEN_DATE, 0);

            if (expired(date))
                renewToken();
            else {
                Runnable runnable = new Runnable() {

                    @Override
                    public void run() {

                        openMainActivity();
                    }
                };
                handler.postDelayed(runnable, 2000);
            }
        }

    }

    private void setLanguage() {
        String language =
                PreferenceManager.getDefaultSharedPreferences(
                        getApplicationContext()).getString(Constants.LANGUAGE, "");
        if (language.contentEquals(""))
            language = "ar";


        Utils.setDefaultLocale(this, language);

    }


    private void openLoginActivity() {
        Intent intent = new Intent();
        intent.setClass(SplashActivity.this, LoginActivity.class);

        startActivity(intent);
        finish();
    }

    private void openMainActivity() {
        Intent intent = new Intent();
        intent.setClass(SplashActivity.this, MainActivity.class);

        startActivity(intent);
        finish();
    }

    private void openTutorialActivity() {
        Intent intent = new Intent();
        intent.setClass(SplashActivity.this, TutorialActivity.class);

        startActivity(intent);
        finish();
    }


    private boolean expired(long date) {

        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(date);
        Calendar today = Calendar.getInstance();
        long diff = today.getTimeInMillis() - time.getTimeInMillis();

        long days = diff / (24 * 60 * 60 * 1000);

        return Math.abs(days) > 5;
    }

    private void renewToken() {
        User user = UsersManager.getDefaultUser(getApplicationContext());
        if (user == null) {
            openLoginActivity();
            return;
        }

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(2, TimeUnit.MINUTES)
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        APIManager apiManager = retrofit.create(APIManager.class);


        Call<LoginResponse> call = apiManager.refreshToken(UsersManager.getToken(getBaseContext()));
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {


                if (response.body() != null && response.body().token != null) {
                    UsersManager.setToken(getApplicationContext(), response.body().token);
                    openMainActivity();
                } else {
                    openLoginActivity();

                }


            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                openLoginActivity();

            }
        });
    }

}
