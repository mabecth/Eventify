package dat255.refugeeevent.view.adapter;

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
import dat255.refugeeevent.activity.DetailActivity;
import dat255.refugeeevent.R;
import dat255.refugeeevent.model.Event;
import dat255.refugeeevent.manager.StorageManager;

public class MainListAdapter extends BaseAdapter{

    private static final String TAG = "MainListAdapter";
    private List<Event> listOfEvents;
    private Event currEvent;
    private Event lastEvent;
    private ImageView eventProfilePictureView;
    private TextView nameTextView, dateTextView, timeTextView,
            locationTextView, attendeesTextView, distanceTextView,
            monthTextView;
    private View result;

    public MainListAdapter(){
        listOfEvents = StorageManager.getInstance().getEvents();

        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(StorageManager.getInstance().getEventsKey())) {
                    //Events changed
                    Log.d(TAG, "Events in storage changed!");
                    updateEventList();
                }
            }
        };
        notifyDataSetChanged();
        StorageManager.getInstance().registerOnSharedPreferenceChangeListener(listener);
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

        if (view == null) {
            result = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.small_event, viewGroup, false);
        } else {
            result = view;
        }

        initializeView();
        setViewData(viewGroup);

        if (position > 0) {
            lastEvent = listOfEvents.get(position - 1);
        }

        //Only display date once if two or more events have the same date
        if (lastEvent != null && lastEvent.getDate().equals(currEvent.getDate())) {
            dateTextView.setVisibility(View.INVISIBLE);
            monthTextView.setVisibility(View.INVISIBLE);
        } else {
            dateTextView.setVisibility(View.VISIBLE);
            monthTextView.setVisibility(View.VISIBLE);
        }

        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "You clicked item with position: " + position);
                Intent intent = new Intent(v.getContext(), DetailActivity.class);
                intent.putExtra("EventIndex", position);
                v.getContext().startActivity(intent);
            }
        });

        lastEvent = null;
        return result;
    }

    public void updateEventList() {
        listOfEvents = StorageManager.getInstance().getEvents();
        notifyDataSetChanged();
        Log.d(TAG,"Event List Updated");
    }

    private void initializeView() {
        eventProfilePictureView = (ImageView) result.findViewById(R.id.eventProfilePictureView);
        nameTextView = (TextView) result.findViewById(R.id.nameTextView);
        dateTextView = (TextView) result.findViewById(R.id.dateTextView);
        timeTextView = (TextView) result.findViewById(R.id.timeTextView);
        locationTextView = (TextView) result.findViewById(R.id.locationTextView);
        attendeesTextView = (TextView) result.findViewById(R.id.attendeesTextView);
        distanceTextView = (TextView) result.findViewById(R.id.distanceTextView);
        monthTextView = (TextView) result.findViewById(R.id.monthTextView);
    }

    private void setViewData(final ViewGroup viewGroup) {
        nameTextView.setText(currEvent.getTitle());
        dateTextView.setText(currEvent.getDate().substring(currEvent.getDate().length()-2, currEvent.getDate().length()));
        monthTextView.setText(currEvent.getMonth());
        timeTextView.setText(currEvent.getTime());
        locationTextView.setText(currEvent.getPlace());
        attendeesTextView.setText(currEvent.getNbrAttending() + " people attending");
        distanceTextView.setText(currEvent.getDistance());

        //Display image
        Glide.with(viewGroup.getContext())
                .load(currEvent.getCover())
                .fitCenter()
                .centerCrop()
                .into(eventProfilePictureView);
    }
}
