package sys.diag.car.models;
public class Car {
    private long id;
    private String markCar;
    private String modelCar;
    private String yearRelease;
    private String issueBroken;
    private String imageUri;

    public Car(long id, String markCar, String modelCar, String yearRelease,String issueBroken,String imagePath) {
        this.id=id;
        this.markCar = markCar;
        this.modelCar = modelCar;
        this.yearRelease = yearRelease;
        this.issueBroken = issueBroken;
        this.imageUri = imagePath;
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