package eg.com.theplanet.akram.ui.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import eg.com.theplanet.akram.R;
import eg.com.theplanet.akram.managers.APIManager;
import eg.com.theplanet.akram.managers.UsersManager;
import eg.com.theplanet.akram.models.User;
import eg.com.theplanet.akram.models.requests.AddContributionRequest;
import eg.com.theplanet.akram.models.responses.AddContributionResponse;
import eg.com.theplanet.akram.ui.views.CustomEditText;
import eg.com.theplanet.akram.utils.App;
import eg.com.theplanet.akram.utils.Constants;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by georgenaiem on 5/18/16.
 */
public class FormFragment extends Fragment {

    private static final int REQUEST_LOCATION_PERMISSION = 8;
    private View headerView;
    private TextView headerTextView;
    private CustomEditText quantityEditText;
    private SeekBar accessSeekBar;
    private SwitchCompat areaConditionSwitch;
    private TextView areaConditionTextView;
    private View submitView;
    private View rootView;
    private CustomEditText feedbackEditText;


    private String formType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_form, container, false);
        headerView = rootView.findViewById(R.id.header_layout);
        headerTextView = (TextView) rootView.findViewById(R.id.header_textView);
        quantityEditText = (CustomEditText) rootView.findViewById(R.id.quantity_editText);
        accessSeekBar = (SeekBar) rootView.findViewById(R.id.safety_seekBar);
        areaConditionSwitch = (SwitchCompat) rootView.findViewById(R.id.area_condition_switch);
        areaConditionTextView = (TextView) rootView.findViewById(R.id.area_condition_textView);
        submitView = rootView.findViewById(R.id.submit_layout);
        feedbackEditText = (CustomEditText) rootView.findViewById(R.id.feedback_editText);
        initialize();

        return rootView;
    }

    private void initialize() {
        formType = getArguments().getString(Constants.FORM_TYPE, "");
        if (formType.contentEquals(Constants.TYPE_FOOD_BOXES)) {
            headerView.setBackgroundResource(R.color.brown);
            submitView.setBackgroundResource(R.color.brown);
            headerTextView.setText(R.string.food_boxes);


        } else if (formType.contentEquals(Constants.TYPE_CLOTHES)) {
            headerView.setBackgroundResource(R.color.purple);
            submitView.setBackgroundResource(R.color.purple);
            headerTextView.setText(R.string.clothes);


        }else {
            headerView.setBackgroundResource(R.color.blue);
            submitView.setBackgroundResource(R.color.blue);
            headerTextView.setText(R.string.hot_meals);
        }

        accessSeekBar.setProgress(0);
        accessSeekBar.setMax(2);

        areaConditionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    areaConditionTextView.setText(R.string.area_needs_help);
                    areaConditionTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                } else {
                    areaConditionTextView.setText(R.string.area_is_covered);
                    areaConditionTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                }
            }
        });
        submitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        rootView.findViewById(R.id.back_imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    private void submit() {

        User user = UsersManager.getDefaultUser(getContext());

        String token = UsersManager.getToken(getContext());

        String quantity = quantityEditText.getText().toString();


        String access = accessSeekBar.getProgress() == 0 ?
                "easy" : accessSeekBar.getProgress() == 1 ? "normal" : "difficult";

        String isCovered = areaConditionSwitch.isChecked() ? "0" : "1";

        AddContributionRequest request = new AddContributionRequest();

        request.userID = user.userID;
        request.token = token;
        request.quantity = quantity;
        request.access = access;
        request.isCovered = isCovered;
        request.type = formType;
        request.latitude = getArguments().getDouble(Constants.LATITUDE);
        request.longitude = getArguments().getDouble(Constants.LONGITUDE);
        request.area = getArea(new LatLng(request.latitude, request.longitude));
        request.feedback = feedbackEditText.getText().toString();

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


        Call<AddContributionResponse> call = apiManager
                .addContribution(request.token, request.userID, request.type, request.quantity,
                        request.access, request.isCovered, request.latitude, request.longitude,
                        request.area,request.feedback);

        call.enqueue(new Callback<AddContributionResponse>() {
            @Override
            public void onResponse(Call<AddContributionResponse> call, Response<AddContributionResponse> response) {

            }

            @Override
            public void onFailure(Call<AddContributionResponse> call, Throwable t) {

            }
        });
        Fragment fragment = new CompleteFormFragment();
        fragment.setArguments(getArguments());
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_view, fragment)
                .commit();

    }

    private String getArea(LatLng location) {
        String area = "";
        Geocoder gcd = new Geocoder(App.getInstance()
                , Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(location.latitude, location.longitude, 1);
            if (addresses.size() > 0)
                area = addresses.get(0).getLocality();
            if (area == null)
                area = addresses.get(0).getAdminArea();
            if (area == null)
                area = addresses.get(0).getSubAdminArea();
            if (area == null)
                area = addresses.get(0).getThoroughfare();
            if (area == null)
                area = "unknown";
        } catch (IOException e) {
            e.printStackTrace();
            area = "unknown";
        }

        return area;

    }


}
