package sys.diag.car.api.contact;

public class LoginRequest
{
    private String email;
    private String password;

    public LoginRequest(String login, String password) {
        this.email = login;
        this.password = password;
    }

    public void setEmail(String login) {
        this.email = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
