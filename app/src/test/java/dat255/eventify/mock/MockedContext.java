package dat255.eventify.mock;


import android.content.SharedPreferences;
import android.test.mock.MockContext;

/*
    Mocked class for Context to access storage.
 */

public class MockedContext extends MockContext {

    public SharedPreferences getSharedPreferences(String name, int mode){
        return new MockedSharedPreferences();
    }
}
