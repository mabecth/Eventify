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

import android.location.Location;
import android.os.Handler;
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
        implements NavigationView.OnNavigationItemSelectedListener{

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

    public static GoogleApi googleApi;
    /**
     * The formatted location address.
     */
    protected String mAddressOutput;

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */




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


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Reach views from nav_header_main.xml
        View view = navigationView.getHeaderView(0);
        TextView fbName = (TextView) view.findViewById(R.id.nameTV);
        ProfilePictureView fbPicture = (ProfilePictureView) view.findViewById(R.id.profilePictureIV);

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

    @Override
    public void onPause() {
        super.onPause();
        //stop location updates when Activity is no longer active
        if (googleApi.getmGoogleApiClient() != null) {
            googleApi.removeLocationUpdates();
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
    public void onStart(){
        super.onStart();
        googleApi = new GoogleApi(this);
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
        /*if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }*/

    }

    public void onDestroy() {
        super.onDestroy();
        if (profileTracker != null){
            profileTracker.stopTracking();
        }
    }



}


