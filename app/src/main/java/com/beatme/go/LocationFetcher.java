package com.beatme.go;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;

public class LocationFetcher implements LocationListener {

    private final String TAG = "LocationFetcher";
    private final Context mContext;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    Location mLocation;
    protected LocationManager locationManager;
    double latitude, longitude;

    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 10;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 25;

    public LocationFetcher(Context context){
        this.mContext = context;
        getUserLocation();
    }

    public Location getUserLocation(){
        try {
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

            this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSEnabled && !isNetworkEnabled){
                showAlert();
            } else {
                this.canGetLocation = true;
                if(isNetworkEnabled){
                    locationManager.requestLocationUpdates(
                                        LocationManager.NETWORK_PROVIDER,
                                        MIN_TIME_BW_UPDATES,
                                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null){
                        mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if(mLocation != null){
                            latitude = mLocation.getLatitude();
                            longitude = mLocation.getLongitude();
                        }
                    }
                }

                if(isGPSEnabled){
                    if (mLocation == null){
                        locationManager.requestLocationUpdates(
                                        LocationManager.GPS_PROVIDER,
                                        MIN_TIME_BW_UPDATES,
                                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null){
                            mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if(mLocation != null){
                                latitude = mLocation.getLatitude();
                                longitude = mLocation.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e){
            Log.e(TAG, "Get Location: "+e.toString());
        }
        return mLocation;
    }

    private void showAlert(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app.")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    public void stopLocationService(){
        if (locationManager != null){
            locationManager.removeUpdates(LocationFetcher.this);
        }
    }

    public double getLatitude(){
        if (mLocation != null){
            return mLocation.getLatitude();
        }
        return latitude;
    }

    public double getLongitude(){
        if (mLocation != null){
            return mLocation.getLongitude();
        }
        return longitude;
    }

    public boolean isCanGetLocation(){
        return this.canGetLocation;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
