package dat255.eventify.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import java.text.SimpleDateFormat;
import java.util.Date;
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

    //Facebook
    private ProfileTracker profileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        googleApi = new GoogleApi(this);
        adapter = new MainListAdapter();

        //Start collecting events if we have access to the internet
        if (ConnectionManager.getInstance().isConnected()) {
            startService(new Intent(MainActivity.this, FetchEventService.class));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initCalendarDropDown();

        //Only display logout button when using app with Facebook
        navigationView.getMenu().findItem(R.id.nav_logout).setVisible(StorageManager.getInstance().getLoginType().equals("facebook"));

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
                } else {
                    adapter.updateEventList();
                }
                swipeRefresh.setRefreshing(false);
            }
        });
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
        mCompactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                ViewCompat.animate(arrow).rotation(0).start();
                mAppBarLayout.setExpanded(false);
                isCalendarExpanded = false;
                //TODO: Scroll to the picked date
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

    @Override
    public void onPause() {
        super.onPause();

        //Stop location updates when Activity is no longer active
        if (googleApi.getmGoogleApiClient() != null) {
            if (googleApi.getmGoogleApiClient().isConnected()) {
                googleApi.removeLocationUpdates();
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

        if (id == R.id.nav_profile) {

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
    public void onResume() {
        super.onResume();
        adapter.updateEventList();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        adapter.updateEventList();
    }

    @Override
    public void onStart(){
        super.onStart();
        adapter.updateEventList();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void onDestroy() {
        super.onDestroy();

        if (profileTracker != null){
            profileTracker.stopTracking();
        }
    }
}