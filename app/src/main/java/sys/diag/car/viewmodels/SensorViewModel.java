package sys.diag.car.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import sys.diag.car.DB.Entity.SensorEntity;
import sys.diag.car.api.contact.PredictionResponse;
import sys.diag.car.api.contact.ResponseContact;
import sys.diag.car.dto.Result;
import sys.diag.car.dto.SensorDto;
import sys.diag.car.repository.SensorRepository;

@HiltViewModel
public class SensorViewModel  extends ViewModel {
    private final SensorRepository  sensorRepository;
    private final MutableLiveData<PredictionResponse> sendSensorsResult = new MutableLiveData<>();
    @Inject
    public SensorViewModel(SensorRepository sensorRepository){
        this.sensorRepository = sensorRepository;
    }
    public LiveData<Result<SensorDto>>getLoadData(long carId){
        return this.sensorRepository.getSensors(carId);
    }
    public void loadSensotData(long carId){
        this.getLoadData(carId);
    }
    public void exctractSensors(SensorDto sensor){
        this.sensorRepository.insertSensor(sensor);
    }

    public void sendSensors(long carId){
        this.sensorRepository.sendData(sendSensorsResult,carId);
    }
    public  MutableLiveData<PredictionResponse> getSendSensorsResult(){
        return this.sendSensorsResult;
    }

}
