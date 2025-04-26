package sys.diag.car.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
    private final String NO_PREDICTED="Не диагностирован";
    ImageView ivCar;

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
        if (getIntent() != null && getIntent().hasExtra("selectedCar")) {
            long selectedCarId=getIntent().getLongExtra("selectedCar",1);

            carViewModel.getByIdCar(selectedCarId).observe(this,car-> {
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
                        else {
                            Picasso.get()
                                    .load(R.drawable.img_place_holder)
                                    .into(ivCar);
                        }
            });
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
