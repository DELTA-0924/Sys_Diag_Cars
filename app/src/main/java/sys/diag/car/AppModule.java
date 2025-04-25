package sys.diag.car;

import android.app.Application;

import androidx.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import sys.diag.car.DB.AppDatabase;
import sys.diag.car.DB.DAO.CarDAO;
import sys.diag.car.DB.DAO.UserDAO;
import sys.diag.car.repository.CarRepository;
import sys.diag.car.repository.UserRepository;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {
    @Provides
    @Singleton
    public static AppDatabase provideAppDatabase(Application application) {
        return Room.databaseBuilder(application, AppDatabase.class, "car_database")
                .fallbackToDestructiveMigration()
                .build();
    }
    @Provides
    @Singleton
    UserDAO provideUserDao(AppDatabase db){
        return db.userDAO();
    }
    @Provides
    @Singleton
    CarDAO providesCarDAO(AppDatabase db){
        return db.carDAO();
    }
    @Provides
    @Singleton
    UserRepository provideUserRepository(UserDAO userDAO){
        return new UserRepository(userDAO);
    }
    @Provides
    @Singleton
    CarRepository provideCarRepository(CarDAO carDAO){
        return new CarRepository(carDAO);
    }
}
