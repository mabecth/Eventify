package dat255.eventify.eventutil;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import dat255.eventify.mock.MockedContext;
import dat255.eventify.manager.StorageManager;

import static org.junit.Assert.*;


public class NotificationUtilTest {
    private NotificationUtil notificationUtil;
    private HashMap<String, Integer> settingsMap;
    private StorageManager storageManager;

    @Before
    public void setUp(){
        notificationUtil = new NotificationUtil();
        settingsMap = new HashMap<>();

        storageManager = StorageManager.getInstance();
        storageManager.setContext(new MockedContext());
    }

    /*
        Mocks the Saving and loading of storageManager and test switch statements for NotificationsUtil.
        Does not test or check for null because of input only showing hardcoded values for the user.
    */
    @Test
    public void calculateSettingsDelay() throws Exception {
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

        settingsMap.put("notifyDay", 0);
        settingsMap.put("notifyHour", 5);
        storageManager.storeSettings(settingsMap);

        assertEquals(36000000,notificationUtil.calculateSettingsDelay());

        settingsMap.put("notifyDay", 0);
        settingsMap.put("notifyHour", 6);
        storageManager.storeSettings(settingsMap);

        assertEquals(43200000,notificationUtil.calculateSettingsDelay());
    }

    /*
        Needs implementation
     */
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