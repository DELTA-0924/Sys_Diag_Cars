package sys.diag.car.DB.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name = "user_name")
    private String name;
    @ColumnInfo(name = "user_email")
    private String email;
    @ColumnInfo(name = "user_avatar")
    private String avatar;


    @ColumnInfo(name = "access_token")
    private String accessToken;
    @ColumnInfo(name = "refresh_token")
    private String refreshToken;

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

    public UserEntity(long id, String name, String email, String avatar, String accessToken, String refreshToken) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.avatar = avatar;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }


    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }



    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
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
}
