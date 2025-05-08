package sys.diag.car.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import sys.diag.car.DB.DAO.UserWithCars;
import sys.diag.car.api.contact.LoginRequest;
import sys.diag.car.api.contact.LoginResponse;
import sys.diag.car.api.contact.RegisterResponse;
import sys.diag.car.dto.Result;
import sys.diag.car.dto.UserDto;
import sys.diag.car.repository.UserRepository;

@HiltViewModel
public class UserViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<Result<RegisterResponse>> registerResult = new MutableLiveData<>();
    private final MutableLiveData<Result<LoginResponse>> loginResult = new MutableLiveData<>();
    @Inject
    public UserViewModel(UserRepository userRepository){
        this.userRepository=userRepository;
    }




    public LiveData<List<UserWithCars>> getUserCars(){
        return this.userRepository.getUserCars();
    }

    public  LiveData<UserDto> Login(String name,String password){
        return this.userRepository.getByName(name);
    }
    public LiveData <UserDto> getCurrent(){
        return this.userRepository.getUser();
    }

    public LiveData<Result<RegisterResponse>>getRegisterResult(){
        return registerResult;
    }

    public LiveData<Result<LoginResponse>>getLoginResult(){
        return loginResult;
    }

    public void register(UserDto userDto){
        userRepository.RegisterUser(userDto,registerResult);
    }

    public void login(String email,String password){
        userRepository.LoginUser(email,password,loginResult);
    }

    public void Logout(UserDto userDto){
        this.userRepository.delete(userDto);
    }

    public void Edit( UserDto userDto){
        this.userRepository.update(userDto);
    }
}
