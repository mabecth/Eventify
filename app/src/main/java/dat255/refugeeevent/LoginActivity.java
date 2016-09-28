package dat255.refugeeevent;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class LoginActivity extends AppCompatActivity {

    private TextView continueWithout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        // Facebook Analytics
        AppEventsLogger.activateApp(this);

        continueWithout = (TextView) findViewById(R.id.login_continue_without);
        continueWithout.setPaintFlags(continueWithout.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }
}
