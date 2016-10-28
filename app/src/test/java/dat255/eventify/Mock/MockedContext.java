package dat255.eventify.mock;


import android.content.SharedPreferences;
import android.test.mock.MockContext;

/**
 * Created by Johannes on 2016-10-28.
 */

public class MockedContext extends MockContext {

    public SharedPreferences getSharedPreferences(String name, int mode){
        return new MockedSharedPreferences();
    }
}
