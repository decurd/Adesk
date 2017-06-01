package kr.co.roonets.adesk.b.location;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import kr.co.roonets.adesk.MainActivity;

/**
 * 위치정보 제공.
 *
 * @author yeonkil choi
 * @since 2017. 3. 27
 */
public class CurrentLocation implements LocationListener {

    /**
     * 위치정보 수신시 이벤트 리스너.
     *
     * @see OnLocationEvent
     */
    public interface OnLocationListener {

        /**
         * On recieved location.
         *
         * @param location the location
         */
        void onRecievedLocation(Location location);
    }

    /** The context. */
    private Context context;

    /**
     * The on location listener.
     *
     * @see OnLocationListener
     */
    private OnLocationListener onLocationListener;

    /** The location manager. */
    private LocationManager locationManager;

    /** The location update interval. */
    private final long LOCATION_UPDATE_INTERVAL = 10000;

    /** The location update distance. */
    private final float LOCATION_UPDATE_DISTANCE = 10;

    private String currentProvider = null;

    /**
     * Instantiates a new current location.
     *
     * @param context the context
     * @param onLocationListener the on location listener
     */
    public CurrentLocation(Context context,
                           OnLocationListener onLocationListener) {
        this.context = context;
        this.onLocationListener = onLocationListener;

        this.locationManager = (LocationManager) context
                .getSystemService(Activity.LOCATION_SERVICE);
    }

    /**
     * Sets the on location listener.
     *
     * @param onLocationListener the new on location listener
     */
    public void setOnLocationListener(OnLocationListener onLocationListener) {
        this.onLocationListener = onLocationListener;
    }

    /**
     * Checks if is location enabled.
     *
     * @param context the context
     * @return true, if is location enabled
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressWarnings("deprecation")
    public boolean isLocationEnabled(Context context) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            String providers = Secure.getString(context.getContentResolver(),
                    Secure.LOCATION_PROVIDERS_ALLOWED);
            if (TextUtils.isEmpty(providers)) {
                return false;
            }
            return true;
        } else {
            final int locationMode;
            try {
                locationMode = Secure.getInt(context.getContentResolver(),
                        Secure.LOCATION_MODE);
            } catch (SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            switch (locationMode) {

                case Secure.LOCATION_MODE_HIGH_ACCURACY:
                case Secure.LOCATION_MODE_SENSORS_ONLY:
                case Secure.LOCATION_MODE_BATTERY_SAVING:
                    return true;
                case Secure.LOCATION_MODE_OFF:
                default:
                    return false;
            }
        }
    }

    /**
     * Find current location immediatley.
     *
     * @return the location
     */
    public Location findCurrentLocationImmediatley() {
        locationManager = (LocationManager) context
                .getSystemService(Activity.LOCATION_SERVICE);

        String locationProvider = getLocationProvider();
        Location location = null;
        if (locationProvider != null) {
            location = locationManager
                    .getLastKnownLocation(locationProvider);
        }

        return location;
    }

    /**
     * Gets the location provider.
     *
     * @return the location provider
     */
    public String getLocationProvider() {
        String bestProvider;
        Criteria criteria = new Criteria(); // Criteria는 위치 정보를 가져올 때 고려되는 옵션 정도로 생각하면 된다.
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);

        bestProvider = locationManager.getBestProvider(criteria, true);

        return bestProvider;
    }

    /**
     * Gets the last known location.
     *
     * @return the last known location
     */
    public Location getLastKnownLocation() {
        locationManager = (LocationManager) context
                .getSystemService(Activity.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getTime() > bestLocation.getTime()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    /**
     * Start location update.
     */
    public void startLocationUpdate() {
        String locationProvider = getLocationProvider();

        if (isLocationEnabled(context)) {
            Location location = getLastKnownLocation();

            if (location != null && onLocationListener != null) {
                onLocationListener.onRecievedLocation(location);
            }
            currentProvider = locationProvider;
            locationManager.requestLocationUpdates(locationProvider, LOCATION_UPDATE_INTERVAL, LOCATION_UPDATE_DISTANCE, this);
        } else {
            if (onLocationListener != null) {
                onLocationListener.onRecievedLocation(null);
            }
        }
    }

    /**
     * Stop location update.
     */
    public void stopLocationUpdate() {
        try{
            locationManager.removeUpdates(this);
        }catch(IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see android.location.LocationListener#onLocationChanged(android.location.Location)
     */
    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        if (onLocationListener != null) {
            onLocationListener.onRecievedLocation(location);
        }
    }

    /* (non-Javadoc)
     * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
        Log.e(provider, String.valueOf(status));
    }

    /* (non-Javadoc)
     * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
     */
    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
     */
    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
        if(provider.equals(currentProvider)){
            stopLocationUpdate();
            startLocationUpdate();
        }
    }
}
