package sys.diag.car.api.contact;

public class PredictionResponse {

    private String status_code;
    private String detail;


    private String issue_broken;
    private String km_to_failure;
    public PredictionResponse() {}

    public String getStatus_code() {
        return status_code;
    }

    public String getDetail() {
        return detail;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setIssue_broken(String issue_broken) {
        this.issue_broken = issue_broken;
    }

    public void setKm_to_failure(String km_to_failure) {
        this.km_to_failure = km_to_failure;
    }

    public String getIssue_broken() {
        return issue_broken;
    }

    public String getKm_to_failure() {
        return km_to_failure;
    }
}
