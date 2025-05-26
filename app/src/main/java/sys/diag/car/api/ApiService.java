package sys.diag.car.api;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.Call;
import retrofit2.http.Part;
import sys.diag.car.api.contact.CarRequest;
import sys.diag.car.api.contact.CarResponse;
import sys.diag.car.api.contact.CarSyncResponse;
import sys.diag.car.api.contact.LoginRequest;
import sys.diag.car.api.contact.LoginResponse;
import sys.diag.car.api.contact.PredictionResponse;
import sys.diag.car.api.contact.RegisterRequest;
import sys.diag.car.api.contact.RegisterResponse;
import sys.diag.car.api.contact.ResponseContact;
import sys.diag.car.api.contact.SensorRequest;
import sys.diag.car.dto.idMapping;

public interface ApiService {

   @POST("auth/login")
   Call<LoginResponse> login(@Body LoginRequest loginRequest);
   @POST("auth/signup")
   Call<RegisterResponse>register(@Body RegisterRequest regsiterRequest );
   @Headers("Content-Type: application/json")
   @GET("auth/me")
   Call<RegisterResponse>getCurrentUser(@Header("Authorization")String token);
   @Headers("Content-Type: application/json")

   @GET("auth/logout")
   Call<ResponseContact>logout(@Header("Authorization")String token);

   @Headers("Content-Type: application/json")
   @POST("car/sync")
   Call<CarSyncResponse>synchronizeData(@Header("Authorization")String token, @Body List<CarRequest> request);

   @Headers("Content-Type: application/json")
   @POST("car")
   Call<idMapping>createCarToSendSensors(@Body CarRequest request);

   @Headers("Content-Type: application/json")
   @GET("car")
   Call<List<CarResponse>>getCars(@Header("Authorization")String token);
   @Multipart
   @POST("car/upload-image")
   Call<CarSyncResponse>uploadImage(@Header("Authorization")String token, @Part List<MultipartBody.Part>images);
   @POST("sensor/send-sensors")
   Call<PredictionResponse> sendDataSensors(@Body SensorRequest sensorRequest);

}
