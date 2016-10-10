package dat255.refugeeevent.model;

public class Event {
    private String title, place, date, time,desc, distance;
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

    public String getDistance() {
        return distance;
    }
    public void setDistance(String distance) {
        this.distance = distance;
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

    public String getMonth() {
        String month;
        switch (date.substring(3,5)) {
            case "08":
                month = "Aug";
                break;
            case "09":
                month = "Sep";
                break;
            default:
                month = "Shoot Long";
                break;
        }
        return month;
    }

    public int getNbrAttending() {
        return nbrAttending;
    }
    public void setNbrAttending(int nbrAttending) {
        this.nbrAttending = nbrAttending;
    }



    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
}
