package sys.diag.car.repository;

import static sys.diag.car.common.Utility.NO_IMAGE;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.inject.Inject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sys.diag.car.DB.DAO.CarDAO;
import sys.diag.car.DB.Entity.CarEntity;
import sys.diag.car.api.ApiService;
import sys.diag.car.api.contact.CarRequest;
import sys.diag.car.api.contact.CarResponse;
import sys.diag.car.api.contact.CarSyncResponse;
import sys.diag.car.api.contact.ResponseContact;
import sys.diag.car.common.DataImageUtil;
import sys.diag.car.dto.CarDto;
import sys.diag.car.dto.Result;
import sys.diag.car.dto.idMapping;

public class CarRepository {
    private final CarDAO carDAO;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final ApiService apiService;
    @Inject
    public CarRepository(CarDAO carDAO, ApiService apiService) {
        this.carDAO = carDAO;
        this.apiService  = apiService;
    }

    public LiveData<List<CarDto>> getAll(){
        return Transformations.map( this.carDAO.getAllCar(),cars->
            cars.stream().
                map(this::convertToCarDto)
                .collect(Collectors.toList())
        );
    }

    public List<CarEntity> getAllSync(){
        return this.carDAO.getAllCarSync();
    }
    public LiveData<CarDto> getById(long carId){
        return Transformations.map(this.carDAO.getCarById(carId),car->    Optional.ofNullable(car)
                .map(this::convertToCarDto)
                .orElse(null)
        );
    }

    public void create( CarDto car){

        CarEntity carEntity = Optional.ofNullable(car)
                .map(this::convertToCarEntity)
                .orElse(null);
        Log.e("SERVER_ID",String.valueOf(carEntity.getId()));


       executor.execute(()-> this.carDAO.insert(carEntity));
    }
    public void update(CarDto carDto){
        CarEntity carEntity = Optional.ofNullable(carDto)
                .map(this::convertToCarEntity)
                .orElse(null);
        carEntity.set_synchronized(false);
       executor.execute(()-> this.carDAO.update(carEntity));
    }
    public void update(CarEntity carEntity){

        executor.execute(()-> this.carDAO.update(carEntity));
    }
    public void delete(CarDto carDto){
        CarEntity carEntity = Optional.ofNullable(carDto)
                .map(this::convertToCarEntity)
                .orElse(null);
        executor.execute(()-> this.carDAO.delete(carEntity));
    }
    private void setSyncFlag(List<idMapping>ids){

        for(idMapping id:ids) {
            executor.execute(() -> carDAO.markAsSynchronized(id.getTemp_id(),id.getNew_id()));
        }
    }



    public void syncData(String token , MutableLiveData<Result<CarSyncResponse>> liveResponse, File internalDir) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<List<CarEntity>> future = executor.submit(this::getAllSync);

