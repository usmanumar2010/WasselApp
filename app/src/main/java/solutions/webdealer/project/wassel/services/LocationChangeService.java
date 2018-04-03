package solutions.webdealer.project.wassel.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.media.audiofx.BassBoost;
import android.media.audiofx.EnvironmentalReverb;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import solutions.webdealer.project.wassel.R;

import static com.facebook.FacebookSdk.getApplicationContext;

public class LocationChangeService extends Service implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 5000;
    private static final float LOCATION_DISTANCE = 10f;
    private SharedPreferences sharedPreferences;
    private String order_id_is;
    private String MYORDERMAJORDETAIL="myordermajordetail";
    private Intent intent;
    private String driver_id_is;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 met
    private LocationListener mLocationListener;

//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        displayLocation();
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//        mGoogleApiClient.connect();
//    }
//


//    private class LocationListener implements android.location.LocationListener  {
//        Location mLastLocation;
//
//        public LocationListener(String provider) {
//            Log.e(TAG, "LocationListener " + provider);
//            mLastLocation = new Location(provider);
//        }
//
//        @Override
//        public void onLocationChanged(Location location) {
//            Log.e(TAG, "onLocationChanged: " + location);
//            mLastLocation.set(location);
//
//            String userId; // Came from Order through FCM
//            double lat = location.getLatitude();
//            double log = location.getLongitude();
//            sendLocation(lat, log);
//
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//            Log.e(TAG, "onProviderDisabled: " + provider);
//            Toast.makeText(LocationChangeService.this, "Kindly on tour GPS", Toast.LENGTH_SHORT).show();
////            Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
////            startActivity(intent);
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//            Log.e(TAG, "onProviderEnabled: " + provider);
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//            Log.e(TAG, "onStatusChanged: " + provider);
//        }
//    }
//
//    LocationListener[] mLocationListeners = new LocationListener[]{
//            new LocationListener(LocationManager.GPS_PROVIDER),
//            new LocationListener(LocationManager.NETWORK_PROVIDER)
//    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        order_id_is=intent.getStringExtra("order_id");
        driver_id_is=intent.getStringExtra("driver_id");
        return START_STICKY;
    }

//    @Override
//    public void onCreate() {
//        try {
//            Log.e(TAG, "onCreate");
//            sharedPreferences = getSharedPreferences(MYORDERMAJORDETAIL, Context.MODE_PRIVATE);
////        order_id_is = sharedPreferences.getString("order_id", null);
//
//
//            Toast.makeText(this, "i m in location server !! hurrah", Toast.LENGTH_SHORT).show();
//
//
//            initializeLocationManager();
//            try {
//                mLocationManager.requestLocationUpdates(
//                        LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
//                        mLocationListeners[1]);
//            } catch (java.lang.SecurityException ex) {
//                Log.i(TAG, "fail to request location update, ignore", ex);
//                Toast.makeText(this, "Kindly check your internet", Toast.LENGTH_SHORT).show();
//            } catch (IllegalArgumentException ex) {
//                Log.d(TAG, "network provider does not exist, " + ex.getMessage());
//                Toast.makeText(this, "Kindly check your internet", Toast.LENGTH_SHORT).show();
//            }
//            try {
//                mLocationManager.requestLocationUpdates(
//                        LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
//                        mLocationListeners[0]);
//            } catch (java.lang.SecurityException ex) {
//                Log.i(TAG, "fail to request location update, ignore", ex);
////            Toast.makeText(this, "Kindly check your internet", Toast.LENGTH_SHORT).show();
//            } catch (IllegalArgumentException ex) {
//                Log.d(TAG, "gps provider does not exist " + ex.getMessage());
//                Toast.makeText(this, "Kindly check your internet", Toast.LENGTH_SHORT).show();
//            }
//        }catch (Exception e)
//        {
//            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
//        }
//    }



    //--------
//    @Override
//    public void onDestroy() {
//        Log.e(TAG, "onDestroy");
//        super.onDestroy();
//        if (mLocationManager != null) {
//            for (int i = 0; i < mLocationListeners.length; i++) {
//                try {
//                    mLocationManager.removeUpdates(mLocationListeners[i]);
//                } catch (Exception ex) {
//                    Log.i(TAG, "fail to remove location listners, ignore", ex);
////                    Toast.makeText(this, "Kindly check your internet", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//
//    }


    @Override
    public void onCreate() {
        try{

            // First we need to check availability of play services

                buildGoogleApiClient();
            createLocationRequest();


        }catch (Exception e)
        {
            Toast.makeText(this, "There is an exception", Toast.LENGTH_SHORT).show();
        }
    }

//

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationListener);
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API).build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).addApi(LocationServices.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                mLocationRequest = LocationRequest.create();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setInterval(9000);

                setLocationListener();
            }

            @Override
            public void onConnectionSuspended(int i) {
                mGoogleApiClient.connect();

            }
        }).build();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Ooops!! something wrong", Toast.LENGTH_SHORT).show();
    }
    private void setLocationListener()
    {
        mLocationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                sendLocation(location.getLatitude(),location.getLongitude());
            }
        };

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mLocationListener);
    }

    /**
     * Method to display the location on UI
     * */
    private void displayLocation() {

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            sendLocation(latitude,longitude);

        } else {

            Toast.makeText(this, "Couldn't get the location. Make sure location is enabled on the device", Toast.LENGTH_SHORT).show();
        }

    }









    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    public void sendLocation(final double lat, final double log) {

        final String strLat = String.valueOf(lat);
        final String strLog = String.valueOf(log);

        String url = "http://ec2-34-208-61-63.us-west-2.compute.amazonaws.com/api/setDriverLocation";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString()+" "+"LocationServiceError", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("order_id",order_id_is);
                params.put("driver_id",driver_id_is);
                params.put("longitude", strLog);
                params.put("latitude", strLat);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }


}