package sys.diag.car.DB.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.util.TableInfo;

@Entity(tableName = "cars",
        foreignKeys = @ForeignKey(entity = UserEntity.class,
                                    parentColumns = "id",
                                    childColumns = "user_id",
                                    onUpdate = ForeignKey.CASCADE))
public class CarEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name="car_mark")
    private String markCar;
    @ColumnInfo(name="car_model")
    private String modelCar;
    @ColumnInfo(name="car_year")
    private String yearRelease;
    @ColumnInfo(name="car_issue_broken")
    private String issueBroken;
    @ColumnInfo(name="car_image_url")
    private String imageUri;

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    @ColumnInfo(name="user_id")
    private long userId;
    public CarEntity(long id, String markCar, String modelCar, String yearRelease, String issueBroken, String imageUri,long userId) {
        this.id = id;
        this.markCar = markCar;
        this.modelCar = modelCar;
        this.yearRelease = yearRelease;
        this.issueBroken = issueBroken;
        this.imageUri = imageUri;
        this.userId=userId;
    }


    public void setId(long id) {
        this.id = id;
    }

    public void setMarkCar(String markCar) {
        this.markCar = markCar;
    }

    public void setModelCar(String modelCar) {
        this.modelCar = modelCar;
    }

    public void setYearRelease(String yearRelease) {
        this.yearRelease = yearRelease;
    }

    public void setIssueBroken(String issueBroken) {
        this.issueBroken = issueBroken;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public long getId() {
        return id;
    }

    public String getMarkCar() {
        return markCar;
    }

    public String getModelCar() {
        return modelCar;
    }

    public String getYearRelease() {
        return yearRelease;
    }

    public String getIssueBroken() {
        return issueBroken;
    }

    public String getImageUri() {
        return imageUri;
    }


}
