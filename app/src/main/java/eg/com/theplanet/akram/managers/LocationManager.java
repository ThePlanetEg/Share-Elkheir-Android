package eg.com.theplanet.akram.managers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by georgenaiem on 9/29/15.
 */
public class LocationManager implements LocationListener {
    private Context mContext;
    private Location location;
    private android.location.LocationManager mManager;
    private OnGetLocationListener mListener;

    public LocationManager(Context context, OnGetLocationListener mListener) {
        this.mContext = context;
        this.mListener = mListener;
    }


    public boolean getLocation() {

        mManager = (android.location.LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            boolean requested = false;
            if (mManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
                mManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, 1000, 1, this);
                requested = true;
            }

            if (mManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
                mManager.requestLocationUpdates(android.location.LocationManager.NETWORK_PROVIDER, 1000, 1, this);
                requested = true;
            }

            return requested;
        }

        return false;

    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d("Location", location.toString());
        if (this.location == null) {
            this.location = location;
            if (mListener != null)
                mListener.onGetLocation(location);
        }
    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public boolean isLocationEnabled() {
        mManager = (android.location.LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            boolean requested = false;
            if (mManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
                requested = true;
            }

            if (mManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
                requested = true;
            }

            return requested;
        }
        return false;
    }

    public void removeListener() {
        mListener = null;
    }

    public interface OnGetLocationListener {
        void onGetLocation(Location location);
    }


}
