package sys.diag.car.repository;

import static sys.diag.car.common.DataImageUtil.NO_IMAGE;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.gson.Gson;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sys.diag.car.DB.DAO.UserDAO;
import sys.diag.car.DB.DAO.UserWithCars;
import sys.diag.car.DB.Entity.CarEntity;
import sys.diag.car.DB.Entity.UserEntity;
import sys.diag.car.api.ApiService;
import sys.diag.car.api.contact.LoginRequest;
import sys.diag.car.api.contact.LoginResponse;
import sys.diag.car.api.contact.RegisterRequest;
import sys.diag.car.api.contact.RegisterResponse;
import sys.diag.car.api.contact.ResponseContact;
import sys.diag.car.dto.Result;
import sys.diag.car.dto.UserDto;

public class UserRepository {
    private final UserDAO userDAO;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final ApiService apiService;
    public UserRepository(UserDAO userDAO, ApiService apiService) {
        this.userDAO = userDAO;
        this.apiService = apiService;
    }

    public LiveData<UserDto> getByEmail(String email){
        return Transformations.map(this.userDAO.getUserByEmail(email),user->
                Optional.ofNullable(user)
                .map(this::convertToUserDto)
                .orElse(null)
        );

    }
    public  LiveData<UserDto>  getByName(String name){
        return Transformations.map(this.userDAO.getUserByName(name),user->
                Optional.ofNullable(user)
                        .map(this::convertToUserDto)
                        .orElse(null)
        );
    }


    public LiveData<Result<UserDto>> getUser(){
        return Transformations.map(this.userDAO.getUser(),user->
            Optional.ofNullable(user)
                    .map(this::convertToUserDto)
                    .map(Result::success)
                    .orElse(Result.error("USER_NOT_FOUND",null))
                );
    }

    public LiveData<List<UserWithCars>>getUserCars(){
        return this.userDAO.getUsersWithCars();
    }

    public void create(LoginResponse loginResponse){
        UserEntity userEntity=Optional.ofNullable(loginResponse)
                .map(this::ConvertToUserEntityFormLoginResponse)
                .orElse(null);
        System.out.println("User id from users "+userEntity.getId());
        executor.execute(()->this.userDAO.insert(userEntity));

    }

    public void create(UserDto userDto){
        UserEntity userEntity=Optional.ofNullable(userDto)
                .map(this::convertToUserEntity)
                .orElse(null);

        executor.execute(()->this.userDAO.insert(userEntity));
    }


    public void update(UserDto userDto){
        UserEntity userEntity=Optional.ofNullable(userDto)
                .map(this::convertToUserEntity)
                .orElse(null);
        executor.execute(()-> this.userDAO.update(userEntity));
    }

    public void delete(UserDto userDto){
        UserEntity userEntity=Optional.ofNullable(userDto)
                .map(this::convertToUserEntity)
                .orElse(null);
      executor.execute(()->  this.userDAO.delete(userEntity));
    }

    public void RegisterUser(UserDto userDto, MutableLiveData<Result<RegisterResponse>> liveData){
        String result;
        RegisterRequest userRegister = Optional.ofNullable(userDto)
                .map(this::convertToRegisterRequest)
                .orElse(null);
        Call<RegisterResponse>call =apiService.register(userRegister);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if(response.isSuccessful() && response.body() !=null ){
                    liveData.postValue(Result.success(response.body()));
                }
                else {
                    try{
                        Gson gson = new Gson();
                        String responseRow =  response.errorBody().string();
                        ResponseContact responseContact = gson.fromJson(responseRow, ResponseContact.class);
                        liveData.postValue(Result.error(responseContact.getDetail(),null));
                    }catch (IOException e){
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable throwable) {
                liveData.postValue(Result.error("Ошибка сети"+throwable.getMessage(),null));
            }
        });
    }

    public void LoginUser(String email,String password, MutableLiveData<Result<LoginResponse>> liveData){
        String result;
        LoginRequest userLogin = new LoginRequest(email,password);
        Call<LoginResponse>call =apiService.login(userLogin);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful() && response.body() !=null ){
                    create(response.body());

                    liveData.postValue(Result.success(response.body()));

                }
                else {
                    try{
                        Gson gson = new Gson();
                        String responseRow =  response.errorBody().string();
                        ResponseContact responseContact = gson.fromJson(responseRow, ResponseContact.class);
                        liveData.postValue(Result.error(responseContact.getDetail(),null));
                    }catch (IOException e){
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable throwable) {
                liveData.postValue(Result.error("Ошибка сети"+throwable.getMessage(),null));
            }
        });
    }



    private UserDto convertToUserDto(UserEntity userEntity){
        return new UserDto(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getEmail(),
                userEntity.getAccessToken(),
                userEntity.getRefreshToken(),
                userEntity.getAvatar()
        );
    }

    private UserEntity ConvertToUserEntityFormLoginResponse(LoginResponse loginResponse){
        return new UserEntity(
               Long.parseLong(loginResponse.getUser().getUid()),
                loginResponse.getUser().getUsername(),
                loginResponse.getUser().getEmail(),
                NO_IMAGE,
                loginResponse.getAccess_token(),
                loginResponse.getRefresh_token());

    }


    private UserEntity convertToUserEntity(UserDto userDto) {
        return new UserEntity(userDto.getId(),
                userDto.getName(),
                userDto.getEmail(),
                userDto.getImagePath(),
                userDto.getAccessToken(),
                userDto.getRefreshToken()
        );
    }

    private RegisterRequest convertToRegisterRequest(UserDto userDto){
        return new RegisterRequest(
                userDto.getEmail(),
                userDto.getName(),
                userDto.getPassword());
    }

    private LoginRequest convertToLoginRequest(UserDto userDto){
        return new LoginRequest(
                userDto.getEmail(),
                userDto.getPassword());
    }


    private UserDto convertToUserDtoFromRegisterResponse(RegisterResponse registerResponse){
        return new UserDto(
                Long.parseLong(registerResponse.getUid()),
                registerResponse.getUsername(),
                registerResponse.getEmail(),
                "userPassword",
                "image");
    }

}
