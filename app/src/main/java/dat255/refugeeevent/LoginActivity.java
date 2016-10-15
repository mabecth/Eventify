package dat255.refugeeevent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import dat255.refugeeevent.util.Connection;
import dat255.refugeeevent.util.Storage;

public class LoginActivity extends AppCompatActivity {

    private final static String TAG = "LoginActivity";
    private CallbackManager callbackManager;
    private LoginButton loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        // Facebook Analytics
        AppEventsLogger.activateApp(this);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");

        Storage.getInstance().setContext(this);
        Connection.getInstance().setContext(this);

        //Check if we have logged in before
        if (Storage.getInstance().isLoginTypeSet()) {

            if (Storage.getInstance().getLoginType().equals("guest")) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            } else {
                if (Profile.getCurrentProfile() == null) {
                    initFacebookLogin();
                } else {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }
        } else {
            if (Profile.getCurrentProfile() == null) {
                initFacebookLogin();
            }
        }
    }

    private void initFacebookLogin() {
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                    Toast.makeText(LoginActivity.this, "Authentication successful",
                            Toast.LENGTH_SHORT).show();
                    if (!Storage.getInstance().isLoginTypeSet()) {
                        Storage.getInstance().setLoginTypeFacebook();
                    }
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                Toast.makeText(LoginActivity.this, "Authentication canceled",
                        Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                Toast.makeText(LoginActivity.this, "Authentication failed",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void continueAsGuest(View view) {
        if (!Storage.getInstance().isLoginTypeSet()) {
            Storage.getInstance().setLoginTypeGuest();
        }
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }
}
