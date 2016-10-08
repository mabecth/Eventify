package dat255.refugeeevent.model;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.ConcurrentLinkedQueue;
import dat255.refugeeevent.R;
import dat255.refugeeevent.helpers.SortByDate;

public class EventHandler extends Service {

    private static EventHandler ourInstance = new EventHandler();

    private ConcurrentLinkedQueue<Event> events;
    private Event event;
    private int nbrOfOrganisations;
    private int dataCollectCycles;

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        events = new ConcurrentLinkedQueue<>();
        String[] organisations = EventHandler.this.getResources().getStringArray(R.array.organisations);
        nbrOfOrganisations = organisations.length;
        dataCollectCycles = 0;

        for(String s : organisations) {
            getEventsFromFacebook(s);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void checkIfDone() {
        if (dataCollectCycles == nbrOfOrganisations) {
            //Send events to view
            System.out.println("All done!");
        }
    }

    public void getEventsFromFacebook(final String id) {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + id + "/events?fields=id,name,description,attending_count,cover,start_time,place",
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

                                        if (obj.has("id"))
                                            event.setId(obj.getString("id"));

                                        if (obj.has("name"))
                                            event.setTitle(obj.getString("name"));

                                        if (obj.has("description"))
                                            event.setDesc(obj.getString("description"));

                                        if (obj.has("attending_count"))
                                            event.setNbrAttending(obj.getString("attending_count"));

                                        if (obj.has("cover"))
                                            event.setCover(obj.getJSONObject("cover").getString("source"));

                                        if (obj.has("place"))
                                            event.setPlace(obj.getJSONObject("place").getJSONObject("location").getString("street"));

                                        if (obj.has("start_time"))
                                            event.setDate(obj.getString("start_time"));

                                        events.offer(event);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        dataCollectCycles++;
                        checkIfDone();
                    }
                }
        ).executeAsync();
    }


    /*private EventHandler() {
        events = new ArrayList<>();

        //TEST ----------
        Event testEvent = new Event();
        testEvent.setTitle("Hangout @Heden");
        testEvent.setPlace("Heden");
        testEvent.setTime("15:00");
        testEvent.setDate("17/08/2017");
        testEvent.setDesc("Hi everyone!");
        testEvent.setNbrAttending(203);

        Event test2 = new Event();
        test2.setDate("17/08/2017");
        test2.setTime("15:30");

        events.add(test2);
        events.add(testEvent);

        //date must be formatted dd/mm/yyyy
        SortByDate.sortDates(events);
        // --------------
    }*/

    public static EventHandler getInstance() {
        return ourInstance;
    }

    public Event getEventAtTop() {
        return events.poll();
    }

    public ConcurrentLinkedQueue<Event> getEvents(){
        return events;
    }
}
