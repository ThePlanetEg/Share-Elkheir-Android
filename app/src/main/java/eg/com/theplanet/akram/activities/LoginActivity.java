package eg.com.theplanet.akram.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import eg.com.theplanet.akram.R;
import eg.com.theplanet.akram.managers.APIManager;
import eg.com.theplanet.akram.managers.UsersManager;
import eg.com.theplanet.akram.models.responses.LoginResponse;
import eg.com.theplanet.akram.ui.views.CustomEditText;
import eg.com.theplanet.akram.utils.Constants;
import eg.com.theplanet.akram.utils.Utils;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by georgenaiem on 5/17/16.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private final List<String> facebookPermissions = Arrays.asList("email", "public_profile");
    private CallbackManager callbackManager;
    private View rootView;
    private TextInputLayout emailInputLayout;
    private CustomEditText emailEditText;
    private TextInputLayout passwordInputLayout;
    private CustomEditText passwordEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        rootView = findViewById(R.id.root_view);
        emailInputLayout = (TextInputLayout) findViewById(R.id.email_inputLayout);
        emailEditText = (CustomEditText) findViewById(R.id.email_editText);
        passwordInputLayout = (TextInputLayout) findViewById(R.id.password_inputLayout);
        passwordEditText = (CustomEditText) findViewById(R.id.password_editText);
        findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(v);
            }
        });

        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithEmail(v);
            }
        });

        findViewById(R.id.login_with_facebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithFacebook(v);
            }
        });
    }

    public void loginWithEmail(final View v) {

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }


        if (!validate())
            return;

        if (!Utils.isConnectingToInternet(this)) {
            showMessage(getString(R.string.internet_is_not_connected));
            return;
        }


        v.setEnabled(false);
        v.setAlpha(0.3f);
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


        Call<LoginResponse> call = apiManager
                .loginWithEmail(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();

                if (loginResponse == null || loginResponse.error != null) {
                    v.setEnabled(true);
                    v.setAlpha(1f);
                    showMessage(getString(R.string.invalid_email_or_password));
                } else {
                    String token = response.body().token;

                    if (token != null) {
                        completeLogin(response.body());
                    }
                }


            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                v.setAlpha(1f);
                v.setEnabled(true);

                showMessage(getString(R.string.something_went_wrong));
                Log.v(TAG, t.getMessage());

            }
        });
    }

    private boolean validate() {
        boolean isValid = true;

        if (!Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText().toString()).matches()) {
            emailInputLayout.setErrorEnabled(true);
            emailInputLayout.setError(getString(R.string.invalid_email));
            isValid = false;
        } else {
            emailInputLayout.setErrorEnabled(false);
        }


        if (passwordEditText.getText().toString().contentEquals("")
                || passwordEditText.getText().toString().length() < 6) {
            passwordInputLayout.setErrorEnabled(true);
            passwordInputLayout.setError(getString(R.string.invalid_password));
            isValid = false;
        } else {
            passwordInputLayout.setErrorEnabled(false);
        }


        return isValid;
    }

    public void loginWithFacebook(final View v) {
        if (!Utils.isConnectingToInternet(getBaseContext())) {
            Toast.makeText(this, R.string.internet_is_not_connected, Toast.LENGTH_LONG).show();
            return;
        }

        v.setAlpha(0.3f);


        LoginManager.getInstance().logInWithReadPermissions(this, facebookPermissions);

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        String facebookToken = loginResult.getAccessToken().getToken();
                        Log.d("Facebook Access Token", facebookToken);

                        login(facebookToken, v);
                    }

                    @Override
                    public void onCancel() {
                        Log.d("Facebook Access Token", "Canceled");
                        v.setAlpha(1f);

                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("Facebook Access Token", error.toString());
                        v.setAlpha(1f);

                    }
                });


    }

    private void login(String facebookToken, final View view) {

        if (!Utils.isConnectingToInternet(getBaseContext())) {
            showMessage(getString(R.string.internet_is_not_connected));
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


        Call<LoginResponse> call = apiManager.loginWithFacebook(facebookToken);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if (response.body() != null && response.body().token != null) {
                    completeLogin(response.body());
                    return;
                }
                view.setAlpha(1f);
                showMessage(getString(R.string.something_went_wrong));

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                view.setAlpha(1f);
                showMessage(getString(R.string.something_went_wrong));
                Log.v(TAG, t.getMessage());

            }
        });
    }

    private void completeLogin(LoginResponse loginResponse) {
        UsersManager.setDefaultUser(getApplicationContext(), loginResponse.user);
        UsersManager.setToken(getApplicationContext(), loginResponse.token);

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


    public void register(View v) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private void showMessage(String message) {
        if (rootView != null)
            Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
