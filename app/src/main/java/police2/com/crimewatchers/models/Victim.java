package police2.com.crimewatchers.models;

/**
 * Created by Abhishek on 16-Jan-17.
 */

public class Victim {

    public String name;
    public String email;
    public String photoUrl;
    public String dob;
    public String mobile;
    public String gender;


    public Victim() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Victim(String name, String email, String photoUrl, String dob, String mobile, String gender) {
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
        this.dob = dob;
        this.mobile = mobile;
        this.gender = gender;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

}
