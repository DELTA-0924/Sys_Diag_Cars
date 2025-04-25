package sys.diag.car.repository;

import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import sys.diag.car.DB.DAO.UserDAO;
import sys.diag.car.DB.Entity.UserEntity;
import sys.diag.car.dto.UserDto;

public class UserRepository {
    private final UserDAO userDAO;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public UserRepository(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserDto getByEmail(String email){
        return Optional.ofNullable(this.userDAO.getUserByEmail(email))
                .map(this::convertToUserDto)
                .orElse(null);
    }

    public UserDto getUser(){
        return Optional.ofNullable(this.userDAO.getUser())
                .map(this::convertToUserDto)
                .orElse(null);
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
                userEntity.getEmail()
        );
    }
    private UserEntity convertToUserEntity(UserDto userDto){
        return new UserEntity(userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }
}
