package eg.com.theplanet.akram.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import eg.com.theplanet.akram.R;
import eg.com.theplanet.akram.managers.LocationManager;
import eg.com.theplanet.akram.utils.Constants;
import eg.com.theplanet.akram.utils.Utils;

/**
 * Created by georgenaiem on 5/30/16.
 */
public class PickLocationActivity extends AppCompatActivity implements OnMapReadyCallback, LocationManager.OnGetLocationListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    private static final int REQUEST_LOCATION_PERMISSION = 3;
    private FloatingActionButton myLocationFab;
    private GoogleMap mMap;
    private Location userLocation;
    private LocationManager locationManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_location);
        myLocationFab = (FloatingActionButton) findViewById(R.id.my_location_fab);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        myLocationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });


        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.pick_location);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();

                if (isTaskRoot()) {
                    startActivity(new Intent(this, MainActivity.class));
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        if (userLocation == null) {
            double latitude = getIntent().getExtras().getDouble(Constants.LATITUDE, 0);
            double longitude = getIntent().getExtras().getDouble(Constants.LONGITUDE, 0);
            double zoomLevel = getIntent().getExtras().getDouble(Constants.ZOOM_LEVEL, 0);

            if (latitude == 0) latitude = 30.044211f;
            if (longitude == 0) longitude = 31.215660f;
            if (zoomLevel == 0) zoomLevel = 14.0f;

            LatLng location = new LatLng(latitude, longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, (float) zoomLevel));
//        } else {
//            LatLng currentLocation = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14.0f));
//        }

        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);

    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().
                position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pick_location)));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Bundle bundle = getIntent().getExtras();
        bundle.putDouble(Constants.LATITUDE, marker.getPosition().latitude);
        bundle.putDouble(Constants.LONGITUDE, marker.getPosition().longitude);
        Intent intent = new Intent(this, FormActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
        return false;
    }

    private void getLocation() {
        if (!checkPermissions())
            return;

        locationManager = new LocationManager(this, this);
        if (!locationManager.isLocationEnabled()) {
            Utils.showLocationIsNotRequestedPopup(this);
        }

        locationManager.getLocation();

    }

    private boolean checkPermissions() {
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
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

                } else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                    new AlertDialog.Builder(this)
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

    @Override
    public void onGetLocation(Location location) {
        userLocation = location;
        if (mMap != null) {
            LatLng currentLocation = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(currentLocation));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14.0f));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);

            }

        }

        locationManager.removeListener();
    }


}
