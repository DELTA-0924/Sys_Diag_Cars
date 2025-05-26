package sys.diag.car.activity;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

import static sys.diag.car.common.Utility.GUEST;
import static sys.diag.car.common.Utility.OBD_II;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;
import sys.diag.car.adapter.CardAdapter;
import sys.diag.car.DB.DataBaseHelper;
import sys.diag.car.bluetooth.SocketCallback;
import sys.diag.car.common.DataImageUtil;
import sys.diag.car.common.OverlapPageTransformer;
import sys.diag.car.R;
import sys.diag.car.dto.CarDto;
import sys.diag.car.dto.Result;
import sys.diag.car.dto.UserDto;
import sys.diag.car.obd.ObdAdapter;
import sys.diag.car.obd.ObdSession;
import sys.diag.car.viewmodels.CarViewModel;
import sys.diag.car.viewmodels.UserViewModel;
import sys.diag.car.bluetooth.BlueToothConnection;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;

    ImageView mainIocn;
    private CarViewModel carViewModel;
    @Inject
     BlueToothConnection blueToothConnection;
    @Inject
    ObdSession obdSession;


    private UserViewModel userViewModel;
    private AppCompatButton btnCreateCar,btnOnBlueTooth;
    CardAdapter adapter;
    private ViewPager2 viewPager;
    private ProgressBar loadingUi;
    private Boolean hasData =false;
    private UserDto user=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);




        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        carViewModel= new ViewModelProvider(this).get(CarViewModel.class);

        adapter = new CardAdapter(new ArrayList<>(),this);

        carViewModel.getAllCars().observe(this,cars->{
            if(!cars.isEmpty())
                hasData =true;
            for(CarDto car :cars){
                Log.w("Car id",String.valueOf(car.getId()));
            }
            adapter.setCardList(cars);
        });

        userViewModel.getCurrent().observe(this,result->{

            if(result.status == Result.Status.ERROR){
                Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
            else {
                user = result.data;
                if(!user.getName().equals(GUEST)) {
                    String token = user.getAccessToken();
                    carViewModel.loadData(token, MainActivity.this.getFilesDir());
                    loadingUi.setVisibility(View.VISIBLE);
                }
            }
        });

        carViewModel.getLoadData().observe(this,result->{
            if(result.status == Result.Status.ERROR && result.data==null)
                Toasty.warning(this,result.message, LENGTH_LONG).show();
            loadingUi.setVisibility(View.GONE);
        });
        setUp();

        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(new OverlapPageTransformer());

        mainIocn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user!=null && !user.getName().equals(GUEST)) {
                    Intent intent = new Intent(MainActivity.this, UserDetailActivity.class);
                    createCarLauncher.launch(intent);
                }else {

                    Toasty.info(MainActivity.this,"Для входа личный кабинет авторизуйтесь",Toasty.LENGTH_LONG).show();
                    Toasty.info(MainActivity.this,"Удерживайте картинку что бы авторизоваться",Toasty.LENGTH_LONG).show();
                }
            }
        });
        mainIocn.setOnLongClickListener(v->{
            Intent intent=new Intent(MainActivity.this, LoginActivity.class);
            userViewModel.Logout(user);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;

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

        btnOnBlueTooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blueToothConnect();
                loadingUi.setVisibility(View.VISIBLE);
            }
        });

    }

    private void setUp(){
        viewPager = findViewById(R.id.vp_cards);
        btnCreateCar=findViewById(R.id.btnCreateProfile);
        mainIocn=findViewById(R.id.imageView);
        loadingUi=findViewById(R.id.loading1);
        btnOnBlueTooth=findViewById(R.id.btnOnBluewTooth);
    }
    private final ActivityResultLauncher<Intent> createCarLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            });
    void blueToothConnect(){
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
        }, REQUEST_BLUETOOTH_PERMISSIONS);

        blueToothConnection = new BlueToothConnection();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Toasty.warning(MainActivity.this,"Permission denied",Toasty.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                    REQUEST_BLUETOOTH_PERMISSIONS);
            return;
        }

        blueToothConnection.findObdDevice(OBD_II);
            blueToothConnection.connectToObdDevice(new SocketCallback() {
            @Override
            public void onSocketReady(BluetoothSocket socket) {
                runOnUiThread(() -> {

                    Toasty.success(MainActivity.this,"Соединение установлено",Toasty.LENGTH_LONG).show();
                    loadingUi.setVisibility(View.GONE);
                    obdSession.setSocket(socket);
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(()->{

                    Toasty.warning(MainActivity.this,"Failed to connect to OBD device",Toasty.LENGTH_LONG).show();
                    loadingUi.setVisibility(View.GONE);
                });
            }
        });



    }
}
