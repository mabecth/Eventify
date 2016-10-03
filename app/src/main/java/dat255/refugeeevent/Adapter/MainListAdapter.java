package dat255.refugeeevent.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.facebook.login.widget.ProfilePictureView;
import java.util.HashMap;
import dat255.refugeeevent.R;
import dat255.refugeeevent.model.Event;

public class MainListAdapter extends BaseAdapter{

    private HashMap<Event, Integer> listOfEvents;
    private Event[] mKeys;
    private Event temp;
    private ProfilePictureView eventProfilePictureView;
    private TextView nameTextView, dateTextView, timeTextView,
            locationTextView, attendeesTextView;
    private View result;

    public MainListAdapter(){
        Event first = new Event();
        Event second = new Event();
        listOfEvents = new HashMap<>();

        first.setDate("29/10/2016");
        first.setNbrAttending(200);
        first.setPlace("Helvete");
        first.setTime("23:59");
        first.setTitle("First Event");

        second.setDate("14/11/2016");
        second.setNbrAttending(1111);
        second.setPlace("Heden");
        second.setTime("12:00");
        second.setTitle("Second Event");

        listOfEvents.put(first,1);
        listOfEvents.put(second,2);
        mKeys = listOfEvents.keySet().toArray(new Event[listOfEvents.size()]);
    }

    @Override
    public int getCount() {
        return listOfEvents.size();
    }

    @Override
    public Object getItem(int position) {
        return listOfEvents.get(mKeys[position]);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        temp = mKeys[position];

        if (view == null)
        {
            result = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.small_event, viewGroup, false);
        }
        else {
            result = view;
        }

        initializeView();

        nameTextView.setText(temp.getTitle());
        dateTextView.setText(temp.getDate());
        timeTextView.setText(temp.getTime());
        locationTextView.setText(temp.getPlace());
        attendeesTextView.setText(temp.getNbrAttending() + "");


        return result;
    }

    private void initializeView(){
        eventProfilePictureView = (ProfilePictureView) result.findViewById(R.id.eventProfilePictureView);
        nameTextView = (TextView) result.findViewById(R.id.nameTextView);
        dateTextView = (TextView) result.findViewById(R.id.dateTextView);
        timeTextView = (TextView) result.findViewById(R.id.timeTextView);
        locationTextView = (TextView) result.findViewById(R.id.locationTextView);
        attendeesTextView = (TextView) result.findViewById(R.id.attendeesTextView);
    }
}
