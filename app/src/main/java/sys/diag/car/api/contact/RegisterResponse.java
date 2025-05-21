package sys.diag.car.api.contact;

public class RegisterResponse
{
    public ResponseContact message;
    public UserResponse user;

    public RegisterResponse(ResponseContact message, UserResponse user) {
        this.message = message;
        this.user   = user;
    }

    public void setMessage(ResponseContact message) {
        this.message = message;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public ResponseContact getMessage() {
        return message;
    }

    public UserResponse getUser() {
        return user;
    }
}
