package sys.diag.car.activity;

import static android.widget.Toast.LENGTH_SHORT;
import static sys.diag.car.common.DataImageUtil.NO_IMAGE;
import static sys.diag.car.common.DataImageUtil.copyImageToInternalStorage;
import static sys.diag.car.common.DataImageUtil.deleteImagesByName;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import com.github.pires.obd.exceptions.NoDataException;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;
import javax.inject.Inject;
import sys.diag.car.DB.DAO.CarDAO;
import sys.diag.car.R;
import sys.diag.car.bluetooth.BlueToothConnection;
import sys.diag.car.bluetooth.SensorAction;
import sys.diag.car.dto.CarDto;
import sys.diag.car.obd.ObdAdapter;
import sys.diag.car.obd.ObdSession;
import sys.diag.car.viewmodels.CarViewModel;
import sys.diag.car.viewmodels.SensorViewModel;

@AndroidEntryPoint
public class CarDetailActivity extends AppCompatActivity {

    private CarViewModel carViewModel;
    private SensorViewModel sensorViewModel;
    Button btnBack;
    TextView tvMarkCar,tvYearCar,tvIssueBroken;
    private static final int PICK_IMAGE_REQUEST = 1;
    private AppCompatButton btnGetSensors;
    private final String NO_PREDICTED="Не диагностирован";
    private TableLayout tbSensors,tbErrorCodes;
    private View divide1,divide2;


     ObdAdapter obdAdapter;
     @Inject
    ObdSession obdSession;

    ImageView ivCar;
    CarDto selectedCar;
    private TextView sensor;
    @Override
    protected void  onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_car);

        BluetoothSocket socket = obdSession.getSocket();

         obdAdapter = new ObdAdapter(socket);


        carViewModel=new ViewModelProvider(this).get(CarViewModel.class);
        sensorViewModel=new ViewModelProvider(this).get(SensorViewModel.class);

        setup();




        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ivCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        btnGetSensors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v.setVisibility(View.GONE);

                try{
                    Thread.sleep(1500);
                    obdAdapter.initializeObdProtocol();
                } catch (InterruptedException e){
                    Toast.makeText(CarDetailActivity.this,e.getMessage(),LENGTH_SHORT).show();
                }
                catch (IOException e){
                    Toast.makeText(CarDetailActivity.this,e.getMessage(),LENGTH_SHORT).show();
                }
                catch (NullPointerException e){
                    Toast.makeText(CarDetailActivity.this,e.getMessage(),LENGTH_SHORT).show();
                }
                tbSensors.setVisibility(View.VISIBLE);
                tbErrorCodes.setVisibility(View.VISIBLE);
                divide1.setVisibility(View.VISIBLE);
                divide2.setVisibility(View.VISIBLE);
                extractSensors();
            }
        });
        if (getIntent() != null && getIntent().hasExtra("selectedCar")) {
             selectedCar=(CarDto)getIntent().getSerializableExtra("selectedCar");

            carViewModel.getByIdCar(selectedCar.getId()).observe(this,car-> {
                        tvMarkCar.setText(car.getMarkCar());
                        tvYearCar.setText(car.getYearRelease());
                        tvIssueBroken.setText(car.getIssueBroken() == null ? NO_PREDICTED : car.getIssueBroken());
                        String imagePath = car.getImageUri();
                        if( imagePath!=null &&!imagePath.equals(NO_IMAGE)) {
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
            if(!selectedCar.getImageUri().equals(NO_IMAGE))
                deleteImagesByName(CarDetailActivity.this,selectedCar.getImageUri());
            String imageFileName = selectedCar.getId()+"_Car"+System.currentTimeMillis()+".jpg";
            String internalPath=copyImageToInternalStorage(imageUri,CarDetailActivity.this,imageFileName);

            selectedCar.setImageUri(internalPath);
            Log.e("IMAGE_CAR",selectedCar.getImageUri());
            try {
                carViewModel.updateCar(selectedCar);

            }catch(Exception ex){
                Log.e("Add Car",ex.getMessage());
            }

        }
    }

    void setup(){
        tvMarkCar = findViewById(R.id.tvMarkCarRes);
        tvYearCar = findViewById(R.id.tvYearReleaseRes);
        tvIssueBroken = findViewById(R.id.tvIssueBrokenRes);
        btnBack=findViewById(R.id.btnBackDetail);
        ivCar = findViewById(R.id.ivCarDetail);
        btnGetSensors = findViewById(R.id.btnGetSensors);
        tbSensors = findViewById(R.id.TbSensors);
        tbErrorCodes = findViewById(R.id.TbErrorCodes);
        divide1 = findViewById(R.id.view3);
        divide2 = findViewById(R.id.view4);
    }

    private void extractSensors() {
        Map<String, SensorAction> actionMap = new LinkedHashMap<>();
        actionMap.put("temp_oil",obdAdapter::OilTempCommand);
        actionMap.put("temp_cool",obdAdapter::EngineCoolTemp);
        actionMap.put("RPM",obdAdapter::EngineRpm);
        actionMap.put("fuel_rate",obdAdapter::calculateFuelConsumption);
        actionMap.put("voltage",obdAdapter::Voltage);
        actionMap.put("MAF",obdAdapter::MAF);
        actionMap.put("IAT",obdAdapter::IntakeAirTemperature);
        actionMap.put("MAP",obdAdapter::IntakeManifoldPressureCommand);
        actionMap.put("TPS",obdAdapter::ThrottlePos);
        actionMap.put("speed",obdAdapter::Speed);
        actionMap.put("fuel_trim",obdAdapter::getFuelTrim);
        actionMap.put("time_advance",obdAdapter::getTimingAdvance);
        int i = 1;
        for (SensorAction action : actionMap.values()) {
            String sensorId = "sensor" + i;
            int resID = getResources().getIdentifier(sensorId, "id", getPackageName());
            TextView sensor = findViewById(resID);
            if (sensor != null) {
                try {
                    String result = action.execute();
                    sensor.setText(result);
                }catch (NoDataException e){
                    Log.e("SENSOR",e.getMessage());
                }

            }
            i++;
        }
    }
}
