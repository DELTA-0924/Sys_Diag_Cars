package sys.diag.car.api.contact;

import com.google.gson.annotations.SerializedName;

public class CarResponse {
    private String uid;
    private String user_uid;
    private String car_model;
    private String car_mark;
    private String car_year;
    private String issueBroken;
    @SerializedName("car_image_path")
    private String image_path;


    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getImage_path() {
        return image_path;
    }

    public CarResponse(String uid, String user_uid, String car_model, String car_mark, String car_year, String issueBroken, String image_path) {
        this.uid = uid;
        this.user_uid = user_uid;
        this.car_model = car_model;
        this.car_mark = car_mark;
        this.car_year = car_year;
        this.issueBroken = issueBroken;
        this.image_path = image_path;

    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public void setCar_model(String car_model) {
        this.car_model = car_model;
    }

    public void setCar_mark(String car_mark) {
        this.car_mark = car_mark;
    }

    public void setCar_year(String car_year) {
        this.car_year = car_year;
    }

    public void setIssueBroken(String issueBroken) {
        this.issueBroken = issueBroken;
    }



    public String getUid() {
        return uid;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public String getCar_model() {
        return car_model;
    }

    public String getCar_mark() {
        return car_mark;
    }

    public String getCar_year() {
        return car_year;
    }

    public String getIssueBroken() {
        return issueBroken;
    }



}
