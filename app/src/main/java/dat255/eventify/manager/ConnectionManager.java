package dat255.eventify.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/** Class used for analysing the internet connection on the users phone**/
public class ConnectionManager {

    private static ConnectivityManager connectivityManager;
    private static NetworkInfo activeNetwork;
    private static ConnectionManager instance = new ConnectionManager();

    private ConnectionManager() {
    }

    private void setConnectivityManager(Context context) {
        if (connectivityManager == null) {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            activeNetwork = connectivityManager.getActiveNetworkInfo();
        }
    }

    public static ConnectionManager getInstance() {
        return instance;
    }

    public void setContext(Context context) {
        setConnectivityManager(context);
    }

    public boolean isConnected() {
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public boolean isWifi() {
        return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public boolean isMobile() {
        return activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
    }
}
