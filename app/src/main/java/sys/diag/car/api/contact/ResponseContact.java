package sys.diag.car.api.contact;

public class ResponseContact {
    private String status_code;
    private String detail;


    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getStatus_code() {
        return status_code;
    }

    public String getDetail() {
        return detail;
    }

    public ResponseContact(String status_code, String detail) {
        this.status_code = status_code;
        this.detail = detail;
    }
}
