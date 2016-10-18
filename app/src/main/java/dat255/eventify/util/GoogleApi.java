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

    public List<Event> getUpdatedList(){
        return updatedList;
    }

    public static GoogleApi getLocationManager(Context context)     {
        if (instance == null) {
            instance = new GoogleApi(context);
        }
        return instance;
    }

    private GoogleApi(Context context) {
        mResultReceiver = new AddressResultReceiver(new Handler());
        mainActivity = (MainActivity) context;
        listOfEvents = StorageManager.getInstance().getEvents();
        System.out.println("google api");

        listView = (ListView) mainActivity.findViewById(R.id.listView);
        adapter = new MainListAdapter();
        listView.setAdapter(adapter);

        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(StorageManager.getInstance().getEventsKey())) {
                    //Events changed
                    System.out.println("Google api");
                    Log.d(TAG, "Events in storage changed!");
                    listOfEvents = StorageManager.getInstance().getEvents();
                    updatedList = StorageManager.getInstance().getEvents();
                }
            }
        };

        StorageManager.getInstance().registerOnSharedPreferenceChangeListener(listener);

        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;
        mAddressOutput = "";
           /* Check for latest version of Play services */

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

        if (mLastLocation != null) {
            if(ConnectionManager.getInstance().isConnected()) {
                Log.d(TAG, "Service started");
                startIntentService();
                //calculateDistance();
            }
            else{
                mAddressOutput = "Recent: " + StorageManager.getInstance().getAdress();
                displayAddressOutput();
            }
        }
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void removeLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void calculateDistance() {

        System.out.println("Calculate distance");
        System.out.println("Adapter count: " + adapter.getCount());
        System.out.println("Storage list: " + listOfEvents.size());
        System.out.println("Latlng: " + latLng);

        if (latLng != null && listOfEvents.size() > 0) {
            System.out.println("Adapter count if: " + adapter.getCount());

            for (int index = 0; index < listOfEvents.size(); index++) {

                System.out.println("Adapter in loop: " + adapter.getCount());
                System.out.println("int i:" + index);
                new ParseDistanceAsyncTask(this, index).execute("https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" + latLng.toString().replaceAll("[()]", "").replaceAll("lat/lng:", "").replaceAll(" ", "") + "&destinations=" + listOfEvents.get(index).getPlace().replaceAll(" ", "") + "&key=AIzaSyBaDd7HOHSt5IHeCAqWr_MDTXqsYpgZRhQ");

            }
            adapter.updateEventList();
            listView.invalidateViews();
        }
    }

    public void updateDistance(int id, String result) {
        updatedList.get(id).setDistance(result);
        StorageManager.getInstance().storeEvents(updatedList);
        adapter.updateEventList();
        listView.invalidateViews();

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
        calculateDistance();
        //Set location in coordinates

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
