package sys.diag.car.activity;

import static sys.diag.car.common.Utility.GUEST;
import static sys.diag.car.common.Utility.NO_IMAGE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Optional;

import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;
import sys.diag.car.R;
import sys.diag.car.dto.Result;
import sys.diag.car.dto.UserDto;
import sys.diag.car.viewmodels.UserViewModel;
@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {
    private UserViewModel userViewModel;
    private AppCompatButton btnLogin;
    private EditText userName;
    private TextInputEditText password;
    private TextView textRegister,tvLoginGuest;
    private ProgressBar loadingUi;

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_login);
        setUpWidgets();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        userViewModel.getLoginResult().observe(LoginActivity.this,result->{
            if(result.status== Result.Status.SUCCESS){
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                Toasty.success(LoginActivity.this,  result.data.getMessage(), Toast.LENGTH_SHORT).show();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            else if(result.status == Result.Status.ERROR){
                Toasty.error(LoginActivity.this, "Ошибка: " + result.message, Toast.LENGTH_SHORT).show();

            }

        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String passwordStr=password.getText().toString();
                String userNameStr=userName.getText().toString();
                userViewModel.login(userNameStr,passwordStr);

            }
        });
        tvLoginGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                Toasty.info(LoginActivity.this, "Выполнен вход как гость: ", Toast.LENGTH_SHORT).show();
                UserDto guest = new UserDto(9999,GUEST,GUEST,GUEST,NO_IMAGE);
                userViewModel.guestLogin(guest);
                startActivity(intent);
            }
        });
    }
    private void setUpWidgets(){
        btnLogin = findViewById(R.id.btnLogin);
        userName = findViewById(R.id.editTextNameLogin);
        password = findViewById(R.id.InnereditTextPasswordLogin);
        textRegister = findViewById(R.id.tvMessage2);
        loadingUi  = findViewById(R.id.loading3);
        tvLoginGuest = findViewById(R.id.tvLoginGuest);
    }

}
