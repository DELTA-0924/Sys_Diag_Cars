package sys.diag.car.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import java.util.Optional;

import dagger.hilt.android.AndroidEntryPoint;
import sys.diag.car.R;
import sys.diag.car.dto.UserDto;
import sys.diag.car.viewmodels.UserViewModel;
@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {
    private UserViewModel userViewModel;
    private AppCompatButton btnLogin;
    private EditText userName,password;
    private TextView textRegister;
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
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passwordStr=password.getText().toString();
                String userNameStr=userName.getText().toString();

                 userViewModel.Login(userNameStr,passwordStr).observe(LoginActivity.this,user->{
                     if(user==null){
                         Toast.makeText(LoginActivity.this, "Введенные данные не верны", Toast.LENGTH_LONG).show();
                     }
                     else{
                         Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                         startActivity(intent);
                     }
                 });

            }
        });
    }
    private void setUpWidgets(){
        btnLogin = findViewById(R.id.btnLogin);
        userName = findViewById(R.id.editTextNameLogin);
        password = findViewById(R.id.editTextPasswordLogin);
        textRegister = findViewById(R.id.tvMessage2);
    }

}
