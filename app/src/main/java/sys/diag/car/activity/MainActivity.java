package sys.diag.car.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import sys.diag.car.adapter.CardAdapter;
import sys.diag.car.DB.DataBaseHelper;
import sys.diag.car.common.DataImageUtil;
import sys.diag.car.common.OverlapPageTransformer;
import sys.diag.car.R;
import sys.diag.car.dto.CarDto;
import sys.diag.car.dto.Result;
import sys.diag.car.viewmodels.CarViewModel;
import sys.diag.car.viewmodels.UserViewModel;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    ImageView mainIocn;
    private CarViewModel carViewModel;
    private UserViewModel userViewModel;
    private BluetoothAdapter blueToothAdapter=BluetoothAdapter.getDefaultAdapter();
    private Button btnCreateCar;
    CardAdapter adapter;
    private ViewPager2 viewPager;
    private ProgressBar loadingUi;
    private Boolean hasData =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        carViewModel= new ViewModelProvider(this).get(CarViewModel.class);

        userViewModel.getCurrent().observe(this,user->{

            if(user==null){

                Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            else {
                String uid = String.valueOf(user.getId());
                String token = user.getAccessToken();
                carViewModel.loadData(token, MainActivity.this.getFilesDir());
            }
        });

        carViewModel.getLoadData().observe(this,result->{
            if(result.status == Result.Status.ERROR && result.data==null)
                Toast.makeText(this,result.message,Toast.LENGTH_LONG).show();
            loadingUi.setVisibility(View.GONE);
        });
        setUp();
        adapter = new CardAdapter(new ArrayList<>(),this);
        carViewModel.getAllCars().observe(this,cars->{
            if(!cars.isEmpty())
                hasData=true;
            adapter.setCardList(cars);

            Log.e("LIST_CARS","HAS CHANGED");
        });


        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(new OverlapPageTransformer());

        mainIocn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, UserDetailActivity.class);
                createCarLauncher.launch(intent);
            }
        });


        btnCreateCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. Регистрируем при старте (например, в onCreate)
                Intent intent = new Intent(MainActivity.this, CarCreateActivity.class);
                createCarLauncher.launch(intent);

            }
        });

        adapter.setOnItemClickListener(new CardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CarDto carDto) {
                Intent intent = new Intent(MainActivity.this, CarDetailActivity.class);
                intent.putExtra("selectedCar", carDto);

                createCarLauncher.launch(intent);
            }
        });


    }

    private void setUp(){
        viewPager = findViewById(R.id.vp_cards);
        btnCreateCar=findViewById(R.id.btnCreateProfile);
        mainIocn=findViewById(R.id.imageView);
        loadingUi=findViewById(R.id.loading1);
    }



    private final ActivityResultLauncher<Intent> createCarLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

            });
}
