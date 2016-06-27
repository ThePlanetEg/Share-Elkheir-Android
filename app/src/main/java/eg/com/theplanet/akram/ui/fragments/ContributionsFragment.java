package eg.com.theplanet.akram.ui.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import eg.com.theplanet.akram.R;
import eg.com.theplanet.akram.activities.MainActivity;
import eg.com.theplanet.akram.managers.APIManager;
import eg.com.theplanet.akram.managers.LocationManager;
import eg.com.theplanet.akram.managers.UsersManager;
import eg.com.theplanet.akram.models.Contribution;
import eg.com.theplanet.akram.models.responses.ContributionsResponse;
import eg.com.theplanet.akram.models.responses.LatestContributionResponse;
import eg.com.theplanet.akram.ui.dialogs.ContributionDialog;
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
 * Created by georgenaiem on 5/18/16.
 */
public class ContributionsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener, LocationManager.OnGetLocationListener, GoogleMap.OnMarkerClickListener {

    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private GoogleMap mMap;
    private Location userLocation;
    private LocationManager locationManager;
    private List<Contribution> contributions;
    private TextView latestContributionTextView;
    private Handler latestContributionHandler;
    private Runnable latestContributionRunnable;
    private LatLngBounds bounds;
    private FloatingActionButton myLocationFab;
    private List<Marker> markers;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contributions, container, false);
        latestContributionTextView = (TextView) rootView.findViewById(R.id.last_contribution_textView);
        myLocationFab = (FloatingActionButton) rootView.findViewById(R.id.my_location_fab);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getLocation();
        contributions = new ArrayList<>();
        myLocationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });
        return rootView;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnCameraChangeListener(this);
        if (userLocation == null) {
            LatLng cairo = new LatLng(30.044211, 31.215660);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cairo, 15.0f));
        } else {
            LatLng currentLocation = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(currentLocation));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15.0f));
        }

        mMap.setOnMarkerClickListener(this);

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        getContributions();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int index = markers.indexOf(marker);
        if (index < 0 || index >= contributions.size())
            return false;
        ContributionDialog dialog = new ContributionDialog(getActivity());
        dialog.contribution = contributions.get(index);
        dialog.show();
        return true;
    }

    private void getContributions() {
        if (bounds == null)
            return;
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

        String token = UsersManager.getToken(getContext());
        Log.i("token", token);
        Call<ContributionsResponse> call = apiManager
                .getContributions(token,
                        String.valueOf(bounds.northeast.latitude),
                        String.valueOf(bounds.northeast.longitude),
                        String.valueOf(bounds.southwest.latitude),
                        String.valueOf(bounds.southwest.longitude),
                        String.valueOf(mMap.getCameraPosition().zoom));

        call.enqueue(new Callback<ContributionsResponse>() {
            @Override
            public void onResponse(Call<ContributionsResponse> call, Response<ContributionsResponse> response) {

                if (response.body() != null && response.body().contributions != null) {
                    addContributions(response.body().contributions);
                } else {
                    addContributions(new ArrayList<Contribution>());

                }
            }

            @Override
            public void onFailure(Call<ContributionsResponse> call, Throwable t) {

            }
        });


        apiManager
                .getLatestContribution(UsersManager.getToken(getContext()),
                        String.valueOf(bounds.northeast.latitude),
                        String.valueOf(bounds.northeast.longitude),
                        String.valueOf(bounds.southwest.latitude),
                        String.valueOf(bounds.southwest.longitude),
                        String.valueOf(mMap.getCameraPosition().zoom)).enqueue(new Callback<LatestContributionResponse>() {
            @Override
            public void onResponse(Call<LatestContributionResponse> call, Response<LatestContributionResponse> response) {
                if (response.body() != null) {
                    LatestContributionResponse latestContributionResponse = response.body();
                    setLatestContribution(latestContributionResponse.time);
                }

            }

            @Override
            public void onFailure(Call<LatestContributionResponse> call, Throwable t) {

            }
        });

    }

    private void setLatestContribution(LatestContributionResponse.Time time) {
        if (time == null || time.date.contentEquals("")) {
            latestContributionTextView.setVisibility(View.GONE);
            return;
        }
        String timestamp = Utils.formatDateString(getContext(), time.date);

        latestContributionTextView.setText
                (String.format(Locale.getDefault(), "%s: %s", getString(R.string.last_contribution), timestamp));
        latestContributionTextView.setVisibility(View.VISIBLE);
        if (latestContributionRunnable != null && latestContributionHandler != null)
            latestContributionHandler.removeCallbacks(latestContributionRunnable);

        latestContributionHandler = new Handler();
        latestContributionRunnable = new Runnable() {

            @Override
            public void run() {
                if (getActivity() != null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (latestContributionTextView != null)
                                latestContributionTextView.setVisibility(View.GONE);
                        }
                    });


            }
        };
        latestContributionHandler.postDelayed(latestContributionRunnable, 10000);

        latestContributionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latestContributionTextView.setVisibility(View.GONE);
                getContributions();
            }
        });
    }

    private void addContributions(ArrayList<Contribution> contributions) {
        this.contributions = contributions;
        addMarkers();
        setCounters();

    }

    private void setCounters() {
        int foodBoxesCount = 0;
        int hotMealsCount = 0;
        int clothesCount = 0;

        for (Contribution contribution : contributions) {
            if (contribution.type.contentEquals("food_boxes"))
                foodBoxesCount++;
            else if (contribution.type.contentEquals("clothes"))
                clothesCount++;
            else
                hotMealsCount++;
        }

        ((MainActivity) getActivity()).setFoodBoxesCount(String.valueOf(foodBoxesCount));
        ((MainActivity) getActivity()).setHotMealsCount(String.valueOf(hotMealsCount));
        ((MainActivity) getActivity()).setClothesCount(String.valueOf(clothesCount));

    }

    private void addMarkers() {
        mMap.clear();
        markers = new ArrayList<>();
        for (Contribution contribution : contributions) {
            markers.add(addMarker(contribution));
        }
    }

    private Marker addMarker(Contribution contribution) {
        LatLng contributionLatLng = new LatLng(contribution.latitude, contribution.longitude);
        int drawable;
        int color;
        int radius;

        if (contribution.type.contentEquals("hot_meals")) {
            drawable = R.drawable.pin_hot_meals;
            color = ContextCompat.getColor(getContext(), R.color.transparent_blue);
        } else if (contribution.type.contentEquals("clothes")) {
            drawable = R.drawable.pin_clothes;
            color = ContextCompat.getColor(getContext(), R.color.transparent_purple);
        } else {
            drawable = R.drawable.pin_food_boxes;
            color = ContextCompat.getColor(getContext(), R.color.transparent_brown);
        }

        try {
            int quantity = Integer.parseInt(contribution.quantity);
            radius = quantity * 0.2 > 200 ? 200 : (int) (quantity * 0.2);

        } catch (Exception e) {
            radius = 200;
        }


        Marker marker = mMap.addMarker(new MarkerOptions().
                position(contributionLatLng).icon(BitmapDescriptorFactory.fromResource(drawable)));


        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(contribution.latitude, contribution.longitude))
                .fillColor(color)
                .strokeWidth(0)
                .radius(radius); // In meters

        mMap.addCircle(circleOptions);
        return marker;
    }


    private void getLocation() {
        if (!checkPermissions())
            return;

        locationManager = new LocationManager(getContext(), this);
        if (!locationManager.isLocationEnabled()) {
            Utils.showLocationIsNotRequestedPopup(getActivity());
        }

        locationManager.getLocation();

    }

    private boolean checkPermissions() {
        if ((ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            return false;

        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getLocation();

                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.RECORD_AUDIO)) {
                    new AlertDialog.Builder(getActivity())
                            .setMessage(getString(R.string.allow_location_permission))
                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
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
                                    i.setData(Uri.parse("package:" + getActivity().getPackageName()));
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

    @Override
    public void onGetLocation(Location location) {
        userLocation = location;

        if (getContext() == null)
            return;

        if (mMap != null) {
            LatLng currentLocation = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(currentLocation));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15.0f));
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);

            }

        }

        locationManager.removeListener();
    }

    public LatLng getTargetLocation() {
        return mMap.getCameraPosition().target;
    }

    public float getZoomLevel() {
        return mMap.getCameraPosition().zoom;
    }


    public void gotoAddress(Address address){
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
    }
}
