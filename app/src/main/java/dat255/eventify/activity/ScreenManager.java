package dat255.eventify.activity;

public class ScreenManager {

    private static ScreenManager instance = new ScreenManager();

    public static ScreenManager getInstance() {
        return instance;
    }

    public Class getMainActivity() {
        return ScreenType.getInstance().getMainActivity();
    }

    public Class getLoginActivity() {
        return ScreenType.getInstance().getLoginActivity();
    }

    public Class getSettingsActivity() {
        return ScreenType.getInstance().getSettingsActivity();
    }
}
