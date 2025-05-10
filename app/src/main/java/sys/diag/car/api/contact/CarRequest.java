package sys.diag.car.api.contact;

public class CarRequest {
    private String uid;
    private String user_uid;
    private String car_model;
    private String car_mark;
    private String car_year;
    private String IssueBroken;
    private String car_image_path;

    public CarRequest(String id, String user_uid, String car_model, String car_mark, String car_year, String issueBroken, String car_image_path) {
        this.uid = id;
        this.user_uid = user_uid;
        this.car_model = car_model;
        this.car_mark = car_mark;
        this.car_year = car_year;
        this.IssueBroken = issueBroken;
        this.car_image_path = car_image_path;
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
