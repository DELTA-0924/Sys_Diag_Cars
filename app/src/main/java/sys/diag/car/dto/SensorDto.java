package sys.diag.car.dto;

public class SensorDto {
    public String coolTemp;
    public String rpm;
    public String fuelRate;
    public String voltage;
    public String maf;
    public String iat;
    public String tps;
    public String speed;
    public Long carId;
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

    public SensorDto(){}


    public void setCoolTemp(String coolTemp) {
        this.coolTemp = coolTemp;
    }

    public void setRpm(String rpm) {
        this.rpm = rpm;
    }

    public void setFuelRate(String fuelRate) {
        this.fuelRate = fuelRate;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public void setMaf(String maf) {
        this.maf = maf;
    }

    public void setIat(String iat) {
        this.iat = iat;
    }

    public void setTps(String tps) {
        this.tps = tps;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getCoolTemp() {
        return coolTemp;
    }

    public String getRpm() {
        return rpm;
    }

    public String getFuelRate() {
        return fuelRate;
    }

    public String getVoltage() {
        return voltage;
    }

    public String getMaf() {
        return maf;
    }

    public String getIat() {
        return iat;
    }

    public String getTps() {
        return tps;
    }

    public String getSpeed() {
        return speed;
    }

    public Long getCarId() {
        return carId;
    }

    public SensorDto(String coolTemp, String rpm, String fuelRate, String voltage, String maf, String iat, String tps, String speed,String fuelTrim, String timingAdvance,Long carId) {
        this.coolTemp = coolTemp;
        this.rpm = rpm;
        this.fuelRate = fuelRate;
        this.voltage = voltage;
        this.maf = maf;
        this.iat = iat;
        this.tps = tps;
        this.speed = speed;
        this.carId = carId;
        this.fuelTrim = fuelTrim;
        this.timingAdvance = timingAdvance;
    }
}

