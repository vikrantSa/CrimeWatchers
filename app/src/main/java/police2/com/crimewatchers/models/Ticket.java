package police2.com.crimewatchers.models;

/**
 * Created by Abhishek on 14-Jan-17.
 */

public class Ticket {

    String latitude;
    String longitude;
    String pusername;
    String status;
    String type;
    String userid;
    Integer confirm;
    String dateTime;


    public Ticket() {
    }


    public Ticket(String latitude, String longitude, String pusername, String status, String type, String userid, Integer confirm, String dateTime) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.pusername = pusername;
        this.status = status;
        this.type = type;
        this.userid = userid;
        this.confirm = confirm;
        this.dateTime = dateTime;
    }

    public Integer getConfirm() {
        return confirm;
    }

    public void setConfirm(Integer confirm) {
        this.confirm = confirm;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPusername() {
        return pusername;
    }

    public void setPusername(String pusername) {
        this.pusername = pusername;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
