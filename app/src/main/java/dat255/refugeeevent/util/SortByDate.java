package dat255.refugeeevent.util;

import java.util.Collections;
import java.util.List;

import dat255.refugeeevent.model.Event;

/**
 * Requirements:
 * - The date must be dd/mm/yyyy formatted
 * - The time must be 00:00 formatted
 */
public class SortByDate {

    public static List<Event> sortDates(List<Event> eventList) {
        int a, b, c, d, e, f, g, h, ix, j;

        // Sorting
        for (int i = 0; i < eventList.size() - 1; i++) {
            a = Integer.parseInt(eventList.get(i).getDate().split("-")[0]);
            b = Integer.parseInt(eventList.get(i+1).getDate().split("-")[0]);
            // Sorting years
            if (a > b) {
                Collections.swap(eventList, i, i+1);
                sortDates(eventList);
            } else if (a == b) {
                c = Integer.parseInt(eventList.get(i).getDate().split("-")[1]);
                d = Integer.parseInt(eventList.get(i+1).getDate().split("-")[1]);
                // Sorting months
                if (c > d) {
                    Collections.swap(eventList, i, i+1);
                    sortDates(eventList);
                } else if (c == d) {
                    e = Integer.parseInt(eventList.get(i).getDate().split("-")[2]);
                    f = Integer.parseInt(eventList.get(i+1).getDate().split("-")[2]);
                    // Sorting days
                    if (e > f) {
                        Collections.swap(eventList, i, i+1);
                        sortDates(eventList);
                    } else if (e == f) {
                        g = Integer.parseInt(eventList.get(i).getTime().split(":")[0]);
                        h = Integer.parseInt(eventList.get(i+1).getTime().split(":")[0]);
                        // Sorting hours
                        if (g > h) {
                            Collections.swap(eventList, i, i+1);
                            sortDates(eventList);
                        } else if (g == h) {
                            ix = Integer.parseInt(eventList.get(i).getTime().split(":")[1]);
                            j = Integer.parseInt(eventList.get(i+1).getTime().split(":")[1]);
                            // Sorting minutes
                            if (ix > j) {
                                Collections.swap(eventList, i, i+1);
                                sortDates(eventList);
                            }
                        }
                    }
                }
            }
        }
        return eventList;
    }
}