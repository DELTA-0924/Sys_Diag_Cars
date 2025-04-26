package sys.diag.car.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.inject.Inject;

import sys.diag.car.DB.DAO.CarDAO;
import sys.diag.car.DB.Entity.CarEntity;
import sys.diag.car.dto.CarDto;

public class CarRepository {
    private final CarDAO carDAO;
    private final Executor executor = Executors.newSingleThreadExecutor();
    @Inject
    public CarRepository(CarDAO carDAO) {
        this.carDAO = carDAO;
    }

    public LiveData<List<CarDto>> getAll(){
        return Transformations.map( this.carDAO.getAllCar(),cars->
            cars.stream().
                map(this::convertToCarDto)
                .collect(Collectors.toList())
        );
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
                carDto.getUserId());
    }
}
