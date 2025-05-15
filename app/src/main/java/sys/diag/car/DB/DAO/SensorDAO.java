package sys.diag.car.DB.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import sys.diag.car.DB.Entity.SensorEntity;

@Dao
public interface SensorDAO {
    @Insert
    void insert(SensorEntity sensor);
    @Update
    void update(SensorEntity sensor);
    @Delete
    void delete(SensorEntity sensor);
    @Query("SELECT * FROM sensors WHERE car_id = :carId")
    LiveData<SensorEntity> getSensors(long carId);
    @Query("SELECT * FROM sensors WHERE car_id = :carId")
    SensorEntity getSensorsSync(long carId);
}
