package sys.diag.car.api.contact;

public class LoginResponse {
    private String message;
    private String access_token;
    private String refresh_token;
    private String status_code;
    private String detail;

    public String getStatus_code() {
        return status_code;
    }

    public String getDetail() {
        return detail;
    }

    private User user;

    public User getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public static  class User{
        private String email;

        public String getUsername() {
            return username;
        }

        private String username;
        private String uid;
        public void setEmail(String email) {
            this.email = email;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getEmail() {
            return email;
        }

        public String getUid() {
            return uid;
        }

        public User(String uid, String email) {
            this.uid = uid;
            this.email = email;
        }


    }
}
