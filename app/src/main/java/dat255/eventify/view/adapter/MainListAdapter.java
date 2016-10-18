package dat255.eventify.view.adapter;

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
import java.util.Random;

import dat255.eventify.activity.DetailActivity;
import dat255.eventify.R;
import dat255.eventify.model.Event;
import dat255.eventify.manager.StorageManager;

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
    private static boolean onlyFavorite = false;

    public MainListAdapter(){
        listOfEvents = StorageManager.getInstance().getEvents();

        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(StorageManager.getInstance().getEventsKey())) {
                    //Events changed
                    Log.d(TAG, "Events in storage changed!");
                    updateEventList(onlyFavorite);
                }
            }
        };

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
        else lastEvent = null;

        //Only display date once if two or more events have the same date
        if (lastEvent != null && lastEvent.getDate().equals(currEvent.getDate())) {
            dateTextView.setVisibility(View.INVISIBLE);
            monthTextView.setVisibility(View.INVISIBLE);
        }
        else {
            dateTextView.setVisibility(View.VISIBLE);
            monthTextView.setVisibility(View.VISIBLE);
        }

        return result;
    }

    public void setChosenEvent(int position)
    {
        StorageManager.getInstance().setChosenEvent(listOfEvents.get(position));
    }

    public void updateEventList(boolean onlyFavorite) {
        this.onlyFavorite = onlyFavorite;

        if (onlyFavorite == true)
        {
            listOfEvents = StorageManager.getInstance().getFavorites();
        }
        else listOfEvents = StorageManager.getInstance().getEvents();

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
        dateTextView.setVisibility(View.VISIBLE);
        monthTextView.setVisibility(View.VISIBLE);
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
