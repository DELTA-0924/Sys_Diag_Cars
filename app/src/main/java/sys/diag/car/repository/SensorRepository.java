package sys.diag.car.repository;

import static sys.diag.car.common.Utility.NO_PREDICTED;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.gson.Gson;

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
import sys.diag.car.api.contact.PredictionResponse;
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

     public void sendData(MutableLiveData<PredictionResponse> responseContact,Long carId){
         ExecutorService executor = Executors.newSingleThreadExecutor();
         Future<SensorEntity>future = executor.submit(()->this.getSensorSync(carId));
        executor.submit(()->{
            try{
                SensorEntity sensor = future.get();

                SensorRequest sensorRequest = Optional.ofNullable(sensor)
                        .map(this::entityToRequest)
                        .orElse(null);
                if (sensorRequest == null) {
                    PredictionResponse response = new PredictionResponse();
                    response.setDetail("Данные сенсора отсутствуют");
                    response.setStatus_code("400");
                    responseContact.postValue(response);
                    return;
                }
                Call<PredictionResponse> call =this.apiService.sendDataSensors(sensorRequest);
                call.enqueue(new Callback<PredictionResponse>() {
                    @Override
                    public void onResponse(Call<PredictionResponse> call, Response<PredictionResponse> response) {
                        if(response.isSuccessful())
                            responseContact.postValue(response.body());
                        else {
                            // пробуем считать errorBody
                            try {
                                String errorJson = response.errorBody().string();
                                Gson gson = new Gson();
                                PredictionResponse errorResponse = gson.fromJson(errorJson, PredictionResponse.class);
                                // теперь поля будут заполнены
                                responseContact.postValue(errorResponse);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<PredictionResponse> call, Throwable throwable) {
                        PredictionResponse response=  new PredictionResponse();
                                response.setDetail(throwable.getMessage());
                                throwable.printStackTrace();
                                response.setStatus_code("400");
                        responseContact.postValue(response);
                    }
                });

                } catch (Exception e) {
                    PredictionResponse response=  new PredictionResponse();
                    response.setDetail("Ошибка чтения из бд");
                    response.setStatus_code("404");
                    responseContact.postValue(response);
                 } finally {
                     executor.shutdown();
                 }
        });

     }


    // DTO → Entity
    public SensorEntity dtoToEntity(SensorDto dto) {
        SensorEntity entity = new SensorEntity();

        entity.setCool_temp(dto.coolTemp);
        entity.setRPM(dto.rpm);
        entity.setFuel_rate(dto.fuelRate);
        entity.setVoaltage(dto.voltage);
        entity.setMAF(dto.maf);
        entity.setIAT(dto.iat);

        entity.setTPS(dto.tps);
        entity.setFuelTrim(dto.fuelTrim);
        entity.setTimingAdvance(dto.timingAdvance);
        entity.setSpeed(dto.speed);
        entity.setCarId(dto.carId);
        return entity;
    }

    // Entity → DTO
    public SensorDto entityToDto(SensorEntity entity) {
        SensorDto dto = new SensorDto();

        dto.coolTemp = entity.getCool_temp();
        dto.rpm = entity.getRPM();
        dto.fuelRate = entity.getFuel_rate();
        dto.voltage = entity.getVoaltage();
        dto.maf = entity.getMAF();
        dto.iat = entity.getIAT();
        dto.tps = entity.getTPS();
        dto.speed = entity.getSpeed();
        dto.timingAdvance =entity.getTimingAdvance();
        dto.fuelTrim =entity.getFuelTrim();
        dto.carId = entity.getCarId();
        return dto;
    }

    // Entity → Request (например, для отправки на сервер)
    public SensorRequest entityToRequest(SensorEntity entity) {
        SensorRequest request = new SensorRequest();
        request.coolant_temp = entity.getCool_temp();
        request.rpm = entity.getRPM();
        request.fuel_consumption = entity.getFuel_rate();
        request.generator_voltage = entity.getVoaltage();
        request.maf = entity.getMAF();
        request.iat = entity.getIAT();
        request.tps = entity.getTPS();
        request.speed = entity.getSpeed();
        request.timing_advance = entity.getTimingAdvance();
        request.short_term_fuel_trim = entity.getFuelTrim();
        request.car_uid = String.valueOf( entity.getCarId());
        return request;
    }






}
