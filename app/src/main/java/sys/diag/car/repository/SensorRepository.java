package sys.diag.car.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sys.diag.car.DB.DAO.SensorDAO;
import sys.diag.car.DB.Entity.SensorEntity;
import sys.diag.car.api.ApiService;
import sys.diag.car.api.contact.ResponseContact;
import sys.diag.car.api.contact.SensorRequest;
import sys.diag.car.dto.Result;
import sys.diag.car.dto.SensorDto;

public class SensorRepository {
    private final SensorDAO sensorDAO;
    private final Executor executor =  Executors.newSingleThreadExecutor();
    private final ApiService apiService;
    public SensorRepository(SensorDAO sensorDao,ApiService apiService){
        this.apiService = apiService;
        this.sensorDAO = sensorDao;
    }
    public LiveData<SensorEntity> getSensors(Long userId){
        return this.sensorDAO.getSensors(userId);
    }

    public void insertSensor(SensorDto sensorDto){
        SensorEntity sensorEntity = Optional.ofNullable(sensorDto)
                .map(this::dtoToEntity)
                .orElse(null);
        executor.execute(()->{
            this.sensorDAO.insert(sensorEntity);
        });
    }

    public void delete(SensorDto sensorDto){
        SensorEntity sensorEntity = Optional.ofNullable(sensorDto)
                .map(this::dtoToEntity)
                .orElse(null);
        executor.execute(()->{
            this.sensorDAO.delete(sensorEntity);
        });
    }

    public void update(SensorDto sensorDto){
        SensorEntity sensorEntity = Optional.ofNullable(sensorDto)
                .map(this::dtoToEntity)
                .orElse(null);
        executor.execute(()->{
            this.sensorDAO.update(sensorEntity);
        });
    }

    public LiveData<Result<SensorDto>> getSensors(long carId) {
        return Transformations.map(sensorDAO.getSensors(carId), sensorEntity ->
            Optional.ofNullable(sensorEntity)
                    .map(this::entityToDto)
                    .map(Result::success)
                    .orElse(Result.error("SENSOR_NOT_EXTRACT",null))
        );
    }

    public SensorEntity getSensorSync(Long carId){
        return this.sensorDAO.getSensorsSync(carId);
    }
     public void sendData(MutableLiveData<ResponseContact> responseContact,Long carId,String token){
         ExecutorService executor = Executors.newSingleThreadExecutor();
         Future<SensorEntity>future = executor.submit(()->this.getSensorSync(carId));
        executor.submit(()->{
            try{
                SensorEntity sensor = future.get();

                SensorRequest sensorRequest = Optional.ofNullable(sensor)
                        .map(this::entityToRequest)
                        .orElse(null);
                Call<ResponseContact> call =this.apiService.sendDataSensors("Token "+token,sensorRequest);
                call.enqueue(new Callback<ResponseContact>() {
                    @Override
                    public void onResponse(Call<ResponseContact> call, Response<ResponseContact> response) {
                        responseContact.postValue(response.body());
                    }

                    @Override
                    public void onFailure(Call<ResponseContact> call, Throwable throwable) {
                        responseContact.postValue(new ResponseContact("Ошибка сети",throwable.getMessage()));
                    }
                });

                } catch (Exception e) {
                     responseContact.postValue(new ResponseContact( "Ошибка при чтении БД: ",e.getMessage()));
                 } finally {
                     executor.shutdown();
                 }
        });

     }


    // DTO → Entity
    public SensorEntity dtoToEntity(SensorDto dto) {
        SensorEntity entity = new SensorEntity();
        entity.setOil_temp(dto.oilTemp);
        entity.setCool_temp(dto.coolTemp);
        entity.setRPM(dto.rpm);
        entity.setFuel_rate(dto.fuelRate);
        entity.setVoaltage(dto.voltage);
        entity.setMAF(dto.maf);
        entity.setIAT(dto.iat);
        entity.setMAP(dto.map);
        entity.setTPS(dto.tps);
        entity.setSpeed(dto.speed);
        entity.setCarId(dto.carId);
        return entity;
    }

    // Entity → DTO
    public SensorDto entityToDto(SensorEntity entity) {
        SensorDto dto = new SensorDto();
        dto.oilTemp = entity.getOil_temp();
        dto.coolTemp = entity.getCool_temp();
        dto.rpm = entity.getRPM();
        dto.fuelRate = entity.getFuel_rate();
        dto.voltage = entity.getVoaltage();
        dto.maf = entity.getMAF();
        dto.iat = entity.getIAT();
        dto.map = entity.getMAP();
        dto.tps = entity.getTPS();
        dto.speed = entity.getSpeed();
        dto.carId = entity.getCarId();
        return dto;
    }

    // Entity → Request (например, для отправки на сервер)
    public SensorRequest entityToRequest(SensorEntity entity) {
        SensorRequest request = new SensorRequest();
        request.oilTemp = entity.getOil_temp();
        request.coolTemp = entity.getCool_temp();
        request.rpm = entity.getRPM();
        request.fuelRate = entity.getFuel_rate();
        request.voltage = entity.getVoaltage();
        request.maf = entity.getMAF();
        request.iat = entity.getIAT();
        request.map = entity.getMAP();
        request.tps = entity.getTPS();
        request.speed = entity.getSpeed();
        return request;
    }

    // Пример сохранения нового сенсора
    public void saveSensor(SensorDto dto) {
        sensorDAO.insert(dtoToEntity(dto));
    }



}
