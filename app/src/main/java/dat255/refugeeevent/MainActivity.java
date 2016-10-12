package dat255.refugeeevent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.common.api.GoogleApiClient;
import android.view.View;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import dat255.refugeeevent.Adapter.MainListAdapter;
import dat255.refugeeevent.model.Event;
import android.location.Location;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;
import java.util.List;
import dat255.refugeeevent.service.EventHandler;
import dat255.refugeeevent.util.Storage;

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

    public static GoogleApi googleApi;
    protected String mAddressOutput;

    //EventList
    private ListView listView;
    private MainListAdapter adapter;
    private SwipeRefreshLayout swipRefresh;

    //Facebook
    private ProfileTracker profileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Start collecting events
        Storage.getInstance().setPreferences(this.getSharedPreferences("dat255.refugeeevent", Context.MODE_PRIVATE ));
        startService(new Intent(MainActivity.this, EventHandler.class));

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

        //Longs skitkod rör ej
        swipRefresh = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new MainListAdapter();

        swipRefresh.setRefreshing(true);
        if(swipRefresh.isRefreshing())
        {
            new CountDownTimer(1000,1000) {
                @Override
                public void onTick(long l) {
                    Log.e("Refresh","Time left" + l/1000);
                }

                @Override
                public void onFinish() {
                    Log.e("Finish", "CountDownDONE");
                    adapter.updateEventList();
                    listView.setAdapter(adapter);
                    swipRefresh.setRefreshing(false);
                }
            }.start();
        }
        listView.setAdapter(adapter);

        swipRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.updateEventList();
                swipRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        //stop location updates when Activity is no longer active

        if (googleApi.getmGoogleApiClient() != null && googleApi.getmGoogleApiClient().isConnected()) {
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
    public boolean onPrepareOptionsMenu (Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.setGroupVisible(R.id.group2, false); //Not working wtf
        return true;
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