package sys.diag.car.dto;

import java.io.Serializable;

public class CarDto implements Serializable {
    private long id;
    private String markCar;
    private String modelCar;
    private String yearRelease;
    private String issueBroken;
    private String imageUri;
    private long userId;
    private boolean car_synchronized ;

    public void setCar_synchronized(boolean car_synchronized) {
        this.car_synchronized = car_synchronized;
    }

    public boolean isCar_synchronized() {
        return car_synchronized;
    }

    public void setServer_id(Long server_id) {
        this.server_id = server_id;
    }

    public Long getServer_id() {
        return server_id;
    }

    public Long server_id;

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public CarDto(long id, String markCar, String modelCar, String yearRelease, String issueBroken, String imagePath, long userId,boolean car_synchronized) {
        this.id=id;
        this.markCar = markCar;
        this.modelCar = modelCar;
        this.yearRelease = yearRelease;
        this.issueBroken = issueBroken;
        this.imageUri = imagePath;

        this.car_synchronized = car_synchronized;
        this.userId=userId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMarkCar() {
        return markCar;
    }

    public void setMarkCar(String markCar) {
        this.markCar = markCar;
    }

    public String getModelCar() {
        return modelCar;
    }

    public void setModelCar(String modelCar) {
        this.modelCar = modelCar;
    }

    public String getYearRelease() {
        return yearRelease;
    }

    public void setYearRelease(String yearRelease) {
        this.yearRelease = yearRelease;
    }

    public String getIssueBroken() {
        return issueBroken;
    }

    public void setIssueBroken(String issueBroken) {
        this.issueBroken = issueBroken;
    }
    public void setImageUri(String path){
        this.imageUri=path;
    }
    public String getImageUri(){
        return this.imageUri;
    }
}