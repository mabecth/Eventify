package dat255.refugeeevent.util;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import dat255.refugeeevent.model.Event;

/** Class used for storing data locally on the phone**/
public class Storage extends Service {
    private static final String PREFS_NAME = "dat255.refugeeevent.Storage";
    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;
    public static Storage instance = new Storage();
    private static Gson gson;

    private String settingsKey = "1";
    private String eventsKey = "2";

    private Storage() {
        if(settings == null){
            settings = Storage.this.getSharedPreferences(PREFS_NAME,
                    Context.MODE_PRIVATE );
            gson = new Gson();
        }
       /*
        * Get a SharedPreferences editor instance.
        * SharedPreferences ensures that updates are atomic
        * and non-concurrent
        */
        editor = settings.edit();
        editor.commit();
    }

    public static Storage getInstance() {
        return instance;
    }

    public void storeSettings(Object settings) {
        String settings_json = gson.toJson(settings);
        editor.putString(settingsKey, settings_json);
        editor.commit();
    }

    public Object getSettings() {
        String events_json = settings.getString(settingsKey, "");
        return gson.fromJson(events_json, new TypeToken<Object>(){}.getType());
    }

    public void storeEvents(List<Event> events) {
        String events_json = gson.toJson(events);
        editor.putString(eventsKey, events_json);
        editor.commit();
    }

    public List<Event> getEvents() {
        String events_json = settings.getString(eventsKey, "");
        return gson.fromJson(events_json, new TypeToken<List<Event>>(){}.getType());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
