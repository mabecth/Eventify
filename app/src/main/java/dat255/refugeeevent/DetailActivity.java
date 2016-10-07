package dat255.refugeeevent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
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

    //Listeners
    class BackBtnOnClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            finish(); //Goes back to the list view
        }
    }
}
