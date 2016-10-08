package dat255.refugeeevent;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dat255.refugeeevent.model.Event;
import dat255.refugeeevent.model.EventHandler;

public class DetailActivity extends AppCompatActivity {

    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        int index = intent.getIntExtra("EventIndex", 0);
        event = EventHandler.getInstance().getEventAt(index);
        initView();


        ImageButton backBtn = (ImageButton) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new BackBtnOnClick());
        ImageButton showMapsBtn = (ImageButton) findViewById(R.id.showMapsBtn);
        showMapsBtn.setOnClickListener(new MapsBtnOnClick());
        ImageButton translateBtn = (ImageButton) findViewById(R.id.translateBtn);
        translateBtn.setOnClickListener(new TranslateBtnOnClick());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setStatusBarTranslucent(true);
        }
    }

    public void initView(){
        TextView title = (TextView) findViewById(R.id.titleText);
        TextView date = (TextView) findViewById(R.id.dateText);
        TextView time = (TextView) findViewById(R.id.timeText);
        TextView place = (TextView) findViewById(R.id.placeText);
        TextView nbrAttending = (TextView) findViewById(R.id.attendingText);
        TextView desc = (TextView) findViewById(R.id.descText);

        title.setText(event.getTitle());
        date.setText(event.getDate());
        time.setText(event.getTime());
        place.setText(event.getPlace());
        nbrAttending.setText(String.valueOf(event.getNbrAttending()));
        desc.setText(event.getDesc());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    //Listeners
    class BackBtnOnClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            finish(); //Goes back to the list view
        }
    }

    class MapsBtnOnClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            //Öppna google maps! Ruben får pilla med detta
        }
    }

    class TranslateBtnOnClick implements View.OnClickListener{

        List<String> languages;
        final ListPopupWindow mPopupWindow = new ListPopupWindow(getApplicationContext());

        TranslateBtnOnClick(){
            languages = new ArrayList<>();
            languages.add("Arabic");
            languages.add("Serbo-Croatian");
            languages.add("Kurdish");
            languages.add("Persian");
            languages.add("Somali");

            mPopupWindow.setAdapter(new ArrayAdapter<>(getApplicationContext(),R.layout.popup_list, languages));
            mPopupWindow.setWidth(200);
            mPopupWindow.setHeight(400);
            mPopupWindow.setModal(true);
        }

        @Override
        public void onClick(View view) {
            mPopupWindow.show();
        }
    }
}