        executor.submit(() -> {
            try {
                List<CarEntity> carEntities = future.get()
                        .stream()
                        .filter(car->!car.get_synchronized())
                        .collect(Collectors.toList());
                List<Long> carsId = carEntities.stream()
                        .filter(car->!car.get_synchronized())
                        .map(carEntity -> carEntity.getId())
                        .collect(Collectors.toList());

                List<String> carsImage = carEntities.stream()
                        .filter(car->!car.get_synchronized())
                        .map(carEntity -> carEntity.getImageUri())
                        .collect(Collectors.toList());

                for(CarEntity car :carEntities){
                    Log.w("Car id",String.valueOf(car.getId()));
                }
                List<CarRequest>carRequest =
                        carEntities.stream()
                        .map(this::convertToCarRequestfromCarEntity)
                        .collect(Collectors.toList());

                Call<CarSyncResponse> call = apiService.synchronizeData("Bearer " + token, carRequest);
                List<MultipartBody.Part> parts =DataImageUtil.getAllJpgImagesFromInternalStorage(carsImage);;

                call.enqueue(new Callback<CarSyncResponse>() {
                    @Override
                    public void onResponse(Call<CarSyncResponse> call, Response<CarSyncResponse> response) {
                        if (response.isSuccessful()) {
                            setSyncFlag(response.body().getIds());
                            liveResponse.postValue(Result.success(response.body()));

                        } else {
                            try {
                                Gson gson = new Gson();
                                String errorJson = response.errorBody().string();
                                CarSyncResponse carSyncResponse= gson.fromJson( errorJson,CarSyncResponse.class);
                                liveResponse.postValue(Result.error("Ошибка синхронизации " + carSyncResponse.getDetail(), null));
                            }catch(IOException e){
                                liveResponse.postValue(Result.error("Ошибка синхронизации " + e.getMessage(), null));
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<CarSyncResponse> call, Throwable throwable) {
                        liveResponse.postValue(Result.error("Ошибка сети " + throwable.getMessage(), null));
                        throwable.printStackTrace();
                    }
                });
                if(!parts.isEmpty()) {
                    Call<CarSyncResponse> call2 = apiService.uploadImage("Bearer " + token, parts);
                    call2.enqueue(new Callback<CarSyncResponse>() {
                        @Override
                        public void onResponse(Call<CarSyncResponse> call, Response<CarSyncResponse> response) {
                            if (response.isSuccessful()) {
                                liveResponse.postValue(Result.success(response.body()));

                            } else {
                                try {
                                    Gson gson = new Gson();
                                    String errorJson = response.errorBody().string();
                                    CarSyncResponse carSyncResponse= gson.fromJson( errorJson,CarSyncResponse.class);
                                    liveResponse.postValue(Result.error("Ошибка синхронизации " + carSyncResponse.getDetail(), null));
                                }catch(IOException e){
                                    liveResponse.postValue(Result.error("Ошибка синхронизации " + e.getMessage(), null));
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<CarSyncResponse> call, Throwable throwable) {
                            liveResponse.postValue(Result.error("Ошибка сети " + throwable.getMessage(), null));
                        }
                    });
                }


            } catch (Exception e) {
                liveResponse.postValue(Result.error("Ошибка при чтении БД: " + e.getMessage(), null));
            } finally {
                executor.shutdown();
            }
        });

    }



    public void loadData(String token, MutableLiveData<Result<ResponseContact>> cars, File fileDir){
        Call<List<CarResponse>>call = apiService.getCars("Bearer "+token);
        call.enqueue(new Callback<List<CarResponse>>() {
            @Override
            public void onResponse(Call <List<CarResponse>> call, Response<List<CarResponse>> response) {
                if(response.isSuccessful() && response!=null)
                {
                    cars.postValue(Result.success(new ResponseContact(String.valueOf(response.code())
                                                                    ,"Success")));
                    saveAll(response.body(),fileDir);
                }
                else {
                    cars.postValue(Result.error("Ошибка при загрузке данных : "+response.code(),null));
                }
            }

            @Override
            public void onFailure(Call<List<CarResponse>> call, Throwable t) {
                System.out.println(t.getMessage());
                cars.postValue(Result.error("Ошибка при сети : "+t.getMessage(),null));
            }
        });
    }



    public void sendCarToSendSensors(MutableLiveData<Result<idMapping>>responseContact,CarDto carDto){
        CarEntity carEntity = Optional.ofNullable(carDto).map(this::convertToCarEntity).orElse(null);
        CarRequest carRequest = Optional.ofNullable(carEntity).map(this::convertToCarRequestfromCarEntity).orElse(null);
        carRequest.setCar_image_path(NO_IMAGE);
        Call<idMapping>call =apiService.createCarToSendSensors(carRequest);
        call.enqueue(new Callback<idMapping>() {
            @Override
            public void onResponse(Call<idMapping> call, Response<idMapping> response) {
                if(response.isSuccessful())
                {

                    carEntity.set_synchronized(true);
                    carEntity.setServer_id(response.body().getNew_id());
                    Log.w("SERVER_ID", String.valueOf(carEntity.getServer_id()));
                    update(carEntity);
                    responseContact.postValue(Result.success(response.body()));
                }
                else{
                    try {
                        String errorJson = response.errorBody().string();
                        Gson gson =new Gson();
                        ResponseContact responseContactConverted=gson.fromJson(errorJson, ResponseContact.class);
                        responseContact.postValue(Result.error(responseContactConverted.getDetail(),null));
                    }catch(IOException e){
                        responseContact.postValue(Result.error("Ошибка в обработке ответа",null));
                    }

                }
            }

            @Override
            public void onFailure(Call<idMapping> call, Throwable throwable) {
                responseContact.postValue(Result.error("Ошибка в сети"+throwable.getMessage(),null));
            }
        });
    }

    private void saveAll(List<CarResponse>carsResponse,File filesDir){
        List<CarEntity> carEntities = Optional.ofNullable(carsResponse)
                .orElse(Collections.emptyList())
                .stream()
                .map(this::convertToCarEntityFromCarResponse)
                .collect(Collectors.toList());
       executor.execute(()->{
           try {
               carEntities.stream().forEach(car -> {
                   Log.e("LOAD_IMAGE",car.getImageUri());
                   String internalPath = DataImageUtil.DownloadImageAndSaveLocal(filesDir, car.getImageUri());
                   car.setImageUri(internalPath);
               });
           }catch (Exception e) {
               e.printStackTrace();
           }
           carDAO.insertAll(carEntities);
       });
    }
    private CarRequest convertToCarRequestfromCarEntity(CarEntity carEntity){
        String carId;

        carId = String.valueOf(carEntity.getServer_id());
        Log.e("SERVER_ID",carId);
        if(carEntity.getServer_id()==null)
            carId = String.valueOf(carEntity.getId());
        Log.e("TEMP_ID",carId);
        Log.e("TEMP_ID",carEntity.getModelCar());
        String userId = String.valueOf(carEntity.getUserId());
        String[] args = carEntity.getImageUri().split("/");
        String imageName = args[args.length-1];
        return new CarRequest(carId
                ,userId
                ,carEntity.getModelCar()
                ,carEntity.getMarkCar()
                ,carEntity.getYearRelease()
                ,carEntity.getIssueBroken()
                ,imageName,carEntity.getServer_id());
    }
    private CarDto convertToCarDto(CarEntity carEntity){
        CarDto carDto = new CarDto(carEntity.getId(),
                carEntity.getMarkCar(),
                carEntity.getModelCar(),
                carEntity.getYearRelease(),
                carEntity.getIssueBroken(),
                carEntity.getImageUri(),
                carEntity.getUserId(), carEntity.get_synchronized());
        carDto.setServer_id(carEntity.getServer_id());
        return carDto;

    }
    private CarEntity convertToCarEntity( CarDto carDto){
            return  new CarEntity(
                    carDto.getId(),
                    carDto.getMarkCar(),
                    carDto.getModelCar(),
                    carDto.getYearRelease(),
                    carDto.getIssueBroken(),
                    carDto.getImageUri(),
                    carDto.getUserId(),
                    carDto.isCar_synchronized(), carDto.getServer_id());
    }


    private CarEntity convertToCarEntityFromCarResponse(CarResponse carResponse ){
        Long userUid = Long.parseLong(carResponse.getUser_uid());
        Long CarUid = Long.parseLong( carResponse.getUid());
        return new CarEntity(
                CarUid
                ,carResponse.getCar_mark()
                ,carResponse.getCar_model()
                ,carResponse.getCar_year()
                ,carResponse.getIssueBroken()
                ,carResponse.getImage_path()
                ,userUid,
                true,
                CarUid
        );
    }


}
