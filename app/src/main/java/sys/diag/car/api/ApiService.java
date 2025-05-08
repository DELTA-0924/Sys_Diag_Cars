package sys.diag.car.api;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.Call;
import sys.diag.car.api.contact.CarRequest;
import sys.diag.car.api.contact.CarResponse;
import sys.diag.car.api.contact.CarsRequest;
import sys.diag.car.api.contact.CarsResponse;
import sys.diag.car.api.contact.LoginRequest;
import sys.diag.car.api.contact.LoginResponse;
import sys.diag.car.api.contact.RegisterRequest;
import sys.diag.car.api.contact.RegisterResponse;
import sys.diag.car.api.contact.ResponseContact;

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
   Call<ResponseContact>synchronizeData(@Header("Authorization")String token, @Body List<CarRequest> request);

   @Headers("Content-Type: application/json")
   @GET("car")
   Call<List<CarResponse>>getCars(@Header("Authorization")String token);

}
