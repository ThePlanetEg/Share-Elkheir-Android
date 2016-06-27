package eg.com.theplanet.akram.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import eg.com.theplanet.akram.R;
import eg.com.theplanet.akram.managers.APIManager;
import eg.com.theplanet.akram.managers.UsersManager;
import eg.com.theplanet.akram.models.User;
import eg.com.theplanet.akram.models.responses.RegisterResponse;
import eg.com.theplanet.akram.ui.views.CustomButton;
import eg.com.theplanet.akram.ui.views.CustomEditText;
import eg.com.theplanet.akram.utils.Constants;
import eg.com.theplanet.akram.utils.Utils;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by georgenaiem on 5/18/16.
 */
public class RegisterActivity extends AppCompatActivity {

    private static final int REQUEST_STORAGE_PERMISSION = 2;
    private static final String TAG = "RegisterActivity";
    private final int SELECT_FILE = 17;
    private final int REQUEST_CAMERA = 15;
    private String selectImagePath;
    private View rootView;
    private ImageButton addPhotoButton;
    private TextInputLayout nameInputLayout;
    private CustomEditText nameEditText;
    private TextInputLayout emailInputLayout;
    private CustomEditText emailEditText;
    private TextInputLayout passwordInputLayout;
    private CustomEditText passwordEditText;
    private CustomButton registerButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        rootView = findViewById(R.id.root_view);
        addPhotoButton = (ImageButton) findViewById(R.id.add_photo_button);
        nameInputLayout = (TextInputLayout) findViewById(R.id.name_inputLayout);
        nameEditText = (CustomEditText) findViewById(R.id.name_editText);
        emailInputLayout = (TextInputLayout) findViewById(R.id.email_inputLayout);
        emailEditText = (CustomEditText) findViewById(R.id.email_editText);
        passwordInputLayout = (TextInputLayout) findViewById(R.id.password_inputLayout);
        passwordEditText = (CustomEditText) findViewById(R.id.password_editText);
        registerButton = (CustomButton) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(v);
            }
        });

    }

    public void addPhoto(View v) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            addPermissions();
            return;
        }
        new AlertDialog.Builder(this)
                .setItems(new String[]{getString(R.string.capture_picture),
                                getString(R.string.choose_from_gallery)},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        uploadFromCamera();
                                        break;
                                    case 1:
                                        uploadFromGallery();
                                        break;
                                }
                            }
                        })
                .show();
    }

    private void uploadFromGallery() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, "Select File"),
                SELECT_FILE);
    }

    private void uploadFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            String selectedImageUri = MediaStore.Images.Media.insertImage
                    (getContentResolver(), thumbnail, "title", "description");
            setImageUri(Uri.parse(selectedImageUri));

        } else if (requestCode == SELECT_FILE && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            setImageUri(selectedImageUri);
        }
    }

    private void setImageUri(Uri imageUri) {
        selectImagePath = Utils.getUriPath(imageUri, getApplicationContext());


        Glide.with(this)
                .load(new File(selectImagePath))
                .bitmapTransform(new CropCircleTransformation(this))
                .into(addPhotoButton);
    }

    public void register(View v) {
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


        registerButton.setEnabled(false);
        registerButton.setAlpha(0.3f);
        User user = getUserData();

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

        Map<String, RequestBody> map = new HashMap<>();
        map.put("name", Utils.toRequestBody(user.username));
        map.put("email", Utils.toRequestBody(user.emailAddress));
        map.put("password", Utils.toRequestBody(user.password));
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*")
                , new File(selectImagePath));
        map.put("image\"; filename=\"" + selectImagePath + "\"", fileBody);


        Call<RegisterResponse> call = apiManager.register(map);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                registerButton.setEnabled(true);
                registerButton.setAlpha(1f);

                if (response.code() == 401) {
                    try {
                        String errorString = response.errorBody().string();
                        RegisterResponse registerResponse = new Gson().fromJson(errorString,RegisterResponse.class);
                        showMessage(registerResponse.error);
                    } catch (IOException e) {
                        e.printStackTrace();
                        showMessage(getString(R.string.email_exists));

                    }
                    return;
                }

                RegisterResponse registerResponse = response.body();

                if (registerResponse == null) {
                    showMessage(getString(R.string.something_went_wrong));
                    return;
                }
                String error = registerResponse.error;
                if (error != null) {
                    showMessage(error);
                } else {
                    completeRegister(response.body());
                }


            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                registerButton.setEnabled(true);
                registerButton.setAlpha(1f);
                showMessage(getString(R.string.something_went_wrong));
                Log.v(TAG, t.getMessage());

            }
        });
    }

    private boolean validate() {
        boolean isValid = true;

        if (nameEditText.getText().toString().contentEquals("")) {
            nameInputLayout.setErrorEnabled(true);
            nameInputLayout.setError(getString(R.string.invalid_name));
            isValid = false;
        } else {
            nameInputLayout.setErrorEnabled(false);
        }


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


        if (selectImagePath == null) {
            if (rootView != null) {
                Snackbar.make(rootView, "Please add image", Snackbar.LENGTH_SHORT).show();
            }
            isValid = false;

        }

        return isValid;
    }

    private User getUserData() {
        User user = new User();
        user.username = nameEditText.getText().toString();
        user.emailAddress = emailEditText.getText().toString();
        user.password = passwordEditText.getText().toString();
        return user;
    }


    private void showMessage(String message) {
        if (rootView != null)
            Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
    }

    private void addPermissions() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    addPhoto(null);

                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                    new AlertDialog.Builder(this)
                            .setMessage(getString(R.string.explain_storage_permission))
                            .setNegativeButton("NOT NOW", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("SETTINGS", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final Intent i = new Intent();
                                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    i.addCategory(Intent.CATEGORY_DEFAULT);
                                    i.setData(Uri.parse("package:" + getPackageName()));
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    startActivity(i);
                                }
                            })
                            .show();

                }
            }


        }
    }


    private void completeRegister(RegisterResponse registerResponse) {
        UsersManager.setDefaultUser(getApplicationContext(), registerResponse.user);
        UsersManager.setToken(getApplicationContext(), registerResponse.token);

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
