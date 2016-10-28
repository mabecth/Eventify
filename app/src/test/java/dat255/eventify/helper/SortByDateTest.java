package dat255.eventify.helper;

import android.util.Log;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import dat255.eventify.model.Event;
import dat255.eventify.helper.SortByDate;

import static junit.framework.Assert.assertEquals;

public class SortByDateTest {

    Event event1;
    Event event2;
    Event event3;
    Event event4;
    Event event5;
    Event event6;
    Event event7;

    List<Event> fullList;

    public SortByDateTest() throws Exception {
        sortByDateTest1();
        sortByDateTest2();
    }

    public void initList() {
        fullList = new ArrayList<>();

        event1 = new Event();
        event1.setDate("2016-10-20");
        event1.setTime("10:00");
        fullList.add(event1);

        event2 = new Event();
        event2.setDate("2016-10-14");
        event2.setTime("13:00");
        fullList.add(event2);

        event3 = new Event();
        event3.setDate("2016-10-14");
        event3.setTime("11:30");
        fullList.add(event3);

        event4 = new Event();
        event4.setDate("2016-10-14");
        event4.setTime("11:29");
        fullList.add(event4);

        event5 = new Event();
        event5.setDate("2015-10-14");
        event5.setTime("11:28");
        fullList.add(event5);

        event6 = new Event();
        event6.setDate("1996-04-06");
        event6.setTime("17:36");
        fullList.add(event6);

    }

    @Test
    public void sortByDateTest1() throws Exception {
        initList();
        SortByDate.sortDates(fullList);

        assertEquals("1996-04-06 17:36", fullList.get(0).getDate() + " " + fullList.get(0).getTime());
        assertEquals("2015-10-14 11:28", fullList.get(1).getDate() + " " + fullList.get(1).getTime());
        assertEquals("2016-10-14 11:29", fullList.get(2).getDate() + " " + fullList.get(2).getTime());
        assertEquals("2016-10-14 11:30", fullList.get(3).getDate() + " " + fullList.get(3).getTime());
        assertEquals("2016-10-14 13:00", fullList.get(4).getDate() + " " + fullList.get(4).getTime());
        assertEquals("2016-10-20 10:00", fullList.get(5).getDate() + " " + fullList.get(5).getTime());
    }

    @Test
    public void sortByDateTest2() throws Exception {
        initList();

        List<Event> list1 = new ArrayList<>();

        list1.add(event2);
        list1.add(event6);

        SortByDate.sortDates(list1);

        assertEquals("1996-04-06 17:36", list1.get(0).getDate() + " " + list1.get(0).getTime());
        assertEquals("2016-10-14 13:00", list1.get(1).getDate() + " " + list1.get(1).getTime());
    }

    @Test
    public void sortByDateTest3() throws Exception {
        initList();

        List<Event> list2 = new ArrayList<>();

        list2.add(event3);
        list2.add(event4);

        SortByDate.sortDates(list2);

        assertEquals("2016-10-14 11:29", list2.get(0).getDate() + " " + list2.get(0).getTime());
        assertEquals("2016-10-14 11:30", list2.get(1).getDate() + " " + list2.get(1).getTime());
    }

}
