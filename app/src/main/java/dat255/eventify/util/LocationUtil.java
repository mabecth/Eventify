package dat255.eventify.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.List;

import dat255.eventify.manager.ConnectionManager;
import dat255.eventify.manager.StorageManager;
import dat255.eventify.R;
import dat255.eventify.model.Event;

public class LocationUtil extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    protected static final String TAG = "GoogleApi";

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LatLng latLng;
    private List<Event> listOfEvents;
    private LocationRequest mLocationRequest;
    private static List<Event> updatedList;
    protected boolean mAddressRequested;
    protected String mAddressOutput;
    private AddressResultReceiver mResultReceiver;

    private MyActivityListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (MyActivityListener) context;
        } catch (ClassCastException castException) {
        }
    }

    public LocationUtil() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mAddressRequested = false;
        mAddressOutput = "";
        if(listOfEvents == null) {
            listOfEvents = StorageManager.getInstance().getEvents();
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public synchronized void loopCoordinates(){
        listOfEvents = StorageManager.getInstance().getEvents();
        updatedList = StorageManager.getInstance().getEvents();
        if(latLng ==null){
        }else{
            if (listOfEvents.size() > 0) {
                for (int index = 0; index < listOfEvents.size(); index++) {
                    LatLng endLatLng = new LatLng(StorageManager.getInstance().getEvents().get(index).getLatitude(),StorageManager.getInstance().getEvents().get(index).getLongitude());
                    CalculationByDistance(latLng, endLatLng, index);
                }
            }
        }
    }


    public synchronized void CalculationByDistance(LatLng StartP, LatLng EndP, int index) {
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
        System.out.println("Distance: "+ Radius * c);
        updateDistance(index, round(Radius * c,1) + " km");
    }

    public synchronized void updateDistance(int id, String result) {
        updatedList.get(id).setDistance(result);
        StorageManager.getInstance().storeEvents(updatedList);
    }

    public synchronized void buildGoogleApi(){
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        try{
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }catch (java.lang.IllegalStateException e){
            Log.e(TAG,"GoogleApiClient is not connected");
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
        Toast.makeText(getActivity(), "Could not connect to Play Services", Toast.LENGTH_SHORT).show();
    }

    public void sendAddressResultToMain() {
        if(ConnectionManager.getInstance().isConnected()){
            listener.displayAddress(mAddressOutput);
        }else if(!ConnectionManager.getInstance().isConnected()){
            if (StorageManager.getInstance().getAddress() != null) {
                listener.displayAddress("Recent: " + StorageManager.getInstance().getAddress());
            }
        }

        Log.d(TAG, mAddressOutput);
    }

    protected void startIntentService() {
        if (ConnectionManager.getInstance().isConnected()) {
            // Create an intent for passing to the intent service responsible for fetching the address.
            Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
            // Pass the result receiver as an extra to the service.
            intent.putExtra(Constants.RECEIVER, mResultReceiver);

            // Pass the location data as an extra to the service.
            intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
            // Start the service. If the service isn't already running, it is instantiated and started
            // (creating a process for it if needed); if it is running then it remains running. The
            // service kills itself automatically once all intents are processed.
            getActivity().startService(intent);

        }else{
            sendAddressResultToMain();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        //Get coordinates
        latLng = new LatLng(location.getLatitude(), location.getLongitude());

        mResultReceiver = new AddressResultReceiver(new Handler());
        loopCoordinates();
        startIntentService();
        listener.updateAdapter();

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
            if(ConnectionManager.getInstance().isConnected()) {
                StorageManager.getInstance().storeAddress(mAddressOutput);
            }
            sendAddressResultToMain();
        }
    }
}