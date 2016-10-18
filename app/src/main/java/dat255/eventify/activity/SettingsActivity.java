package dat255.eventify.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Switch;

import dat255.eventify.R;

public class SettingsActivity extends AppCompatActivity {
    private String[] arraySpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();


        /*Spinner s = (Spinner) findViewById(R.id.firstdayspinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        s.setAdapter(adapter);*/
    }

    public void initView(){
        Switch notifications = (Switch) findViewById(R.id.notiSwitch);
        

    }

}
