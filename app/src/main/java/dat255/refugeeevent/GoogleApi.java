package dat255.refugeeevent;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import java.util.List;

import dat255.refugeeevent.Adapter.MainListAdapter;
import dat255.refugeeevent.model.Event;



/**
 * Created by Surface pro 3 on 2016-10-11.
 */

public class GoogleApi implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    MainActivity mainActivity;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LatLng latLng;
    private ListView listView;
    private MainListAdapter adapter;
    private List<Event> listOfEvents;
    private LocationRequest mLocationRequest;
    private TextView locationTextView;

    protected boolean mAddressRequested;
    protected String mAddressOutput;
    private AddressResultReceiver mResultReceiver;




    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public GoogleApi(Context context){
        mResultReceiver = new AddressResultReceiver(new Handler());
        mainActivity = (MainActivity) context;
           /* Check for latest version of Play services */
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mainActivity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
            }
        }
        else {
            buildGoogleApiClient();
        }

        listView = (ListView) mainActivity.findViewById(R.id.listView);
        adapter = new MainListAdapter();
        listView.setAdapter(adapter);
        listOfEvents = adapter.getListOfEvents();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mainActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    public void updateDistance(int id, String result){
        adapter.getListOfEvents().get(id).setDistance(result);
        listView.invalidateViews();
    }

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(mainActivity,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(mainActivity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                //TODO:
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                //(just doing it here for now, note that with this code, no explanation is shown)
                ActivityCompat.requestPermissions(mainActivity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(mainActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
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

            // It is possible that the user presses the button to get the address before the

            // GoogleApiClient object successfully connects. In such a case, mAddressRequested

            // is set to true, but no attempt is made to fetch the address (see

            // fetchAddressButtonHandler()) . Instead, we start the intent service here if the


            System.out.println("Start Service");
           startIntentService();

        }

    }

    public GoogleApiClient getmGoogleApiClient(){
        return mGoogleApiClient;
    }

    public void removeLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public void calculateDistance() {
        for(int i = 0; i < adapter.getCount(); i++) {
            new JSONTask(this, i).execute("https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" + latLng.toString().replaceAll("[()]", "").replaceAll("lat/lng:", "").replaceAll(" ", "") + "&destinations=" + listOfEvents.get(i).getPlace() + "&key=AIzaSyCPkKLGhAjwksL-irs3QOElaLvoGD6aePA");
        }
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


               public void displayAddressOutput() {
            NavigationView navigationView = (NavigationView) mainActivity.findViewById(R.id.nav_view);
         navigationView.setNavigationItemSelectedListener(mainActivity);
             View view = navigationView.getHeaderView(0);
            locationTextView = (TextView) view.findViewById(R.id.locationTV);
              locationTextView.setText(mAddressOutput);
                    System.out.println(mAddressOutput);
               }


    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;

        //Get coordinates
        latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //Calculates new distance to the events
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
           /*  Log.i(TAG, "Connection suspended");
             mGoogleApiClient.connect();
        */
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // calculateDistance();
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(mainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }

                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(mainActivity, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    class AddressResultReceiver extends ResultReceiver {
                 public AddressResultReceiver(Handler handler) {
                     super(handler);
                 }


                 @Override
                 protected void onReceiveResult(int resultCode, Bundle resultData) {
                     // Display the address string or an error message sent from the intent service.
                     mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
                     mAddressOutput = mAddressOutput.replace("\n", " ");
                     String[] split = mAddressOutput.split("\\s+");
                    mAddressOutput = split[split.length - 1]; // Only display city


                    displayAddressOutput();
                   // Show a toast message if an address was found.
                   // Reset. Enable the Fetch Address button and stop showing the progress bar.
               }
           }

}
