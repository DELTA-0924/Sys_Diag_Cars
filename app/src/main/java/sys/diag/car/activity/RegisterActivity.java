package sys.diag.car.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import dagger.hilt.android.AndroidEntryPoint;
import sys.diag.car.R;
import sys.diag.car.dto.CarDto;
import sys.diag.car.dto.UserDto;
import sys.diag.car.viewmodels.UserViewModel;

@AndroidEntryPoint
public class RegisterActivity extends AppCompatActivity {
    private UserViewModel userViewmodel;
    private Button btnRegister;
    private EditText name,email,password;
    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_register);
        userViewmodel = new ViewModelProvider(this).get(UserViewModel.class);
        setUpWidgets();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDto userDto = extractData();
                userViewmodel.Register(userDto);
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }
    private void setUpWidgets(){
        name=findViewById(R.id.editTextName);
        email=findViewById(R.id.editTextEmail);
        password=findViewById(R.id.editTextPassword);
        btnRegister=findViewById(R.id.btnRegister);
    }
    private UserDto extractData(){
        UserDto userDto= new UserDto(0,name.getText().toString(),
                                        email.getText().toString(),
                                        password.getText().toString());
        return userDto;
    }
}
