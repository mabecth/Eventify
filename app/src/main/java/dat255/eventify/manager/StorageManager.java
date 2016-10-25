package dat255.eventify.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import dat255.eventify.model.Event;
import dat255.eventify.util.Constants;

/**
 * Class used for storing data locally on the phone
 **/
public class StorageManager {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static StorageManager instance = new StorageManager();
    private static Gson gson = new Gson();

    private static String settingsKey = "1";
    private static String eventsKey = "2";
    private static String loginTypeKey = "3";
    private static String favoritesKey = "4";

    //Empty objects to avoid null
    private static HashMap<String, Integer> settings = new HashMap<>();
    private static List<Event> events = new CopyOnWriteArrayList<>();


    private StorageManager() {
    }

    private void setPreferences(Context context) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(Constants.PACKAGE_NAME,
                    Context.MODE_PRIVATE);
            editor = preferences.edit();
            editor.commit();
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

    public void registerOnSharedPreferenceChangeListener(
            SharedPreferences.OnSharedPreferenceChangeListener listener) {
        System.out.println("Nu sätter vi listener");
        listener.onSharedPreferenceChanged(preferences, "1");
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnSharedPreferenceChangeListener(
            SharedPreferences.OnSharedPreferenceChangeListener listener) {
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public void storeSettings(HashMap<String, Integer> settings) {
        String settings_json = gson.toJson(settings);
        editor.putString(settingsKey, settings_json);
        editor.commit();
    }

    public HashMap<String, Integer> getSettings() {
        String events_json = preferences.getString(settingsKey, "");
        if (gson.fromJson(events_json, new TypeToken<HashMap<String, Integer>>() {
        }.getType())
                == null) {
            settings.put("notification", 1);
            settings.put("distance", 0);
            settings.put("notifyDay", 0);
            settings.put("notifyHour", 0);
            settings.put("firstDayOfWeek", 1);
            return settings;
        } else {
            return gson.fromJson(events_json, new TypeToken<HashMap<String, Integer>>() {
            }.getType());
        }
    }

    public void storeFavorites(List<Event> myEvents) {
        String events_json = gson.toJson(myEvents);
        editor.putString(favoritesKey, events_json);
        editor.commit();
    }

    public List<Event> getFavorites() {
        String events_json = preferences.getString(favoritesKey, "");


        if (gson.fromJson(events_json, new TypeToken<List<Event>>() {
        }.getType()) == null) {
            return new ArrayList<>();
        } else {
            return gson.fromJson(events_json, new TypeToken<List<Event>>() {
            }.getType());
        }
    }

    public void storeEvents(List<Event> events) {
        String events_json = gson.toJson(events);
        editor.putString(eventsKey, events_json);
        editor.commit();
    }

    public void storeAddress(String address) {
        editor.putString("address", address);
        editor.commit();
    }

    public String getAddress() {
        String address = preferences.getString("address", "");
        return address;
    }

    public List<Event> getEvents() {
        String events_json = preferences.getString(eventsKey, "");
        if (gson.fromJson(events_json, new TypeToken<List<Event>>() {
        }.getType()) == null) {
            return events;
        } else {
            return gson.fromJson(events_json, new TypeToken<List<Event>>() {
            }.getType());
        }
    }

    public int getIndexForDate(Date dateToCheck) {
        int index = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-d", Locale.ENGLISH);
        String date = dateFormat.format(dateToCheck);
        int year = Integer.parseInt(date.split("-")[0]);
        int month = Integer.parseInt(date.split("-")[1]);
        int day = Integer.parseInt(date.split("-")[2]);
        for (Event e : getEvents()) {
            if (Integer.parseInt(e.getDate().split("-")[0]) >= year &&
                    Integer.parseInt(e.getDate().split("-")[1]) >= month &&
                    Integer.parseInt(e.getDate().split("-")[2]) >= day) {
                break;
            }
            index++;
        }
        return index;
    }
}
