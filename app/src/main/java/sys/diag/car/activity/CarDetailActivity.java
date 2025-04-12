package sys.diag.car.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import sys.diag.car.repository.CarRepository;
import sys.diag.car.R;
import sys.diag.car.models.Car;

public class CarDetailActivity extends AppCompatActivity {
    CarRepository carRepository;
    TextView tvMarkCar,tvYearCar,tvIssueBroken;
    private final String NO_PREDICTED="Не диагностирован";
    ImageView ivCar;

    @Override
    protected void  onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_car);
        carRepository=CarRepository.getInstanse(getApplicationContext());
        tvMarkCar = findViewById(R.id.tvMarkCarRes);
        tvYearCar = findViewById(R.id.tvYearReleaseRes);
        tvIssueBroken = findViewById(R.id.tvIssueBrokenRes);
        ivCar = findViewById(R.id.ivCar);
        if (getIntent() != null && getIntent().hasExtra("selectedCar")) {
            Car selectedCar=carRepository.getCarById(getIntent().getLongExtra("selectedCar",1));
            tvMarkCar.setText(selectedCar.getMarkCar());
            tvYearCar.setText(selectedCar.getYearRelease());
            tvIssueBroken.setText(selectedCar.getIssueBroken()==null?NO_PREDICTED:selectedCar.getIssueBroken());
            String imagePath =selectedCar.getImageUri();
            if( imagePath!=null &&!imagePath.equals("No_Data")) {
                Log.e("LOAD_IMAGE",imagePath);
                Picasso.get()
                        .load( "file://"+imagePath) // Здесь вызывайте метод, который возвращает URL изображения
                        .placeholder(R.drawable.img_place_holder) // Заглушка, показываемая во время загрузки изображения
                        .error(R.drawable.img_error) // Заглушка, показываемая в случае ошибки загрузки
                        .into(ivCar);
            }
            else {
                Picasso.get()
                        .load(R.drawable.img_place_holder)
                        .into(ivCar);
            }
        }
    }
}
