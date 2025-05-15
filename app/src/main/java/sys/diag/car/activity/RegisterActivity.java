package sys.diag.car.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import dagger.hilt.android.AndroidEntryPoint;
import sys.diag.car.R;
import sys.diag.car.dto.CarDto;
import sys.diag.car.dto.Result;
import sys.diag.car.dto.UserDto;
import sys.diag.car.viewmodels.UserViewModel;

@AndroidEntryPoint
public class RegisterActivity extends AppCompatActivity {
    private UserViewModel userViewmodel;
    private Button btnRegister;
    private ProgressBar loadingUI;
    private EditText name,email,password;
    private TextView goLogin;
    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_register);
        userViewmodel = new ViewModelProvider(this).get(UserViewModel.class);
        setUpWidgets();
        userViewmodel.getRegisterResult().observe(RegisterActivity.this,result->{
            if(result.status == Result.Status.SUCCESS){
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
            else if(result.status == Result.Status.ERROR){
                Toast.makeText(RegisterActivity.this, "Ошибка: " + result.message, Toast.LENGTH_SHORT).show();
            }
            loadingUI.setVisibility(View.GONE);
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDto userDto = extractData();
                userViewmodel.register(userDto);
                loadingUI.setVisibility(View.VISIBLE);
            }
        });
        goLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }
    private void setUpWidgets(){
        name=findViewById(R.id.editTextName);
        email=findViewById(R.id.editTextEmail);
        password=findViewById(R.id.editTextPassword);
        btnRegister=findViewById(R.id.btnRegister);
        loadingUI = findViewById(R.id.loading4);
        goLogin = findViewById(R.id.tvGoToLogin);
    }
    private UserDto extractData(){
        UserDto userDto= new UserDto(0,name.getText().toString(),
                                        email.getText().toString(),
                                        password.getText().toString(),
                                        "No Data")
                    ;
        return userDto;
    }
}
