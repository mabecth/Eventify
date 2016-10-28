package dat255.eventify.Mock;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.util.Map;
import java.util.Set;

/*
    Mock class of SharedPreferences. Created to be able to use storageManager
     and get saved without relying on external parts.
 */

public class MockedSharedPreferences implements SharedPreferences {
    public MockedSharedPreferences(){
    }

    @Override
    public Map<String, ?> getAll() {
        return null;
    }


    /*
    Returns defValue that will enable gson in storage to find the correct saved info.
     */
    @Nullable
    @Override
    public String getString(String key, String defValue) {
        return MockedEditor.getDefValue();
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return null;
    }

    @Override
    public int getInt(String key, int defValue) {
        return 0;
    }

    @Override
    public long getLong(String key, long defValue) {
        return 0;
    }

    @Override
    public float getFloat(String key, float defValue) {
        return 0;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return false;
    }

    @Override
    public boolean contains(String key) {
        return false;
    }

    @Override
    public Editor edit() {
        return new MockedEditor();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }
}
