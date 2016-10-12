package dat255.refugeeevent.Adapter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.List;
import dat255.refugeeevent.DetailActivity;
import dat255.refugeeevent.R;
import dat255.refugeeevent.model.Event;
import dat255.refugeeevent.util.Storage;

public class MainListAdapter extends BaseAdapter{

    private List<Event> listOfEvents;
    private Event currEvent;
    private Event lastEvent;
    private ImageView eventProfilePictureView;
    private TextView nameTextView, dateTextView, timeTextView,
            locationTextView, attendeesTextView, distanceTextView,
            monthTextView;
    private ImageView coverImg;
    private View result;

    public MainListAdapter(){
        listOfEvents = Storage.getInstance().getEvents();

        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(Storage.getInstance().getEventsKey())) {
                    //Events changed
                    Log.v("MainListAdapter", "Events updated!");
                    updateEventList();
                }
            }
        };

        Storage.getInstance().registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public int getCount() {
        return listOfEvents.size();
    }

    @Override
    public Object getItem(int position) {
        return listOfEvents.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, final ViewGroup viewGroup) {

        currEvent = listOfEvents.get(position);

        if (view == null)
        {
            result = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.small_event, viewGroup, false);
        }
        else {
            result = view;
        }

        initializeView();

        nameTextView.setText(currEvent.getTitle());
        dateTextView.setText(currEvent.getDate().substring(currEvent.getDate().length()-2,currEvent.getDate().length()));
        monthTextView.setText(currEvent.getMonth());
        timeTextView.setText(currEvent.getTime());
        locationTextView.setText(currEvent.getPlace());
        attendeesTextView.setText(currEvent.getNbrAttending() + " people attending");
        distanceTextView.setText(currEvent.getDistance());


        Glide.with(viewGroup.getContext())
                .load(currEvent.getCover())
                .fitCenter()
                .centerCrop()
                .into(eventProfilePictureView);

        if (position > 0)
        {
            lastEvent = listOfEvents.get(position-1);
        }

        if (lastEvent != null && lastEvent.getDate().equals(currEvent.getDate()))
        {
            dateTextView.setVisibility(View.INVISIBLE);
            monthTextView.setVisibility(View.INVISIBLE);
        }
        else
        {
            dateTextView.setVisibility(View.VISIBLE);
            monthTextView.setVisibility(View.VISIBLE);
        }

        if (result!=null) {
            result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v("Click", "You clicked item with position: " + position);
                    Intent intent = new Intent(v.getContext(), DetailActivity.class);
                    intent.putExtra("EventIndex", position);
                    v.getContext().startActivity(intent);
                }
            });
        }

        lastEvent =null;
        return result;
    }

    public void updateEventList(){
        listOfEvents = Storage.getInstance().getEvents();
        notifyDataSetChanged();
        Log.v("Click","Event List Updated");
    }

    private void initializeView(){
        eventProfilePictureView = (ImageView) result.findViewById(R.id.eventProfilePictureView);
        nameTextView = (TextView) result.findViewById(R.id.nameTextView);
        dateTextView = (TextView) result.findViewById(R.id.dateTextView);
        timeTextView = (TextView) result.findViewById(R.id.timeTextView);
        locationTextView = (TextView) result.findViewById(R.id.locationTextView);
        attendeesTextView = (TextView) result.findViewById(R.id.attendeesTextView);
        distanceTextView = (TextView) result.findViewById(R.id.distanceTextView);
        monthTextView = (TextView) result.findViewById(R.id.monthTextView);
    }
}
