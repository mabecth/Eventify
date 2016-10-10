package dat255.refugeeevent;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;
import android.view.View;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import dat255.refugeeevent.Adapter.MainListAdapter;
import dat255.refugeeevent.model.Event;

import android.os.ResultReceiver;
import android.content.Intent;

import android.location.Geocoder;

import android.location.Location;

import android.os.Bundle;

import android.os.Handler;

import android.os.ResultReceiver;

import android.support.v7.app.ActionBarActivity;

import android.util.Log;

import android.view.View;

import android.widget.Button;

import android.widget.ProgressBar;

import android.widget.TextView;

import android.widget.Toast;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    protected static final String TAG = "main-activity";



    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";

    protected static final String LOCATION_ADDRESS_KEY = "location-address";

    private String origin = "";
    private String destination = "";
    private LocationRequest mLocationRequest;
    private TextView distanceTextView;
    private TextView destinationTextView;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LatLng latLng;
    private List<Event> listOfEvents;
    private TextView locationTextView;

    protected boolean mAddressRequested;

    /**
     * The formatted location address.

     */
    protected String mAddressOutput;

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.

     */
    private AddressResultReceiver mResultReceiver;



    //EventList
    private ListView listView;
    private MainListAdapter adapter;

    //Facebook
    private ProfileTracker profileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mResultReceiver = new AddressResultReceiver(new Handler());

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Reach views from nav_header_main.xml
        View view = navigationView.getHeaderView(0);
        TextView fbName = (TextView) view.findViewById(R.id.nameTV);
        ProfilePictureView fbPicture = (ProfilePictureView) view.findViewById(R.id.profilePictureIV);


        /* Check for latest version of Play services */
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
            }
        }
        else {
            buildGoogleApiClient();
        }

        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;
        mAddressOutput = "";


        //Retrieve public profile info
        if(Profile.getCurrentProfile() == null) {
            profileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                    // profile2 is the new profile
                    profileTracker.stopTracking();
                }
            };

        } else {
            //Use available info to update views
            if (Profile.getCurrentProfile().getName() != null) {
                fbName.setText(Profile.getCurrentProfile().getName());
            }
            if (Profile.getCurrentProfile().getId() != null) {
                fbPicture.setProfileId(Profile.getCurrentProfile().getId());
            }
        }



    }

    public void calculateDistance() {
        for(int i = 0; i < adapter.getCount(); i++) {
            new JSONTask(this, i).execute("https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" + latLng.toString().replaceAll("[()]", "").replaceAll("lat/lng:", "").replaceAll(" ", "") + "&destinations=" + listOfEvents.get(i).getPlace() + "&key=AIzaSyCPkKLGhAjwksL-irs3QOElaLvoGD6aePA");
        }
    }

    public void updateDistance(int id, String result){
        adapter.getListOfEvents().get(id).setDistance(result);
        listView.invalidateViews();
    }


    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
       if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void fetchAddressButtonHandler(View view) {

        // We only start the service to fetch the address if GoogleApiClient is connected.

        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            startIntentService();
        }

        // If GoogleApiClient isn't connected, we process the user's request by setting

        // mAddressRequested to true. Later, when GoogleApiClient connects, we launch the service to

        // fetch the address. As far as the user is concerned, pressing the Fetch Address button

        // immediately kicks off the process of getting the address.

        mAddressRequested = true;

    }

    protected void startIntentService() {
        System.out.println("Create intent");

        // Create an intent for passing to the intent service responsible for fetching the address.

        Intent intent = new Intent(this, FetchAddressIntentService.class);
        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        // Start the service. If the service isn't already running, it is instantiated and started

        // (creating a process for it if needed); if it is running then it remains running. The

        // service kills itself automatically once all intents are processed.
        startService(intent);

    }
    public void displayAddressOutput() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);
        locationTextView = (TextView) view.findViewById(R.id.locationTV);
        locationTextView.setText(mAddressOutput);
    }

    protected void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1);
        mLocationRequest.setFastestInterval(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
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
    @Override
    public void onConnectionSuspended(int i) {

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

    /**

     * Runs when a GoogleApiClient object successfully connects.

     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
       // calculateDistance();
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                //TODO:
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                //(just doing it here for now, note that with this code, no explanation is shown)
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }

                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_translate) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_logout) {
            LoginManager.getInstance().logOut();
            startActivity(new Intent(this, LoginActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
        locationTextView = (TextView) findViewById(R.id.locationTV);
        //Longs skitkod rör ej
        listView = (ListView) findViewById(R.id.listView);
        adapter = new MainListAdapter();
        listView.setAdapter(adapter);
        listOfEvents = adapter.getListOfEvents();

    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

    }

    public void onDestroy() {
        super.onDestroy();
        if (profileTracker != null){
            profileTracker.stopTracking();
        }
    }

    class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {

            super(handler);

        }

        /**

         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.

         */

        @Override

        protected void onReceiveResult(int resultCode, Bundle resultData) {
            // Display the address string or an error message sent from the intent service.

            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            displayAddressOutput();
            // Show a toast message if an address was found.

            // Reset. Enable the Fetch Address button and stop showing the progress bar.



        }

    }
}


