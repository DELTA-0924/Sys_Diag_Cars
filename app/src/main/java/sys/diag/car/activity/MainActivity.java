package sys.diag.car.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

import sys.diag.car.adapter.CardAdapter;
import sys.diag.car.DB.DataBaseHelper;
import sys.diag.car.common.OverlapPageTransformer;
import sys.diag.car.R;
import sys.diag.car.dto.CarDto;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter blueToothAdapter=BluetoothAdapter.getDefaultAdapter();
    private Button btnCreateCar;
    private ImageButton imgBtnBlueTooth;
    private DataBaseHelper databaseHelper;
    private SQLiteDatabase db;
    CardAdapter adapter;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.vp_cards);
        btnCreateCar=findViewById(R.id.btnCreateProfile);
        btnCreateCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. Регистрируем при старте (например, в onCreate)
                Intent intent = new Intent(MainActivity.this, CarCreateActivity.class);
                createCarLauncher.launch(intent);

            }
        });
        // Заполняем список данными

        //adapter = new CardAdapter(cards,this);
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(new OverlapPageTransformer());

        adapter.setOnItemClickListener(new CardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CarDto carDto) {
                Intent intent = new Intent(MainActivity.this, CarDetailActivity.class);
                intent.putExtra("selectedCar", carDto.getId());
                Log.d( "id by car in onItemClick: ",String.valueOf(carDto.getId()));
                startActivity(intent);
            }
        });
    }
    private final ActivityResultLauncher<Intent> createCarLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {

                }
            });



}
