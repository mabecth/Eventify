package dat255.eventify.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Event {

    private String id, title, place, owner, date, time, desc, nbrAttending, cover, distance,
            latitude, longitude;

    public double getLatitude() {
        if (latitude!=null){
            return Double.parseDouble(latitude);
        }
        return 0.0;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        if (longitude!=null){
            return Double.parseDouble(longitude);
        }
        return 0.0;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNbrAttending(String nbrAttending) {
        this.nbrAttending = nbrAttending;
    }

    public String getNbrAttending() {
        return nbrAttending;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

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

    public long getEventTimeInMillis(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        try {
            Date mDate = sdf.parse(getDate()+" " + getTime());
            return mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getMonth() {
        String month;
        switch (date.substring(5,7)) {
            case "01":
                month = "Jan";
                break;
            case "02":
                month = "Feb";
                break;
            case "03":
                month = "Mar";
                break;
            case "04":
                month = "Apr";
                break;
            case "05":
                month = "May";
                break;
            case "06":
                month = "Jun";
                break;
            case "07":
                month = "Jul";
                break;
            case "08":
                month = "Aug";
                break;
            case "09":
                month = "Sep";
                break;
            case "10":
                month = "Oct";
                break;
            case "11":
                month = "Nov";
                break;
            case "12":
                month = "Dec";
                break;
            default:
                month = "Other";
                break;
        }
        return month;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
