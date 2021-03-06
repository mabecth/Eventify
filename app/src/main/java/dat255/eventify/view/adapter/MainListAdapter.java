package dat255.eventify.view.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import dat255.eventify.R;
import dat255.eventify.eventutil.MyEventsManger;
import dat255.eventify.model.Event;
import dat255.eventify.helper.SortByDate;

public class MainListAdapter extends BaseAdapter {

    private static final String TAG = "MainListAdapter";

    private List<Event> listOfEvents;
    private Event currEvent;
    private Event lastEvent;
    private ImageView eventProfilePictureView;
    private TextView nameTextView, dateTextView, timeTextView,
            locationTextView, attendeesTextView, distanceTextView,
            monthTextView, orgTextView;
    private View result;
    private String allEvents = "1";
    private String onlyFavorites = "2";
    private String filtered = "3";
    private String typeOfList = "1";
    private MyEventsManger manager;
    private boolean onlyDistance = false;


    public MainListAdapter() {
        manager = MyEventsManger.getInstance();
        listOfEvents = manager.getEvents();
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
            result = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.small_event,
                    viewGroup, false);
        } else {
            result = view;
        }

        initializeView();
        setViewData(viewGroup);

        if (position > 0) {
            lastEvent = listOfEvents.get(position - 1);
        } else {
            lastEvent = null;
        }

        //Only display date once if two or more events have the same date
        if (lastEvent != null && lastEvent.getDate().equals(currEvent.getDate())) {
            dateTextView.setVisibility(View.INVISIBLE);
            monthTextView.setVisibility(View.INVISIBLE);
        } else {
            dateTextView.setVisibility(View.VISIBLE);
            monthTextView.setVisibility(View.VISIBLE);
        }

        return result;
    }

    public void setChosenEvent(int position) {
        manager.setChosenEvent(listOfEvents.get(position));
    }

    public void updateEventList() {
        if (typeOfList.equals(onlyFavorites)) {
            listOfEvents = manager.getFavorites();
        } else if (typeOfList.equals(allEvents)) {
            listOfEvents = manager.getEvents();
        } else if (typeOfList.equals(filtered)) {
            listOfEvents = manager.getFilteredEvents();
        }

        SortByDate.sortDates(listOfEvents);
        notifyDataSetChanged();
        Log.d(TAG, "Event List Updated");
    }

    public void setOnlyFavorite(String typeOfList) {
        this.typeOfList = typeOfList;
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
        orgTextView = (TextView) result.findViewById(R.id.orgTextView);
        dateTextView.setVisibility(View.VISIBLE);
        monthTextView.setVisibility(View.VISIBLE);
    }

    private void setViewData(final ViewGroup viewGroup) {
        nameTextView.setText(currEvent.getTitle());
        dateTextView.setText(currEvent.getDate().substring(currEvent.getDate().length() - 2,
                currEvent.getDate().length()));
        monthTextView.setText(currEvent.getMonth());
        timeTextView.setText(currEvent.getTime());
        attendeesTextView.setText(currEvent.getNbrAttending());
        orgTextView.setText(currEvent.getOwner());
        if (!onlyDistance) {
            locationTextView.setText(currEvent.getPlace());
            distanceTextView.setText(currEvent.getDistance());
        } else {
            locationTextView.setText(currEvent.getDistance());
            distanceTextView.setText("");
        }

        //Display image
        Glide.with(viewGroup.getContext())
                .load(currEvent.getCover())
                .fitCenter()
                .centerCrop()
                .into(eventProfilePictureView);
    }

    public void setOnlyDistanceTrue() {
        onlyDistance = true;
    }

    public void setOnlyDistanceFalse() {
        onlyDistance = false;
    }
}
