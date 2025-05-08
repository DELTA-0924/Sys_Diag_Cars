package sys.diag.car.dto;

import java.util.List;

public class UserDto {
    private long id;
    private String name;
    private String email;
    private String password;
    private String imagePath;
    private String accessToken;
    private String refreshToken;

    public UserDto(long id, String name, String email,String password,String imagePath) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.imagePath = imagePath;
    }
    public UserDto(long id, String name, String email,String accessToken,String refreshToken,String imagePath) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.imagePath = imagePath;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    private List<CarDto> userCarDtos;

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserCars(List<CarDto> userCarDtos) {
        this.userCarDtos = userCarDtos;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<CarDto> getUserCars() {
        return userCarDtos;
    }


}
