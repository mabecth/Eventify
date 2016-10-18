package dat255.eventify.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.firebase.auth.FirebaseAuth;
import com.github.sundeepk.compactcalendarview.domain.Event;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import dat255.eventify.R;
import dat255.eventify.util.FetchEventService;
import dat255.eventify.view.adapter.MainListAdapter;
import dat255.eventify.manager.ConnectionManager;
import dat255.eventify.util.GoogleApi;
import dat255.eventify.manager.StorageManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    //Google
    private GoogleApi googleApi;

    //Event list
    private ListView listView;
    private MainListAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private CompactCalendarView mCompactCalendarView;
    private AppBarLayout mAppBarLayout;
    private boolean isCalendarExpanded = false;
    private boolean onlyFavorites = false;

    private TextView toolbarTitle;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    //Facebook
    private ProfileTracker profileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            GoogleApi.getLocationManager(this).build();
        }else {
            checkLocationPermission();
        }
        System.out.println("oncreate");
        adapter = new MainListAdapter();

        //Start collecting events if we have access to the internet
        if (ConnectionManager.getInstance().isConnected()) {
            startService(new Intent(MainActivity.this, FetchEventService.class));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);

        toolbarTitle.setText(R.string.app_name);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initCalendarDropDown();

        //Only display logout button when using app with Facebook
        if (StorageManager.getInstance().getLoginType().equals("guest")) {
            navigationView.getMenu().findItem(R.id.nav_logout).setTitle(R.string.login);
        }

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

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Start collecting events if we have access to the internet
                if (ConnectionManager.getInstance().isConnected()) {
                    startService(new Intent(MainActivity.this, FetchEventService.class));
                    adapter.updateEventList();
                } else {
                    adapter.updateEventList();
                }
                swipeRefresh.setRefreshing(false);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("HELLOOOOO");
                adapter.setChosenEvent(i);
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                startActivity(intent);
            }
        });
        GoogleApi.getLocationManager(this);


    }


    public boolean checkLocationPermission() {
        System.out.println("Checking persmission");
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                //TODO:
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                //(just doing it here for now, note that with this code, no explanation is shown)

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
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
                        System.out.println("permission granted");
                        GoogleApi.getLocationManager(this).build();
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void initCalendarDropDown(){
        //No title
        CollapsingToolbarLayout mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        mCollapsingToolbar.setTitle(" ");

        mAppBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        mAppBarLayout.setExpanded(false);

        final ImageView arrow = (ImageView) findViewById(R.id.arrow);

        // Set up the CompactCalendarView
        mCompactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        mCompactCalendarView.setLocale(TimeZone.getDefault(), Locale.ENGLISH);
        mCompactCalendarView.setShouldDrawDaysHeader(true);
        List<Event> eventToShowInCal = new ArrayList<>();
        for (dat255.eventify.model.Event e : StorageManager.getInstance().getEvents()) {
            System.out.println(e.getEventTimeInMillis());
            eventToShowInCal.add(new Event(Color.parseColor("#039BE5"), e.getEventTimeInMillis()));
        }
        mCompactCalendarView.addEvents(eventToShowInCal);
        mCompactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                ViewCompat.animate(arrow).rotation(0).start();
                mAppBarLayout.setExpanded(false);
                isCalendarExpanded = false;

                int index = StorageManager.getInstance().getIndexForDate(dateClicked);
                listView.setSelection(index);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                setMonthText(firstDayOfNewMonth);
            }
        });
        setMonthText(mCompactCalendarView.getFirstDayOfCurrentMonth());

        ImageButton calendarBtn = (ImageButton) findViewById(R.id.calBtn);

        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCalendarExpanded) {
                    ViewCompat.animate(arrow).rotation(0).start();
                    mAppBarLayout.setExpanded(false, true);
                    isCalendarExpanded = false;
                } else {
                    ViewCompat.animate(arrow).rotation(180).start();
                    mAppBarLayout.setExpanded(true, true);
                    isCalendarExpanded = true;
                }
            }
        });
    }

    public void setMonthText(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
        TextView monthView = (TextView) findViewById(R.id.monthTextView);
        monthView.setText(dateFormat.format(date));
    }

    public void updateAdapter(){
        adapter.updateEventList();
    }

    @Override
    public void onPause() {
        super.onPause();
        //Stop location updates when Activity is no longer active
       if (GoogleApi.getLocationManager(this).getmGoogleApiClient() != null) {
            if (GoogleApi.getLocationManager(this).getmGoogleApiClient().isConnected()) {
                GoogleApi.getLocationManager(this).removeLocationUpdates();
            }
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

        if (id == R.id.nav_home) {
            toolbarTitle.setText(R.string.app_name);
            onlyFavorites = false;
            adapter.setOnlyFavorite(onlyFavorites);
            adapter.updateEventList();

        } else if (id == R.id.nav_my_events) {
            toolbarTitle.setText(R.string.my_events);
            onlyFavorites = true;
            adapter.setOnlyFavorite(onlyFavorites);
            adapter.updateEventList();
            Log.e("shiet","Button pressed");

        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
        } else if (id == R.id.nav_logout) {
            if (StorageManager.getInstance().getLoginType().equals("facebook")) {
                LoginManager.getInstance().logOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(GoogleApi.getLocationManager(this).getmGoogleApiClient()!=null) {
            if (GoogleApi.getLocationManager(this).getmGoogleApiClient().isConnected()) {
                GoogleApi.getLocationManager(this).loopCoordinates();
            }
        }
        adapter.updateEventList();
        toolbarTitle.setText(R.string.app_name);

        if (StorageManager.getInstance().getLoginType().equals("facebook") && Profile.getCurrentProfile() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        if (!onlyFavorites) {
            toolbarTitle.setText(R.string.app_name);
        } else {
            toolbarTitle.setText(R.string.my_events);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        System.out.println("onrestart");
        //adapter.updateEventList();
        if (StorageManager.getInstance().getLoginType().equals("facebook") && Profile.getCurrentProfile() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onStart(){
        System.out.println("OnStart");
        super.onStart();
        adapter.updateEventList();
        if(GoogleApi.getLocationManager(this).getmGoogleApiClient()!=null) {
            if (GoogleApi.getLocationManager(this).getmGoogleApiClient().isConnected()) {
                GoogleApi.getLocationManager(this).loopCoordinates();
            }
        }

        if (StorageManager.getInstance().getLoginType().equals("facebook") && Profile.getCurrentProfile() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(GoogleApi.getLocationManager(this).getmGoogleApiClient()!=null) {
            GoogleApi.getLocationManager(this).getmGoogleApiClient().disconnect();
        }
        System.out.println("ondestroy");
        if (profileTracker != null){
            profileTracker.stopTracking();
        }
    }
}