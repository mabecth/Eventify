package dat255.eventify.Mock;

import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by Johannes on 2016-10-28.
 */

public class MockedEditor implements SharedPreferences.Editor {
    private static String defValue;

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
