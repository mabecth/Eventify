package dat255.refugeeevent.model;

import java.util.ArrayList;
import dat255.refugeeevent.helpers.SortByDate;

public class EventHandler {
    private static EventHandler ourInstance = new EventHandler();

    private ArrayList<Event> events;

    private EventHandler() {
        events = new ArrayList<>();

        //TEST ----------
        Event testEvent = new Event();
        testEvent.setTitle("Hangout @Heden");
        testEvent.setPlace("Heden");
        testEvent.setTime("15:00");
        testEvent.setDate("17/08/2017");
        testEvent.setDesc("Hi everyone!");
        testEvent.setNbrAttending(203);

        Event test2 = new Event();
        test2.setDate("17/08/2017");
        test2.setTime("15:30");

        events.add(test2);
        events.add(testEvent);

        //date must be formatted dd/mm/yyyy
        SortByDate.sortDates(events);
        // --------------
    }

    public static EventHandler getInstance() {
        return ourInstance;
    }

    public Event getEventAt(int index){
        return events.get(index);
    }

    public ArrayList<Event> getEvents(){
        return events;
    }
}
