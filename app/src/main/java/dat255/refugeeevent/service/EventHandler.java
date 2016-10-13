package dat255.refugeeevent.service;

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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import dat255.refugeeevent.R;
import dat255.refugeeevent.helpers.SortByDate;
import dat255.refugeeevent.model.Event;
import dat255.refugeeevent.util.Storage;

public class EventHandler extends Service {

    private static EventHandler ourInstance = new EventHandler();

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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void checkIfDone() {
        if (dataCollectCycles == nbrOfOrganisations) {
            SortByDate.sortDates(events);
            Storage.getInstance().storeEvents(events);
        }
    }

    public void getEventsFromFacebook(final String id) {
        //Get events from now to one month ahead
        long now = System.currentTimeMillis() / 1000L;
        long monthInSeconds = 2592000;
        long monthForward = now + monthInSeconds;
        int limit = 25;

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + id + "/events?fields=id,name,description,attending_count,cover,start_time,place&limit="+limit+"&since="+now+"&until="+monthForward+"",
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
                                        
                                        if (obj.getJSONObject("place").getJSONObject("location").has("street"))
                                            event.setPlace(obj.getJSONObject("place").getJSONObject("location").getString("street"));

                                        if (obj.has("start_time"))
                                            event.setDate(obj.getString("start_time").substring(0,10));
                                            event.setTime(obj.getString("start_time").substring(11,16));

                                        events.add(event);
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
        testEvent.setTitle("Football & hangout");
        testEvent.setPlace("Heden");
        testEvent.setTime("15:00");
        testEvent.setDate("17/08/2017");
        testEvent.setDesc("It was a crazy idea, really: one operating system that would run on a zillion different hardware combinations. Yet Windows turned out to be the foundation of Microsoft’s incredibly dominant empire.\n" +
                "\n" +
                "But there were problems with Windows from the beginning. It was mainly the responsibility of the hardware vendor to build a system that was Windows-compatible, but then there were updates, drivers, service packs, new versions … and decades later we’re still dealing with the compatibility mess InfoWorld’s Woody Leonhard reports on day after day.");
        testEvent.setNbrAttending(203);

        Event test2 = new Event();
        test2.setTitle("Come and meet us");
        test2.setPlace("Lindome");
        test2.setTime("18:00");
        test2.setDate("17/08/2017");
        test2.setDesc("Hi everyone!");
        test2.setNbrAttending(20);

        Event test3 = new Event();
        test3.setTitle("Game of thrones & chill");
        test3.setPlace("Lindome");
        test3.setTime("18:00");
        test3.setDate("18/08/2017");
        test3.setDesc("Hi everyone!");
        test3.setNbrAttending(1002);

        events.add(test2);
        events.add(testEvent);
        events.add(test3);

        //date must be formatted dd/mm/yyyy
        SortByDate.sortDates(events);
        // --------------
    }*/

    public static EventHandler getInstance() {
        return ourInstance;
    }
}
