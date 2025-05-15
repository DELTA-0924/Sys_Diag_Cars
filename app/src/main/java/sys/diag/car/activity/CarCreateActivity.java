package sys.diag.car.activity;

import static android.widget.Toast.LENGTH_LONG;

import static sys.diag.car.common.DataImageUtil.NO_IMAGE;
import static sys.diag.car.common.DataImageUtil.copyImageToInternalStorage;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import dagger.hilt.android.AndroidEntryPoint;
import sys.diag.car.R;
import sys.diag.car.dto.CarDto;
import sys.diag.car.dto.UserDto;
import sys.diag.car.viewmodels.CarViewModel;
import sys.diag.car.viewmodels.UserViewModel;

@AndroidEntryPoint
public class CarCreateActivity extends AppCompatActivity {
    private CarViewModel carViewModel;
    private UserViewModel userViewModel;

    Button btnBack;
    Button btnCreate;

    EditText markCar,modelCar,yearCar;
    CarDto carDto;
    UserDto currentUser;
    private String imageFilePath;
    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_car);
        carViewModel=new ViewModelProvider(this).get(CarViewModel.class);
        userViewModel=new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getCurrent().observe(this,result->{
            currentUser=result.data;
        });
        btnBack=findViewById(R.id.btnBack);
        btnCreate=findViewById(R.id.btnCreate);
        markCar=findViewById(R.id.etName);
        modelCar=findViewById(R.id.etModel);
        yearCar=findViewById(R.id.etYearRelease);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mark = markCar.getText().toString().trim();
                String model = modelCar.getText().toString().trim();
                String year = yearCar.getText().toString().trim();
                if(mark.isEmpty()|| model.isEmpty()||year.isEmpty()){
                    Toast.makeText(CarCreateActivity.this,"Поля должны быть заполнеными",LENGTH_LONG).show();
                    return ;
                }
                carDto=new CarDto(0,mark,model,year,null,NO_IMAGE,currentUser.getId());
                carViewModel.CreateCar(carDto);
                //openFileChooser();
                Toast.makeText(CarCreateActivity.this, "Автомобиль добавлен", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }



    @Override
      public void onBackPressed() {
            super.onBackPressed();
        }
}
