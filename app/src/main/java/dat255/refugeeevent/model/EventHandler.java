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
        testEvent.setTitle("Football & hangout");
        testEvent.setPlace("Heden");
        testEvent.setTime("15:00");
        testEvent.setDate("17/08/2017");
        testEvent.setDesc("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris justo ex, dictum non urna varius, faucibus aliquam nulla. Sed consequat molestie facilisis. Curabitur consequat odio quam, at semper dolor sagittis et. Donec facilisis diam ut purus pulvinar pretium. In aliquet dui mi, a maximus urna gravida nec. Ut tristique vel nisi ac consectetur. Ut in commodo orci. Sed rutrum in neque eget semper. Cras fringilla felis tortor, vitae tristique ex tincidunt a. Vestibulum tempor, diam eu rhoncus faucibus, urna leo euismod libero, vel rutrum sapien ligula at leo. Vestibulum volutpat molestie urna, in malesuada arcu porta a. Nulla tempor sodales ipsum. Nullam sed erat vel dolor porttitor semper. Nam ut ornare est, vitae interdum erat. Fusce nec malesuada leo.Sed tincidunt nulla quam, vel pharetra libero dictum vitae. Suspendisse vel maximus massa. Suspendisse sit amet est dolor. Proin commodo nibh in enim blandit, vel efficitur nibh porttitor. Etiam lorem augue, sodales nec lorem iaculis, porttitor scelerisque dui. Curabitur laoreet fermentum tortor non tempor. Quisque et congue lectus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Vivamus posuere ultricies metus, et condimentum nisi elementum a.\n" +
                "\n" +
                "Donec ac felis blandit tortor vehicula laoreet nec ac nisi. Vestibulum id maximus lorem. Nulla semper lectus at mauris consectetur vestibulum. Aliquam tempor, urna faucibus iaculis gravida, erat ligula laoreet elit, sit amet posuere nulla quam sed est. Etiam quis neque non velit rhoncus sagittis. Sed posuere vitae purus at bibendum. Nam placerat posuere nulla, nec feugiat leo viverra ut. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Maecenas sed quam non odio commodo rhoncus. Mauris commodo odio non nulla pharetra, vitae faucibus risus vehicula. Aliquam rutrum vestibulum mauris interdum ultricies. Aliquam blandit, orci non luctus consequat, dolor ipsum commodo libero, sit amet tempor nisi turpis mattis ante. Nullam nulla elit, egestas in facilisis vitae, venenatis id ipsum.");
        testEvent.setNbrAttending(203);

        Event test2 = new Event();
        test2.setTitle("Come and meet us");
        test2.setPlace("Lindome");
        test2.setTime("18:00");
        test2.setDate("17/08/2017");
        test2.setDesc("Hi everyone!");
        test2.setNbrAttending(20);

        Event test3 = new Event();
        test3.setTitle("Game of thrones & chill");
        test3.setPlace("Lindome");
        test3.setTime("18:00");
        test3.setDate("17/08/2017");
        test3.setDesc("Hi everyone!");
        test3.setNbrAttending(1002);

        events.add(test2);
        events.add(testEvent);
        events.add(test3);

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
