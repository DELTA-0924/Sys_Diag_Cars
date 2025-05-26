package sys.diag.car.api.contact;

import com.google.gson.annotations.SerializedName;

public class CarRequest {
    @SerializedName("temp_uid")
    private String uid;
    @SerializedName("uid")
    private Long server_uid;
    private String user_uid;
    private String car_model;
    private String car_mark;
    private String car_year;
    private String IssueBroken;
    private String car_image_path;

    public Long getServer_uid() {
        return server_uid;
    }

    public void setServer_uid(Long server_uid) {
        this.server_uid = server_uid;
    }

    public CarRequest(String id, String user_uid, String car_model, String car_mark, String car_year, String issueBroken, String car_image_path, Long server_uid) {
        this.uid = id;
        this.user_uid = user_uid;
        this.car_model = car_model;
        this.car_mark = car_mark;
        this.car_year = car_year;
        this.IssueBroken = issueBroken;
        this.car_image_path = car_image_path;
        this.server_uid = server_uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setIssueBroken(String issueBroken) {
        IssueBroken = issueBroken;
    }

    public void setCar_image_path(String car_image_path) {
        this.car_image_path = car_image_path;
    }

    public String getUid() {
        return uid;
    }

    public String getIssueBroken() {
        return IssueBroken;
    }

    public String getCar_image_path() {
        return car_image_path;
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


}
