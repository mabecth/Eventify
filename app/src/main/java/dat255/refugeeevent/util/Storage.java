package dat255.refugeeevent.util;

import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import dat255.refugeeevent.model.Event;

/** Class used for storing data locally on the phone**/
public class Storage {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static Storage instance = new Storage();
    private static Gson gson = new Gson();

    private static String settingsKey = "1";
    private static String eventsKey = "2";
    private static String loginTypeKey = "3";

    private static String loginType;
    private static String facebook = "facebook";
    private static String guest = "guest";

    //Empty objects
    private static Object settings = new Object();
    private static List<Event> events = new CopyOnWriteArrayList<>();

    private Storage() {
    }

    public void setPreferences(SharedPreferences sp) {
        if (preferences == null) {
            preferences = sp;
            editor = preferences.edit();
            editor.commit();

            //Store empty lists
            storeEvents(events);
        }
    }

    public boolean isLoginTypeSet() {
        return preferences.getString(loginTypeKey, null) != null;
    }

    public String getLoginType() {
        return preferences.getString(loginTypeKey, null);
    }

    public void setLoginTypeFacebook() {
        editor.putString(loginTypeKey, facebook);
        editor.commit();
    }

    public void setLoginTypeGuest() {
        editor.putString(loginTypeKey, guest);
        editor.commit();
    }

    public String getSettingsKey() {
        return settingsKey;
    }

    public String getEventsKey() {
        return eventsKey;
    }

    public static Storage getInstance() {
        return instance;
    }

    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public void storeSettings(Object settings) {
        String settings_json = gson.toJson(settings);
        editor.putString(settingsKey, settings_json);
        editor.commit();
    }

    public Object getSettings() {
        String events_json = preferences.getString(settingsKey, "");
        if (gson.fromJson(events_json, new TypeToken<Object>(){}.getType()) == null) {
            return settings;
        } else {
            return gson.fromJson(events_json, new TypeToken<Object>(){}.getType());
        }
    }

    public void storeEvents(List<Event> events) {
        String events_json = gson.toJson(events);
        editor.putString(eventsKey, events_json);
        editor.commit();
    }

    public List<Event> getEvents() {
        String events_json = preferences.getString(eventsKey, "");
        if (gson.fromJson(events_json, new TypeToken<List<Event>>(){}.getType()) == null) {
            return events;
        } else {
            return gson.fromJson(events_json, new TypeToken<List<Event>>(){}.getType());
        }
    }

    public Event getEvent(int index) {
        return getEvents().get(index);
    }
}
