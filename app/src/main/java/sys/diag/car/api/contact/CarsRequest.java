package sys.diag.car.api.contact;

import java.util.List;

public class CarsRequest {
 public  List<CarRequest>cars;

 public CarsRequest(List<CarRequest> cars) {
  this.cars = cars;
 }
}
