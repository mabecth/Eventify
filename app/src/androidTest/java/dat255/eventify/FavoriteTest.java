package dat255.eventify;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import dat255.eventify.activity.LoginActivity;
import dat255.eventify.manager.StorageManager;
import dat255.eventify.model.Event;

import static junit.framework.Assert.assertEquals;

/**
 * Created by alexsundback on 2016-10-21.
 */

@RunWith(AndroidJUnit4.class)
public class FavoriteTest {
    private List<Event> myTestEvents = new ArrayList<>();

    Event event1;
    Event event2;
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        Assert.assertEquals("dat255.eventify", appContext.getPackageName());
        event1 = new Event();
        event1.setId("1");
        event1.setTitle("Event Favorite");

        StorageManager.getInstance().setContext(appContext);
        StorageManager.getInstance().setChosenEvent(event1);
        StorageManager.getInstance().addToFavorite();

        assertEquals(true, StorageManager.getInstance().isFavorite());

        StorageManager.getInstance().addToFavorite();

        assertEquals(false, StorageManager.getInstance().isFavorite());

        event2 = new Event();
        event2.setId("2");
        event2.setTitle("Event Favorite 2");

        StorageManager.getInstance().setChosenEvent(event2);
        StorageManager.getInstance().addToFavorite();

        StorageManager.getInstance().storeFavorites();

        assertEquals("2", StorageManager.getInstance().getFavorites().get(0).getId());

    }
}
