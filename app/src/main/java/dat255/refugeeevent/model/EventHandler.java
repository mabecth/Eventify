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
    private int dataCollectCycles = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        events = new ConcurrentLinkedQueue<>();
        String[] organisations = EventHandler.this.getResources().getStringArray(R.array.organisations);
        nbrOfOrganisations = organisations.length;

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
            System.out.println("All done!");
        }
    }

    public void getEventsFromFacebook(final String id) {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + id + "/events",
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

                                        event.setId(obj.getString("id"));
                                        event.setTitle(obj.getString("name"));
                                        event.setDesc(obj.getString("description"));
                                        event.setNbrAttending(obj.getString("attending_count"));

                                        if (obj.has("cover")) {
                                            event.setCover(obj.getJSONObject("cover").getString("source"));
                                        }
                                        if (obj.has("place")) {
                                            event.setPlace(obj.getJSONObject("place").getJSONObject("location").getString("street"));
                                        }
                                        if (obj.has("start_time")) {
                                            event.setDate(obj.getString("start_time"));
                                        }

                                        events.offer(event); //Does not work
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

    /*public Event getEventAt(int index){
        return events.get(index);
    }*/

    public ConcurrentLinkedQueue<Event> getEvents(){
        return events;
    }
}
