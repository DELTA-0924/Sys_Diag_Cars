package sys.diag.car.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import sys.diag.car.dto.UserDto;
import sys.diag.car.repository.UserRepository;

@HiltViewModel
public class UserViewModel extends ViewModel {
    private final UserRepository userRepository;

    @Inject
    public UserViewModel(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    public void Register(UserDto userDto){
        this.userRepository.create(userDto);
    }

    public  void Login(String email,String password){
        this.userRepository.getByEmail(email);
    }
    public UserDto getCurrent(){
        return this.userRepository.getUser();
    }

    public void Logout(UserDto userDto){
        this.userRepository.delete(userDto);
    }

    public void Edit( UserDto userDto){
        this.userRepository.update(userDto);
    }
}
