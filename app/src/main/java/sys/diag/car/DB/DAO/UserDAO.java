package sys.diag.car.DB.DAO;

import androidx.room.Dao;
import androidx.room.Embedded;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Relation;
import androidx.room.Transaction;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

import sys.diag.car.DB.Entity.CarEntity;
import sys.diag.car.DB.Entity.UserEntity;

@Dao
public interface UserDAO {
    class UserWithCars{
        @Embedded
        public UserEntity user;
        @Relation(parentColumn = "id",entityColumn = "user_id")
        public List<CarEntity>cars;
    }
    @Insert
    void insert(UserEntity user);
    @Update
    void update(UserEntity user);
    @Delete
    void delete(UserEntity user);
    @Query("DELETE FROM users")
    void deleteAllUsers();
    @Query("SELECT * FROM users ORDER BY user_name ASC LIMIT 1")
    UserEntity getUser();
    @Query("SELECT * FROM users WHERE id= :userId")
    UserEntity getUserById(long userId);
    @Query("SELECT * FROM users WHERE user_email= :userEmail")
    UserEntity getUserByEmail(String userEmail);
    @Transaction
    @Query("SELECT * FROM users")
    List<UserWithCars>getUsersWithCars();
}
