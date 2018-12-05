package com.nomade.movilremiscar.remiscarmovil.Util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * @author gino.ghiotto
 */
public class LocationRequester {
    public interface LocationListener {
        void setLocation(Location location);
    }

    private Activity activity;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationListener locationListener;
    LocationRequest mLocationRequest;

    public LocationRequester(Activity activity) {
        this.activity = activity;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.activity);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000);
        mLocationRequest.setFastestInterval(30000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try {
            locationListener = (LocationListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement LocationPermissionFragmentListener");
        }

        if(checkPermission()){
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            locationListener.setLocation(locationResult.getLastLocation());
        }
    };

    public void onPause() {

        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    public void getLocation() {
        if (checkPermission())
            if (mFusedLocationClient != null) {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // GPS location can be null if GPS is switched off
                                if (location != null) {
                                    //Log.d("list", "onSuccess: got location");
                                    locationListener.setLocation(location);
                                }
                            }
                        });
            }
    }
}
