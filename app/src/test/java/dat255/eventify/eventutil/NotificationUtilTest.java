package dat255.eventify.eventutil;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;

import dat255.eventify.Mock.MockedContext;
import dat255.eventify.manager.StorageManager;

import static org.junit.Assert.*;


public class NotificationUtilTest {

    /*
        Mocks the Saving and loading of storageManager and test switch statements for NotificationsUtil.
        Does not test or check for null because of input only showing hardcoed values for the user.
    */
    @Test
    public void calculateSettingsDelay() throws Exception {
        NotificationUtil notificationUtil = new NotificationUtil();
        HashMap<String, Integer> settingsMap = new HashMap<>();
        settingsMap.put("notifyDay", 0);
        settingsMap.put("notifyHour", 0);
        StorageManager storageManager = StorageManager.getInstance();
        storageManager.setContext(new MockedContext());
        storageManager.storeSettings(settingsMap);

        assertEquals(0,notificationUtil.calculateSettingsDelay());

        settingsMap.put("notifyDay", 1);
        settingsMap.put("notifyHour", 2);
        storageManager.storeSettings(settingsMap);

        assertEquals(100800000,notificationUtil.calculateSettingsDelay());

        settingsMap.put("notifyDay", 2);
        settingsMap.put("notifyHour", 1);
        storageManager.storeSettings(settingsMap);

        assertEquals(180000000,notificationUtil.calculateSettingsDelay());

        settingsMap.put("notifyDay", 3);
        settingsMap.put("notifyHour", 3);
        storageManager.storeSettings(settingsMap);

        assertEquals(280800000,notificationUtil.calculateSettingsDelay());

        settingsMap.put("notifyDay", 4);
        settingsMap.put("notifyHour", 4);
        storageManager.storeSettings(settingsMap);

        assertEquals(374400000,notificationUtil.calculateSettingsDelay());
    }

    @Test
    public void delayToNotification() throws Exception {
        NotificationUtil notificationUtil = new NotificationUtil();
        HashMap<String, Integer> settingsMap = new HashMap<>();
        settingsMap.put("notifyDay", 0);
        settingsMap.put("notifyHour", 0);
        StorageManager storageManager = StorageManager.getInstance();
        storageManager.setContext(new MockedContext());
        storageManager.storeSettings(settingsMap);
    }

}