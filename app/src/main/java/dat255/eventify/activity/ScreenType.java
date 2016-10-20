package dat255.eventify.activity;

public class ScreenType {

    private static ScreenType instance = new ScreenType();

    public static ScreenType getInstance() {
        return instance;
    }

    public Class<MainActivity> getMainActivity() {
        return MainActivity.class;
    }

    public Class<LoginActivity> getLoginActivity() {
        return LoginActivity.class;
    }

    public Class<SettingsActivity> getSettingsActivity() {
        return SettingsActivity.class;
    }
}
