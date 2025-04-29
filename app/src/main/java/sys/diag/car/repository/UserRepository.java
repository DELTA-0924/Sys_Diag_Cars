package sys.diag.car.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import sys.diag.car.DB.DAO.UserDAO;
import sys.diag.car.DB.DAO.UserWithCars;
import sys.diag.car.DB.Entity.UserEntity;
import sys.diag.car.dto.UserDto;

public class UserRepository {
    private final UserDAO userDAO;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public UserRepository(UserDAO userDAO) {
        this.userDAO = userDAO;
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


    public LiveData<UserDto> getUser(){
        return Transformations.map(this.userDAO.getUser(),user->
            Optional.ofNullable(user)
                    .map(this::convertToUserDto)
                    .orElse(null)
                );
    }

    public LiveData<List<UserWithCars>>getUserCars(){
        return this.userDAO.getUsersWithCars();
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


    private UserDto convertToUserDto(UserEntity userEntity){
        return new UserDto(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getEmail(),
                "userPassword",
                userEntity.getAvatar()
        );
    }
    private UserEntity convertToUserEntity(UserDto userDto){
        return new UserEntity(userDto.getId(),
                userDto.getName(),
                userDto.getEmail(),
                userDto.getImagePath()
        );
    }
}
