package sys.diag.car.DB.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "sensors",
foreignKeys = @ForeignKey(entity = CarEntity.class,
                            parentColumns = "server_id",
                            childColumns = "car_id",
                            onUpdate = ForeignKey.CASCADE,
                            onDelete = ForeignKey.CASCADE
))
public class SensorEntity {


    @PrimaryKey(autoGenerate = true)
    private long id ;
    private String cool_temp;
    private String RPM;
    private String fuel_rate;
    private String voaltage;
    private String MAF;
    private String IAT;
    private String TPS;
    private String speed;
    public String timingAdvance;
    public String fuelTrim;

    public void setTimingAdvance(String timingAdvance) {
        this.timingAdvance = timingAdvance;
    }

    public void setFuelTrim(String fuelTrim) {
        this.fuelTrim = fuelTrim;
    }

    public String getTimingAdvance() {
        return timingAdvance;
    }

    public String getFuelTrim() {
        return fuelTrim;
    }

    @ColumnInfo(name = "car_id")
    private Long carId;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public SensorEntity(long id, String cool_temp, String RPM, String fuel_rate, String voaltage, String MAF, String IAT, String TPS, String speed,String fuelTrim,String timingAdvance, Long carId) {
        this.id = id;

        this.cool_temp = cool_temp;
        this.RPM = RPM;
        this.fuel_rate = fuel_rate;
        this.voaltage = voaltage;
        this.MAF = MAF;
        this.IAT = IAT;

        this.TPS = TPS;
        this.timingAdvance =timingAdvance;
        this.fuelTrim = fuelTrim;
        this.speed = speed;
        this.carId = carId;

    }

    @Ignore
    public SensorEntity(){}



    public void setCool_temp(String cool_temp) {
        this.cool_temp = cool_temp;
    }

    public void setRPM(String RPM) {
        this.RPM = RPM;
    }

    public void setFuel_rate(String fuel_rate) {
        this.fuel_rate = fuel_rate;
    }

    public void setVoaltage(String voaltage) {
        this.voaltage = voaltage;
    }

    public void setMAF(String MAF) {
        this.MAF = MAF;
    }

    public void setIAT(String IAT) {
        this.IAT = IAT;
    }


    public void setTPS(String TPS) {
        this.TPS = TPS;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }


    public String getCool_temp() {
        return cool_temp;
    }

    public String getRPM() {
        return RPM;
    }

    public String getFuel_rate() {
        return fuel_rate;
    }

    public String getVoaltage() {
        return voaltage;
    }

    public String getMAF() {
        return MAF;
    }

    public String getIAT() {
        return IAT;
    }


    public String getTPS() {
        return TPS;
    }

    public String getSpeed() {
        return speed;
    }

    public Long getCarId() {
        return carId;
    }


}
