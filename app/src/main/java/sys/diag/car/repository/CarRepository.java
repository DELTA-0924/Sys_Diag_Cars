package sys.diag.car.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.io.File;
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
import sys.diag.car.api.contact.ResponseContact;
import sys.diag.car.common.DataImageUtil;
import sys.diag.car.dto.CarDto;
import sys.diag.car.dto.Result;

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
       executor.execute(()-> this.carDAO.insert(carEntity));
    }
    public void update(CarDto carDto){
        CarEntity carEntity = Optional.ofNullable(carDto)
                .map(this::convertToCarEntity)
                .orElse(null);
       executor.execute(()-> this.carDAO.update(carEntity));
    }

    public void delete(CarDto carDto){
        CarEntity carEntity = Optional.ofNullable(carDto)
                .map(this::convertToCarEntity)
                .orElse(null);
        executor.execute(()-> this.carDAO.delete(carEntity));
    }
    private void setSyncFlag(List<Long>cars){

        executor.execute(()->carDAO.markAsSynchronized(cars));
        for(Long id:cars)
            System.out.println("Id has marked : "+id);
    }


    public void syncData(String token , MutableLiveData<Result<ResponseContact>> liveResponse,File internalDir) {
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


                List<CarRequest>carRequest =  Optional.ofNullable(carEntities)
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(this::convertToCarRequestfromCarEntity)
                        .collect(Collectors.toList());

                Call<ResponseContact> call = apiService.synchronizeData("Bearer " + token, carRequest);
                List<MultipartBody.Part> parts =DataImageUtil.getAllJpgImagesFromInternalStorage(internalDir,carRequest.get(0).getCar_image_path());
                Call<ResponseContact> call2 = apiService.uploadImage("Bearer " + token, parts);
                call.enqueue(new Callback<ResponseContact>() {
                    @Override
                    public void onResponse(Call<ResponseContact> call, Response<ResponseContact> response) {
                        if (response.isSuccessful()) {
                            setSyncFlag(carsId);
                            liveResponse.postValue(Result.success(response.body()));

                        } else {
                            liveResponse.postValue(Result.error("Ошибка синхронизации " + response.body().getDetail(), null));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseContact> call, Throwable throwable) {
                        liveResponse.postValue(Result.error("Ошибка сети " + throwable.getMessage(), null));
                        throwable.printStackTrace();
                    }
                });

                call2.enqueue(new Callback<ResponseContact>() {
                    @Override
                    public void onResponse(Call<ResponseContact> call, Response<ResponseContact> response) {
                        if (response.isSuccessful()) {
                            setSyncFlag(carsId);
                            liveResponse.postValue(Result.success(response.body()));

                        } else {
                            liveResponse.postValue(Result.error("Ошибка синхронизации карниток " + response.body(), null));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseContact> call, Throwable throwable) {
                        liveResponse.postValue(Result.error("Ошибка сети " + throwable.getMessage(), null));
                    }
                });

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

    private void saveAll(List<CarResponse>carsResponse,File filesDir){
        List<CarEntity> carEntities = Optional.ofNullable(carsResponse)
                .orElse(Collections.emptyList())
                .stream()
                .map(this::convertToCarEntityFromCarResponse)
                .collect(Collectors.toList());
       executor.execute(()->{
           try {
               carEntities.stream().forEach(car -> {
                   Log.e("IMAGE_LOAD",car.getImageUri());
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
        String carId = String.valueOf(carEntity.getId());
        String userId = String.valueOf(carEntity.getUserId());
        String[] args = carEntity.getImageUri().split("/");
        String imageName = args[args.length-1];
        System.out.println("Car ID"+carId);
        return new CarRequest(carId
                ,userId
                ,carEntity.getModelCar()
                ,carEntity.getMarkCar()
                ,carEntity.getYearRelease()
                ,carEntity.getIssueBroken()
                ,imageName);
    }
    private CarDto convertToCarDto(CarEntity carEntity){
        return new CarDto(carEntity.getId(),
                carEntity.getMarkCar(),
                carEntity.getModelCar(),
                carEntity.getYearRelease(),
                carEntity.getIssueBroken(),
                carEntity.getImageUri(),
                carEntity.getUserId());

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
                false);
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
                true
        );
    }


}
