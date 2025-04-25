package sys.diag.car.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import sys.diag.car.dto.CarDto;
import sys.diag.car.repository.CarRepository;

@HiltViewModel
public class CarViewModel  extends ViewModel {
    private final CarRepository carRepository;
    private final LiveData<List<CarDto>> AllCars;
    @Inject
    public CarViewModel(CarRepository carRepository){
        this.carRepository=carRepository;
        this.AllCars =  this.carRepository.getAll();
    }
    public LiveData<List<CarDto>> getAllCars(){
        return this.AllCars;
    }
    public void CreateCar(CarDto carDto){
        this.carRepository.create(carDto);
    }
    public void update(CarDto carDto){
        this.carRepository.update(carDto);
    }
    public void delete(CarDto carDto){
        this.carRepository.delete(carDto);
    }

}
