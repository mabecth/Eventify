package dat255.eventify.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import dat255.eventify.manager.MyEventsManager;
import dat255.eventify.util.FetchEventService;
import dat255.eventify.util.LocationUtil;
import dat255.eventify.util.MyActivityListener;
import dat255.eventify.view.adapter.MainListAdapter;
import dat255.eventify.manager.ConnectionManager;
import dat255.eventify.manager.StorageManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MyActivityListener {

    //Google
    private LocationUtil locationUtil;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    //Event list
    private MyEventsManager myEventsManager;
    private MainListAdapter adapter;
    private ListView listView;
    private String allEvents = "1";
    private String onlyFavorites = "2";
    private String filtered = "3";
    private String typeOfList = allEvents;

    private TextView toolbarTitle;
    private SwipeRefreshLayout swipeRefresh;
    private CompactCalendarView mCompactCalendarView;
    private AppBarLayout mAppBarLayout;
    private boolean isCalendarExpanded = false;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    private FragmentManager fm;
    private FragmentTransaction fragtrans;

    private NavigationView navigationView;

    //Facebook
    private ProfileTracker profileTracker;

    @Override
    public void updateAdapter(){
        adapter.updateEventList();
    }

    @Override
    public void displayAddress(String mAddressOutput){
        View view = navigationView.getHeaderView(0);
        TextView locationTextView = (TextView) view.findViewById(R.id.locationTV);

        if (!mAddressOutput.equals(locationTextView.getText())) {
            locationTextView.setText(mAddressOutput);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFragment();
        checkLocationPermission();
        adapter = new MainListAdapter();
        myEventsManager = MyEventsManager.getInstance();

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

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initCalendarDropDown();

        //Only display logout button when using app with Facebook
        if (StorageManager.getInstance().getLoginType().equals("guest")) {
            navigationView.getMenu().findItem(R.id.nav_logout).setTitle(R.string.login);
        } else {
            navigationView.getMenu().findItem(R.id.nav_logout).setTitle(R.string.log_out);
        }

        setProfileInfo();

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            System.out.println("permission granted");
            locationUtil.buildGoogleApi();
            locationUtil.getmGoogleApiClient().connect();
            locationUtil.loopCoordinates();
        }
        adapter.updateEventList();
        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Start collecting events if we have access to the internet
                if (ConnectionManager.getInstance().isConnected()) {
                    startService(new Intent(MainActivity.this, FetchEventService.class));
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        locationUtil.loopCoordinates();
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        locationUtil.loopCoordinates();
                    }
                }
                adapter.updateEventList();
                swipeRefresh.setRefreshing(false);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                adapter.setChosenEvent(i);
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                startActivity(intent);
            }
        });
    }

    public void setProfileInfo() {
        //Reach views from nav_header_main.xml
        View view = navigationView.getHeaderView(0);
        TextView fbName = (TextView) view.findViewById(R.id.nameTV);
        ProfilePictureView fbPicture = (ProfilePictureView) view.findViewById(R.id.profilePictureIV);

        //Retrieve public profile info of the user
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

    public void initFragment() {
        fm = this.getSupportFragmentManager();
        fragtrans = fm.beginTransaction();
        fragtrans.add(new LocationUtil(), "LocationUtil");
        fragtrans.addToBackStack("LocationUtil");
        fragtrans.commit();
        fm.executePendingTransactions();
        locationUtil = (LocationUtil) fm.findFragmentByTag("LocationUtil");
    }

    public boolean checkLocationPermission() {
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
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
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
                        locationUtil.buildGoogleApi();
                        locationUtil.getmGoogleApiClient().connect();
                        locationUtil.loopCoordinates();
                        updateAdapter();
                    }

                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void initCalendarDropDown() {
        //No title
        CollapsingToolbarLayout mCollapsingToolbar = (CollapsingToolbarLayout)
                findViewById(R.id.collapsingToolbarLayout);
        mCollapsingToolbar.setTitle(" ");

        mAppBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        mAppBarLayout.setExpanded(false);

        // Set up the CompactCalendarView
        mCompactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        mCompactCalendarView.setLocale(TimeZone.getDefault(), Locale.ENGLISH);
        mCompactCalendarView.setShouldDrawDaysHeader(true);

        //if saved vaule == 2 show monday as first
        boolean showMondayFirst = StorageManager.getInstance().getSettings().
                get("firstDayOfWeek") == 2;
        mCompactCalendarView.setShouldShowMondayAsFirstDay(showMondayFirst);

        //If user change first day of week in settings, then update this calendar
        setUpOnSettingsChangedListener();

        //Show the events in the calendar view
        List<Event> eventToShowInCal = new ArrayList<>();
        for (dat255.eventify.model.Event e : StorageManager.getInstance().getEvents()) {
            System.out.println(e.getEventTimeInMillis());
            eventToShowInCal.add(new Event(Color.parseColor("#039BE5"), e.getEventTimeInMillis()));
        }
        mCompactCalendarView.addEvents(eventToShowInCal);

        //Arrow to rotate when user click on calendar icon
        final ImageView arrow = (ImageView) findViewById(R.id.arrow);
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

    @Override
    public void onPause() {
        super.onPause();
        //Stop location updates when Activity is no longer active
        if(locationUtil.getmGoogleApiClient() != null){
            if(locationUtil.getmGoogleApiClient().isConnected()){
                locationUtil.removeLocationUpdates();
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
        if (id == R.id.action_filtering) {

            final ArrayList seletedItems = new ArrayList();
            CharSequence[]allOrganization = myEventsManager.getAllOrganization().toArray(
                    new CharSequence[myEventsManager.getAllOrganization().size()]);

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select The Organization");
            builder.setMultiChoiceItems(allOrganization, null,
                    new DialogInterface.OnMultiChoiceClickListener() {
                        // indexSelected contains the index of item (of which checkbox checked)

                        @Override
                        public void onClick(DialogInterface dialog, int indexSelected,
                                            boolean isChecked) {
                            if (isChecked) {
                                // If the user checked the item, add it to the selected items
                                seletedItems.add(myEventsManager.getAllOrganization().get(indexSelected));
                            } else if (seletedItems.contains(myEventsManager.getAllOrganization().get(indexSelected))) {
                                // Else, if the item is already in the array, remove it
                                // write your code when user Uchecked the checkbox
                                seletedItems.remove(myEventsManager.getAllOrganization().get(indexSelected));
                            }
                        }
                    })
                    // Set the action buttons
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //  Your code when user clicked on OK
                            //  You can write the code  to save the selected item here
                            for (int i = 0; i< seletedItems.size();i++){
                                System.out.println(seletedItems.get(i) + "");
                            }
                            typeOfList = filtered;
                            myEventsManager.setChosenOrgnization(seletedItems);
                            adapter.setOnlyFavorite(typeOfList);
                            adapter.updateEventList();

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //  Your code when user clicked on Cancel
                            seletedItems.clear();
                        }
                    }).show();


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
            typeOfList = allEvents;
            adapter.setOnlyFavorite(typeOfList);
            if(locationUtil.getmGoogleApiClient() != null) {
                if (locationUtil.getmGoogleApiClient().isConnected()) {
                    locationUtil.loopCoordinates();
                }
            }
            adapter.updateEventList();

        } else if (id == R.id.nav_my_events) {
            toolbarTitle.setText(R.string.my_events);
            typeOfList = onlyFavorites;
            adapter.setOnlyFavorite(typeOfList);
            adapter.updateEventList();

        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(MainActivity.this,
                    ScreenManager.getInstance().getSettingsActivity()));
        } else if (id == R.id.nav_logout) {
            if (StorageManager.getInstance().getLoginType().equals("facebook")) {
                LoginManager.getInstance().logOut();
                startActivity(new Intent(MainActivity.this,
                        ScreenManager.getInstance().getLoginActivity()));
                finish();
            } else {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,
                        ScreenManager.getInstance().getLoginActivity()));
                finish();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setUpOnSettingsChangedListener() {
        System.out.println("Debögar: Nu sätter vi lyssnaren");
        listener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                        System.out.println("Debögar: Nu körs lyssnaren" + key);
                        if (key.equals(StorageManager.getInstance().getSettingsKey())) {
                            //Storage has changed
                            System.out.println("Debögar: Nu ändrar vi värdet");
                            //if firstDayOfWeek == 2 show monday as first
                            boolean showMondayFirst = StorageManager.getInstance().getSettings().
                                    get("firstDayOfWeek")==2;
                            mCompactCalendarView.setShouldShowMondayAsFirstDay(showMondayFirst);
                        }
                    }
                };
        StorageManager.getInstance().registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(locationUtil.getmGoogleApiClient() != null){
            if(locationUtil.getmGoogleApiClient().isConnected()){
                locationUtil.loopCoordinates();
            }else{
                locationUtil.getmGoogleApiClient().connect();
            }
        }
        adapter.updateEventList();
        toolbarTitle.setText(R.string.app_name);

        if (StorageManager.getInstance().getLoginType().equals("facebook") &&
                Profile.getCurrentProfile() == null) {
            startActivity(new Intent(MainActivity.this,
                    ScreenManager.getInstance().getLoginActivity()));
            finish();
        }
        if (typeOfList.equals(onlyFavorites)) {
            toolbarTitle.setText(R.string.my_events);
        } else {
            toolbarTitle.setText(R.string.app_name);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        //adapter.updateEventList();
        if (StorageManager.getInstance().getLoginType().equals("facebook") &&
                Profile.getCurrentProfile() == null) {
            startActivity(new Intent(MainActivity.this,
                    ScreenManager.getInstance().getLoginActivity()));
            finish();
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        adapter.updateEventList();
        if (StorageManager.getInstance().getLoginType().equals("facebook") &&
                Profile.getCurrentProfile() == null) {
            startActivity(new Intent(MainActivity.this,
                    ScreenManager.getInstance().getLoginActivity()));
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationUtil.getmGoogleApiClient() !=null){
            locationUtil.getmGoogleApiClient().disconnect();
        }
        if (profileTracker != null) {
            profileTracker.stopTracking();
        }
    }
}