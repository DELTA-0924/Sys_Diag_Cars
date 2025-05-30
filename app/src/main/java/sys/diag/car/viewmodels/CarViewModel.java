package sys.diag.car.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import sys.diag.car.api.contact.CarSyncResponse;
import sys.diag.car.api.contact.ResponseContact;
import sys.diag.car.dto.CarDto;
import sys.diag.car.dto.Result;
import sys.diag.car.dto.idMapping;
import sys.diag.car.repository.CarRepository;

@HiltViewModel
public class CarViewModel  extends ViewModel {
    private final CarRepository carRepository;
    private final LiveData<List<CarDto>> AllCars;
    private final MutableLiveData<Result<ResponseContact>> responseLoadData =  new MutableLiveData<>();
    private final MutableLiveData<Result<CarSyncResponse>> responseSynchronizeData =  new MutableLiveData<>();
    private final MutableLiveData<Result<idMapping>> responseSendCar =  new MutableLiveData<>();
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
    public void updateCar(CarDto carDto){
        this.carRepository.update(carDto);
    }
    public void deleteCar(CarDto carDto){
        this.carRepository.delete(carDto);
    }
    public void deleteAllCars(){
        this.carRepository.deleteAllCars();
    }

    public LiveData<Result<ResponseContact>> getLoadData(){
        return responseLoadData;
    }

    public LiveData<Result<CarSyncResponse>>getSynchronizeData(){
        return responseSynchronizeData;
    }

    public void loadData(String token, File fileDir){
        this.carRepository.loadData(token,responseLoadData,fileDir);
    }

    public void synchronizeData(String token,File internalDir){
        this.carRepository.syncData(token,responseSynchronizeData,internalDir);
    }

    public LiveData<CarDto> getByIdCar(long id){
        return this.carRepository.getById(id);
    }

    public MutableLiveData<Result<idMapping>> getCarToSendSensors(){
        return responseSendCar;
    }

    public void sendCar(CarDto carDto){
        this.carRepository.sendCarToSendSensors(responseSendCar,carDto);
    }
}
