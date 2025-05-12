package sys.diag.car.DB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import sys.diag.car.DB.DAO.CarDAO;
import sys.diag.car.DB.DAO.UserDAO;
import sys.diag.car.DB.Entity.CarEntity;
import sys.diag.car.DB.Entity.UserEntity;


@Database(entities={UserEntity.class, CarEntity.class},version=7)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDAO userDAO();
    public abstract CarDAO carDAO();
    private static volatile AppDatabase INSTANCE;
    public static AppDatabase getDataBase(final Context context){
        if(INSTANCE ==null){
            synchronized (AppDatabase.class){
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                AppDatabase.class, "app_database")
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return INSTANCE;
    }
}
