package dat255.eventify.Mock;

import android.content.SharedPreferences;

import java.util.Set;

/*
    Mock class of Editor from SharedPreferences. Created to be able to use storageManager
     and save without relying on external parts.
 */

public class MockedEditor implements SharedPreferences.Editor {
    private static String defValue;
/*
    Saves a key for MockSharedPreferences to fetch for gson in StorageManager
 */
    @Override
    public SharedPreferences.Editor putString(String key, String value) {
        defValue=value;
        return null;
    }

    @Override
    public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
        return null;
    }

    @Override
    public SharedPreferences.Editor putInt(String key, int value) {
        return null;
    }

    @Override
    public SharedPreferences.Editor putLong(String key, long value) {
        return null;
    }

    @Override
    public SharedPreferences.Editor putFloat(String key, float value) {
        return null;
    }

    @Override
    public SharedPreferences.Editor putBoolean(String key, boolean value) {
        return null;
    }

    @Override
    public SharedPreferences.Editor remove(String key) {
        return null;
    }

    @Override
    public SharedPreferences.Editor clear() {
        return null;
    }

    @Override
    public boolean commit() {
        return true;
    }

    @Override
    public void apply() {

    }

    public static String getDefValue(){
        return defValue;
    }
}
