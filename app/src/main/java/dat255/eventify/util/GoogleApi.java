package dat255.eventify.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import dat255.eventify.manager.ConnectionManager;
import dat255.eventify.manager.StorageManager;
import dat255.eventify.activity.MainActivity;
import dat255.eventify.R;
import dat255.eventify.model.Event;
import dat255.eventify.view.adapter.MainListAdapter;

public class GoogleApi implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    protected static final String TAG = "GoogleApi";
    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";

    private static GoogleApi instance = null;

    private MainActivity mainActivity;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LatLng latLng;
    private ListView listView;
    private MainListAdapter adapter;
    private List<Event> listOfEvents;
    private LocationRequest mLocationRequest;
    private TextView locationTextView;
    private static List<Event> updatedList;
    protected boolean mAddressRequested;
    protected String mAddressOutput;
    private AddressResultReceiver mResultReceiver;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public static GoogleApi getLocationManager(MainActivity activity)     {
        if (instance == null) {
            instance = new GoogleApi(activity);
        }
        return instance;
    }

    private GoogleApi(MainActivity activity) {
        mResultReceiver = new AddressResultReceiver(new Handler());
        mainActivity = activity;
        mAddressRequested = false;
        mAddressOutput = "";
        listOfEvents = StorageManager.getInstance().getEvents();

        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(StorageManager.getInstance().getEventsKey())) {
                    //Events changed
                    mainActivity.updateAdapter();
                    System.out.println("Google api");
                    Log.d(TAG, "Events in storage changed!");
                    listOfEvents = StorageManager.getInstance().getEvents();
                    updatedList = StorageManager.getInstance().getEvents();
                    if (mLastLocation != null) {
                        if(ConnectionManager.getInstance().isConnected()) {
                            Log.d(TAG, "Service started");
                            startIntentService();
                            loopCoordinates();
                            //calculateDistance();
                        }
                        else{
                            mAddressOutput = "Recent: " + StorageManager.getInstance().getAdress();
                            displayAddressOutput();
                        }
                    }

                }
            }
        };

        StorageManager.getInstance().registerOnSharedPreferenceChangeListener(listener);
        // Set defaults, then update using values stored in the Bundle.
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public void loopCoordinates(){
        System.out.println("listofevents size "+ listOfEvents.size());
        if(latLng ==null){
        }else{
            if (listOfEvents.size() > 0) {
                for (int index = 0; index < listOfEvents.size(); index++) {
                    LatLng endLatLng = new LatLng(StorageManager.getInstance().getEvents().get(index).getLatitude(),StorageManager.getInstance().getEvents().get(index).getLongitude());
                    Double dbl = CalculationByDistance(latLng, endLatLng);
                    System.out.println("Distance: "+ dbl);
                    updateDistance(index, round(dbl,1) + " km");
                }
            }
        }
    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);
        return Radius * c;
    }

    public synchronized void build(){
        mGoogleApiClient = new GoogleApiClient.Builder(mainActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    protected void showToast(String text) {
        Toast.makeText(mainActivity, text, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1);
        mLocationRequest.setFastestInterval(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(mainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void removeLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showToast("Could not connect to Play Services");
    }


    public void updateDistance(int id, String result) {
        updatedList.get(id).setDistance(result);
        StorageManager.getInstance().storeEvents(updatedList);
        mainActivity.updateAdapter();
    }
    public void displayAddressOutput() {
        NavigationView navigationView = (NavigationView) mainActivity.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(mainActivity);
        View view = navigationView.getHeaderView(0);
        locationTextView = (TextView) view.findViewById(R.id.locationTV);
        locationTextView.setText(mAddressOutput);
        Log.d(TAG, mAddressOutput);

    }

    protected void startIntentService() {
          // Create an intent for passing to the intent service responsible for fetching the address.
           Intent intent = new Intent(mainActivity, FetchAddressIntentService.class);
           // Pass the result receiver as an extra to the service.
           intent.putExtra(Constants.RECEIVER, mResultReceiver);

           // Pass the location data as an extra to the service.
           intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
           // Start the service. If the service isn't already running, it is instantiated and started
           // (creating a process for it if needed); if it is running then it remains running. The
           // service kills itself automatically once all intents are processed.
           mainActivity.startService(intent);

    }


    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        //Get coordinates
        latLng = new LatLng(location.getLatitude(), location.getLongitude());

        updatedList = StorageManager.getInstance().getEvents();

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
           Log.e(TAG, "ConnectionManager suspended");
             mGoogleApiClient.connect();
    }





    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
           /* mAddressOutput = mAddressOutput.replace("\n", " ");
            String[] split = mAddressOutput.split("\\s+");
            mAddressOutput = split[split.length - 1]; // Only display city
            */
            StorageManager.getInstance().storeAdress(mAddressOutput);
            //calculateDistance();
            displayAddressOutput();
            // Show a toast message if an address was found.
            // Reset. Enable the Fetch Address button and stop showing the progress bar.
        }
    }
}
