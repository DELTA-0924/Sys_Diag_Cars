package sys.diag.car.activity;

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

import java.util.Optional;

import dagger.hilt.android.AndroidEntryPoint;
import sys.diag.car.R;
import sys.diag.car.dto.Result;
import sys.diag.car.dto.UserDto;
import sys.diag.car.viewmodels.UserViewModel;
@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {
    private UserViewModel userViewModel;
    private AppCompatButton btnLogin;
    private EditText userName,password;
    private TextView textRegister;
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
                Toast.makeText(LoginActivity.this, "Успех: " + result.message, Toast.LENGTH_SHORT).show();
                startActivity(intent);
                Log.w("TOKEN",result.data.getAccess_token());
            }
            else if(result.status == Result.Status.ERROR){
                Toast.makeText(LoginActivity.this, "Ошибка: " + result.message, Toast.LENGTH_SHORT).show();

            }
            loadingUi.setVisibility(View.INVISIBLE);
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingUi.setVisibility(View.VISIBLE);
                String passwordStr=password.getText().toString();
                String userNameStr=userName.getText().toString();
                userViewModel.login(userNameStr,passwordStr);

            }
        });
    }
    private void setUpWidgets(){
        btnLogin = findViewById(R.id.btnLogin);
        userName = findViewById(R.id.editTextNameLogin);
        password = findViewById(R.id.editTextPasswordLogin);
        textRegister = findViewById(R.id.tvMessage2);
        loadingUi  = findViewById(R.id.loading3);
    }

}
