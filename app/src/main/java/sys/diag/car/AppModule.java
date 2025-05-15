package sys.diag.car;

import android.app.Application;

import androidx.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sys.diag.car.DB.AppDatabase;
import sys.diag.car.DB.DAO.CarDAO;
import sys.diag.car.DB.DAO.SensorDAO;
import sys.diag.car.DB.DAO.UserDAO;
import sys.diag.car.api.ApiService;
import sys.diag.car.repository.CarRepository;
import sys.diag.car.repository.SensorRepository;
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
    SensorDAO providesSensorDAO(AppDatabase db){ return db.sensorDAO();}


    @Provides
    @Singleton
    SensorRepository provideSensorRepository(SensorDAO sensorDAO, ApiService apiService){
        return new SensorRepository(sensorDAO,apiService);
    }
    @Provides
    @Singleton
    UserRepository provideUserRepository(UserDAO userDAO,ApiService apiService){
        return new UserRepository(userDAO,apiService);
    }
    @Provides
    @Singleton
    CarRepository provideCarRepository(CarDAO carDAO,ApiService apiService){
        return new CarRepository(carDAO,apiService);
    }


    @Provides
    @Singleton
    public static HttpLoggingInterceptor provideLoggingInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return logging;
    }

    @Provides
    @Singleton
    public static OkHttpClient provideOkHttpClient(HttpLoggingInterceptor logging) {
        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
    }

    @Provides
    @Singleton
    public static Retrofit provideRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl("http://192.168.1.5:8000/api/v1/") // ← замени на свою
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }
    @Provides
    @Singleton
    public static ApiService provideApiService(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }
}
