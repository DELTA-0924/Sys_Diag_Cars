package sys.diag.car.activity;

import static sys.diag.car.common.DataImageUtil.copyImageToInternalStorage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.squareup.picasso.Picasso;

import dagger.hilt.android.AndroidEntryPoint;
import sys.diag.car.DB.DAO.CarDAO;
import sys.diag.car.R;
import sys.diag.car.dto.CarDto;
import sys.diag.car.viewmodels.CarViewModel;
@AndroidEntryPoint
public class CarDetailActivity extends AppCompatActivity {

    private CarViewModel carViewModel;
    Button btnBack;
    TextView tvMarkCar,tvYearCar,tvIssueBroken;
    private static final int PICK_IMAGE_REQUEST = 1;
    private final String NO_PREDICTED="Не диагностирован";
    ImageView ivCar;
    CarDto selectedCar;
    @Override
    protected void  onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_car);
        carViewModel=new ViewModelProvider(this).get(CarViewModel.class);
        tvMarkCar = findViewById(R.id.tvMarkCarRes);
        tvYearCar = findViewById(R.id.tvYearReleaseRes);
        tvIssueBroken = findViewById(R.id.tvIssueBrokenRes);
        btnBack=findViewById(R.id.btnBackDetail);
        ivCar = findViewById(R.id.ivCarDetail);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ivCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        if (getIntent() != null && getIntent().hasExtra("selectedCar")) {
             selectedCar=(CarDto)getIntent().getSerializableExtra("selectedCar");

            carViewModel.getByIdCar(selectedCar.getId()).observe(this,car-> {
                        tvMarkCar.setText(car.getMarkCar());
                        tvYearCar.setText(car.getYearRelease());
                        tvIssueBroken.setText(car.getIssueBroken() == null ? NO_PREDICTED : car.getIssueBroken());
                        String imagePath = car.getImageUri();
                        if( imagePath!=null &&!imagePath.equals("No_Data")) {
                            Log.e("LOAD_IMAGE",imagePath);
                            Picasso.get()
                                    .load( "file://"+imagePath) // Здесь вызывайте метод, который возвращает URL изображения
                                    .placeholder(R.drawable.img_place_holder) // Заглушка, показываемая во время загрузки изображения
                                    .error(R.drawable.img_error) // Заглушка, показываемая в случае ошибки загрузки
                                    .into(ivCar);
                        }
                        else if(imagePath.equals("image")){
                            Picasso.get()
                                    .load(R.drawable.img_place_holder)
                                    .into(ivCar);
                        }
            });
        }
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
            String internalPath=copyImageToInternalStorage(imageUri,CarDetailActivity.this,selectedCar.getId());
            selectedCar.setImageUri(internalPath);
            Log.e("IMAGE_CAR",selectedCar.getImageUri());
            try {
                carViewModel.updateCar(selectedCar);
            }catch(Exception ex){
                Log.e("Add Car",ex.getMessage());
            }

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
