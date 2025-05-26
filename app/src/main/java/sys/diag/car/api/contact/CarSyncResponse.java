package sys.diag.car.api.contact;

import java.util.List;

import sys.diag.car.dto.idMapping;

public class CarSyncResponse {
    private String status_code;
    private String detail;
    private List<idMapping> ids;

    public void setIds(List<idMapping> ids) {
        this.ids = ids;
    }

    public List<idMapping> getIds() {
        return ids;
    }

    public CarSyncResponse(){

    }

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
}
