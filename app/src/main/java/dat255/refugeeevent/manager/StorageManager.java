package dat255.refugeeevent.manager;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import dat255.refugeeevent.model.Event;
import dat255.refugeeevent.util.Constants;

/** Class used for storing data locally on the phone**/
public class StorageManager {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static StorageManager instance = new StorageManager();
    private static Gson gson = new Gson();

    private static String settingsKey = "1";
    private static String eventsKey = "2";
    private static String loginTypeKey = "3";

    //Empty objects
    private static Object settings = new Object();
    private static List<Event> events = new CopyOnWriteArrayList<>();

    private StorageManager() {
    }

    private void setPreferences(Context context) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
            editor = preferences.edit();
            editor.commit();

            //Store empty lists
            storeEvents(events);
        }
    }

    public void setContext(Context context) {
        setPreferences(context);
    }

    public boolean isLoginTypeSet() {
        return preferences.getString(loginTypeKey, null) != null;
    }

    public String getLoginType() {
        return preferences.getString(loginTypeKey, null);
    }

    public void setLoginTypeFacebook() {
        editor.putString(loginTypeKey, "facebook");
        editor.commit();
    }

    public void setLoginTypeGuest() {
        editor.putString(loginTypeKey, "guest");
        editor.commit();
    }

    public String getSettingsKey() {
        return settingsKey;
    }

    public String getEventsKey() {
        return eventsKey;
    }

    public static StorageManager getInstance() {
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
