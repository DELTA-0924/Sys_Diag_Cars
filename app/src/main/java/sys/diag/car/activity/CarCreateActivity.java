package sys.diag.car.activity;

import static android.widget.Toast.LENGTH_LONG;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import sys.diag.car.repository.CarRepository;
import sys.diag.car.R;
import sys.diag.car.models.Car;

public class CarCreateActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    ImageButton btnBack;
    Button btnCreate;
    CarRepository carRepository;
    EditText markCar,modelCar,yearCar;
    Car car;
    private String imageFilePath;
    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        carRepository=CarRepository.getInstanse(getApplicationContext());
        setContentView(R.layout.activity_create_profile);
        btnBack=findViewById(R.id.btnBack);
        btnCreate=findViewById(R.id.btnCreate);
        markCar=findViewById(R.id.editTextBrand);
        modelCar=findViewById(R.id.editTextModel);
        yearCar=findViewById(R.id.editTextYear);

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
                car=new Car(0,mark,model,year,null,null);
                openFileChooser();


            }
        });
    }
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            String internalPath=copyImageToInternalStorage(imageUri);
            car.setImageUri(internalPath);

            try {
                carRepository.addCar(car);
            }catch(Exception ex){
                Log.e("Add Car",ex.getMessage());
            }
            Toast.makeText(CarCreateActivity.this, "Автомобиль добавлен", Toast.LENGTH_SHORT).show();
        }
        car.setImageUri("NoData");
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }
    private String getPathFromUri(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

    private String copyImageToInternalStorage(Uri uri) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "image_" + timeStamp + ".jpg";

        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
            if (inputStream == null) return null;

            File internalFile = new File(getFilesDir(), imageFileName);
            try (FileOutputStream outputStream = new FileOutputStream(internalFile)) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                return internalFile.getAbsolutePath();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
      public void onBackPressed() {
            super.onBackPressed();
        }
}
