package sys.diag.car.DB.DAO;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;
import sys.diag.car.DB.Entity.CarEntity;

@Dao()
public interface CarDAO{
    @Insert
    void insert(CarEntity car);
    @Update
    void update(CarEntity car);
    @Delete
    void delete(CarEntity car);
    @Query("DELETE FROM cars")
    void deleteAllUsers();

    @Query("SELECT * FROM cars ")
    LiveData<List<CarEntity>> getAllCar();
    @Query("SELECT * FROM cars ")
    List<CarEntity>getAllCarSync();
    @Query("SELECT * FROM cars WHERE id= :carId")
    LiveData< CarEntity> getCarById(long carId);
    @Query( "SELECT * FROM cars WHERE user_id= :userId")
    LiveData< List<CarEntity>>getCarsForUser(long userId);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CarEntity> cars);
    @Query("UPDATE cars SET car_synchronized = 1 WHERE id IN (:ids)")
    void markAsSynchronized(List<Long> ids);
}