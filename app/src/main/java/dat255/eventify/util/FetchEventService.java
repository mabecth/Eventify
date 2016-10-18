package dat255.eventify.util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import dat255.eventify.R;
import dat255.eventify.manager.StorageManager;
import dat255.eventify.model.Event;

public class FetchEventService extends Service {

    private static final String TAG = "FetchEventService";
    private List<Event> events;
    private Event event;
    private int nbrOfOrganisations;
    private int dataCollectCycles;

    //Firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("userToken");

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        //events = StorageManager.getInstance().getEvents();
        events = new CopyOnWriteArrayList<>();
        final String[] organisations = FetchEventService.this.getResources().getStringArray(R.array.organisations);
        nbrOfOrganisations = organisations.length;
        dataCollectCycles = 0;
        Log.d(TAG, "Service started");

        if (StorageManager.getInstance().getLoginType().equals("facebook")) {
            for(String s : organisations) {
                getEventsFromFacebook(s, "facebook", null);
            }
        } else {
            //Read Facebook userToken from Firebase
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(String s : organisations) {
                        getEventsFromFacebook(s, "guest", dataSnapshot.getValue(String.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        }
    }

    public FetchEventService() {
        super();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void checkIfDone() {
        if (dataCollectCycles == nbrOfOrganisations) {
            SortByDate.sortDates(events);
            StorageManager.getInstance().storeEvents(events);
            Log.d(TAG, "Service done");
            stopSelf();
        }
    }

    private void getEventsFromFacebook(final String id, String loginType, String userToken) {
        //Get events from now to one month ahead
        long now = System.currentTimeMillis() / 1000L;
        long monthInSeconds = 2592000;
        long monthForward = now + monthInSeconds;
        int limit = 25;
        AccessToken accessToken;
        String graphPath;

        if (loginType.equals("facebook")) {
            accessToken = AccessToken.getCurrentAccessToken();
            graphPath = "/" + id + "/events?fields=id,name,description,attending_count,cover,owner,start_time,place&limit="+limit+"&since="+now+"&until="+monthForward+"";

        } else {
            accessToken = null;
            graphPath = "/" + id + "/events?fields=id,name,description,attending_count,cover,owner,start_time,place&limit="+limit+"&since="+now+"&until="+monthForward+"&access_token="+userToken+"";
        }

        new GraphRequest(
                accessToken,
                graphPath,
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        //Check if there are any upcoming events
                        if (response.getJSONObject() != null) {
                            try {
                                JSONArray jsonArray = response.getJSONObject().getJSONArray("data");
                                //Loop through events
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    event = new Event();
                                    try {
                                        JSONObject obj = jsonArray.getJSONObject(i);

                                        if (obj.has("id")) {
                                            event.setId(obj.getString("id"));
                                        }
                                        if (obj.has("name")) {
                                            event.setTitle(obj.getString("name"));
                                        }
                                        if (obj.has("description")) {
                                            event.setDesc(obj.getString("description"));
                                        }
                                        if (obj.has("attending_count")) {
                                            event.setNbrAttending(obj.getString("attending_count"));
                                        }
                                        if (obj.has("cover")) {
                                            event.setCover(obj.getJSONObject("cover").getString("source"));
                                        }
                                        if (obj.has("place")) {
                                            if (obj.getJSONObject("place").getJSONObject("location").has("street")) {
                                                event.setPlace(obj.getJSONObject("place").getJSONObject("location").getString("street"));
                                            }
                                            if (obj.getJSONObject("place").getJSONObject("location").has("latitude")) {
                                                event.setLatitude(obj.getJSONObject("place").getJSONObject("location").getString("latitude"));
                                            }
                                            if (obj.getJSONObject("place").getJSONObject("location").has("longitude")) {
                                                event.setLongitude(obj.getJSONObject("place").getJSONObject("location").getString("longitude"));
                                            }
                                        }
                                        if (obj.has("owner")) {
                                            event.setOwner(obj.getJSONObject("owner").getString("name"));
                                        }
                                        if (obj.has("start_time")) {
                                            event.setDate(obj.getString("start_time").substring(0, 10));
                                            event.setTime(obj.getString("start_time").substring(11, 16));
                                        }
                                           /* if(StorageManager.getInstance().getEvents() != null || StorageManager.getInstance().getEvents().size() != 0) {
                                                if(StorageManager.getInstance().getEvent(i) != null) {
                                                    if (StorageManager.getInstance().getEvent(i).getDistance() != null) {
                                                        event.setDistance(StorageManager.getInstance().getEvent(i).getDistance());
                                                    }
                                                }
*/

                                        events.add(event);
                                    } catch (JSONException e) {
                                        Log.e(TAG,"JSONException", e);
                                    }
                                }
                            } catch (JSONException e) {
                                Log.e(TAG,"JSONException", e);
                            }
                        }
                        dataCollectCycles++;
                        checkIfDone();
                    }
                }
        ).executeAsync();
    }
}
