package sys.diag.car.api.contact;

public class ResponseContact {
    private String code;
    private String details;


    public ResponseContact(String code, String details) {
        this.code = code;
        this.details = details;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getCode() {
        return code;
    }

    public String getDetails() {
        return details;
    }
}
