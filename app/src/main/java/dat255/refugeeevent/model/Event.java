package dat255.refugeeevent.model;

/**
 * Created by Kristoffer on 2016-09-26.
 */

public class Event {
    private String title, place, date, time;
    private int nbrAttending;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlace() {
        return place;
    }
    public void setPlace(String place) {
        this.place = place;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public int getNbrAttending() {
        return nbrAttending;
    }
    public void setNbrAttending(int nbrAttending) {
        this.nbrAttending = nbrAttending;
    }
}
