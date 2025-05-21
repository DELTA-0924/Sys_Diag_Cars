package sys.diag.car.api.contact;

public class UserResponse {
    private String uid;
    private String username;
    private String  email;
    private String is_verified;
    private String created_at;
    private String updated_at;


    public UserResponse(String uid, String username, String email, String is_verified, String created_at, String updated_at) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.is_verified = is_verified;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getIs_verified() {
        return is_verified;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}
