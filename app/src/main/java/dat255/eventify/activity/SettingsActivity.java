package dat255.eventify.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.HashMap;

import dat255.eventify.R;
import dat255.eventify.manager.StorageManager;

public class SettingsActivity extends AppCompatActivity {
    private String[] notifyDaysArray;
    private String[] notifyHoursArray;
    private String[] firstDayArray;

    private Switch distance;
    private Switch notifications;
    private Spinner notifyDay;
    private Spinner notifyHours;
    private Spinner firstDayCalendar;

    private HashMap<String, Integer> settingsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        settingsMap = StorageManager.getInstance().getSettings();

        initView();
    }

    public void initView() {
        //find views
        notifications = (Switch) findViewById(R.id.notiSwitch);
        distance = (Switch) findViewById(R.id.distanceSwitch);
        notifyDay = (Spinner) findViewById(R.id.spinnerDays);
        notifyHours = (Spinner) findViewById(R.id.spinnerHours);
        firstDayCalendar = (Spinner) findViewById(R.id.spinnerFirstDay);
        ImageButton backBtn = (ImageButton) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new BackOnClickListener());

        //Connect and init switches
        notifications.setChecked(settingsMap.get("notification") == 1);
        distance.setChecked(settingsMap.get("distance") == 1);
        notifications.setOnClickListener(new NotifyOnClickListener());
        distance.setOnClickListener(new DistanceOnClickListener());

        DropdownOnItemSelectedListener dropdownListener = new DropdownOnItemSelectedListener();

        //Connect and init spinners
        notifyDaysArray = new String[]{"0 days", "1 day", "2 days", "3 days", "4 days"};
        ArrayAdapter<String> adapterDays = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, notifyDaysArray);
        notifyDay.setAdapter(adapterDays);
        notifyDay.setSelection(settingsMap.get("notifyDay"));
        notifyDay.setOnItemSelectedListener(dropdownListener);

        notifyHoursArray = new String[]{"08:00", "10:00", "12:00", "14:00", "16:00"};
        ArrayAdapter<String> adapterHours = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, notifyHoursArray);
        notifyHours.setAdapter(adapterHours);
        notifyHours.setSelection(settingsMap.get("notifyHour"));
        notifyHours.setOnItemSelectedListener(dropdownListener);


        firstDayArray = new String[]{"Sunday", "Monday"};
        ArrayAdapter<String> adapterFirstDay = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, firstDayArray);
        firstDayCalendar.setAdapter(adapterFirstDay);
        firstDayCalendar.setSelection(settingsMap.get("firstDayOfWeek"));
        firstDayCalendar.setOnItemSelectedListener(dropdownListener);
    }

    class NotifyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            settingsMap.put("notification", notifications.isChecked() ? 1 : 0);
        }
    }

    class DistanceOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            settingsMap.put("distance", distance.isChecked() ? 1 : 0);
        }
    }

    class BackOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            finish();
        }
    }

    class DropdownOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            switch (adapterView.getId()) {
                case R.id.spinnerDays:
                    settingsMap.put("notifyDay", adapterView.getSelectedItemPosition());
                    break;
                case R.id.spinnerHours:
                    settingsMap.put("notifyHour", adapterView.getSelectedItemPosition());
                    break;
                case R.id.spinnerFirstDay:
                    settingsMap.put("firstDayOfWeek", adapterView.getSelectedItemPosition());
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        StorageManager.getInstance().storeSettings(settingsMap);
    }

}
