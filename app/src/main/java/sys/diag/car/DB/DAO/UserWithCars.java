package sys.diag.car.DB.DAO;

import androidx.room.Dao;
import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import sys.diag.car.DB.Entity.CarEntity;
import sys.diag.car.DB.Entity.UserEntity;

public class UserWithCars{
    @Embedded
    public UserEntity user;
    @Relation(parentColumn = "id",entityColumn = "user_id")
    public List<CarEntity> cars;
}