package sys.diag.car.api.contact;

public class CarRequest {
    private String uid;
    private String user_uid;
    private String car_model;
    private String car_mark;
    private String car_year;
    private String IssueBroken;
    public CarRequest(String id,String user_uid, String car_model, String car_mark, String car_year,String issueBroken) {
        this.uid = id;
        this.user_uid = user_uid;
        this.car_model = car_model;
        this.car_mark = car_mark;
        this.car_year = car_year;
        this.IssueBroken = issueBroken;
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
