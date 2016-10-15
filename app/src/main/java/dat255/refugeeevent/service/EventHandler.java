package dat255.refugeeevent.service;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import dat255.refugeeevent.R;
import dat255.refugeeevent.helpers.SortByDate;
import dat255.refugeeevent.model.Event;
import dat255.refugeeevent.util.Storage;

public class EventHandler extends Service {

    private List<Event> events;
    private Event event;
    private int nbrOfOrganisations;
    private int dataCollectCycles;

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        events = new CopyOnWriteArrayList<>();
        String[] organisations = EventHandler.this.getResources().getStringArray(R.array.organisations);
        nbrOfOrganisations = organisations.length;
        dataCollectCycles = 0;

        for(String s : organisations) {
            getEventsFromFacebook(s);
        }
    }

    private EventHandler() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void checkIfDone() {
        if (dataCollectCycles == nbrOfOrganisations) {
            SortByDate.sortDates(events);
            Storage.getInstance().storeEvents(events);
            Log.d("EventHandler", "Service done");
        }
    }

    private void getEventsFromFacebook(final String id) {
        //Get events from now to one month ahead
        long now = System.currentTimeMillis() / 1000L;
        long monthInSeconds = 2592000;
        long monthForward = now + monthInSeconds;
        int limit = 25;

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + id + "/events?fields=id,name,description,attending_count,cover,owner,start_time,place&limit="+limit+"&since="+now+"&until="+monthForward+"",
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
                                        if (obj.getJSONObject("place").getJSONObject("location").has("street")) {
                                            event.setPlace(obj.getJSONObject("place").getJSONObject("location").getString("street"));
                                        }
                                        if (obj.has("owner")) {
                                            event.setOwner(obj.getJSONObject("owner").getString("name"));
                                        }
                                        if (obj.has("start_time")) {
                                            event.setDate(obj.getString("start_time").substring(0, 10));
                                            event.setTime(obj.getString("start_time").substring(11, 16));
                                        }
                                        events.add(event);
                                    } catch (JSONException e) {
                                        Log.e("EventHandler","JSONException", e);
                                    }

                                }
                            } catch (JSONException e) {
                                Log.e("EventHandler","JSONException", e);
                            }
                        }
                        dataCollectCycles++;
                        checkIfDone();
                    }
                }
        ).executeAsync();
    }
}
