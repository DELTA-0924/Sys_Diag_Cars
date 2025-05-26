package sys.diag.car.dto;

public class idMapping {
    private Long temp_id;
    private Long new_id;

    public void setTemp_id(Long temp_id) {
        this.temp_id = temp_id;
    }

    public void setNew_id(Long new_id) {
        this.new_id = new_id;
    }

    public Long getTemp_id() {
        return temp_id;
    }

    public Long getNew_id() {
        return new_id;
    }

    public idMapping(Long temp_id, Long new_id) {
        this.temp_id = temp_id;
        this.new_id = new_id;
    }
}
