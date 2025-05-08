package sys.diag.car.api.contact;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CarsResponse {
    @SerializedName("carsModel")
    public List<CarResponse> cars;

    public CarsResponse(List<CarResponse> cars) {
        this.cars = cars;
    }
}
