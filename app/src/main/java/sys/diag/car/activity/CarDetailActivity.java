package sys.diag.car.activity;

import static android.widget.Toast.LENGTH_SHORT;

import static sys.diag.car.common.DataImageUtil.copyImageToInternalStorage;
import static sys.diag.car.common.DataImageUtil.deleteImagesByName;
import static sys.diag.car.common.Utility.NO_IMAGE;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import dagger.hilt.android.AndroidEntryPoint;
import javax.inject.Inject;

import es.dmoral.toasty.Toasty;
import sys.diag.car.DB.DAO.CarDAO;
import sys.diag.car.R;
import sys.diag.car.bluetooth.BlueToothConnection;
import sys.diag.car.bluetooth.SensorAction;
import sys.diag.car.bluetooth.SensorAction2;
import sys.diag.car.dto.CarDto;
import sys.diag.car.dto.Result;
import sys.diag.car.dto.SensorDto;
import sys.diag.car.obd.ObdAdapter;
import sys.diag.car.obd.ObdSession;
import sys.diag.car.viewmodels.CarViewModel;
import sys.diag.car.viewmodels.SensorViewModel;

@AndroidEntryPoint
public class CarDetailActivity extends AppCompatActivity {

    private final String NO_PREDICTED="Не диагностирован";
    private static final int PICK_IMAGE_REQUEST = 1;

    private CarViewModel carViewModel;
    private SensorViewModel sensorViewModel;
    private ProgressBar loadingSensors;
    private AppCompatButton btnBack,btnSendSensors,btnDelete;
    private TextView tvMarkCar,tvYearCar,tvIssueBroken,tvKmToFailure;
    private AppCompatButton btnGetSensors;
    private TableLayout tbSensors,tbErrorCodes;
    private View divide1,divide2;

    private List<String>SensorsList;
    private ObdAdapter obdAdapter;
    SensorDto sensorDto;
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


        if (getIntent() != null && getIntent().hasExtra("selectedCar")) {
            selectedCar=(CarDto)getIntent().getSerializableExtra("selectedCar");

            carViewModel.getByIdCar(selectedCar.getId()).observe(this,car-> {
                if(car!=null) {
                    tvMarkCar.setText(car.getMarkCar());
                    tvYearCar.setText(car.getYearRelease());
                    tvIssueBroken.setText(car.getIssueBroken() == null ? NO_PREDICTED : car.getIssueBroken());
                    String imagePath = car.getImageUri();
                    if (imagePath != null && !imagePath.equals(NO_IMAGE)) {
                        Log.e("LOAD_IMAGE", imagePath);
                        Picasso.get()
                                .load("file://" + imagePath) // Здесь вызывайте метод, который возвращает URL изображения
                                .placeholder(R.drawable.img_place_holder) // Заглушка, показываемая во время загрузки изображения
                                .error(R.drawable.img_error) // Заглушка, показываемая в случае ошибки загрузки
                                .into(ivCar);
                    } else if (imagePath == null) {
                        Picasso.get()
                                .load(R.drawable.img_place_holder)
                                .into(ivCar);
                    }
                }
            });
        }



        sensorViewModel.getLoadData(selectedCar.getId()).observe(this,result->{
            if(result.status == Result.Status.SUCCESS ){
                setupDataToUi2(setToMapSensors(result.data));
            }
        });

        sensorViewModel.getSendSensorsResult().observe(this,response->{
            loadingSensors.setVisibility(View.GONE);
            if(response.getStatus_code().equals("car_not_found"))
                Toasty.error(CarDetailActivity.this, response.getDetail(), Toasty.LENGTH_SHORT).show();
            else if(response.getStatus_code().equals("400")){
                Toasty.error(CarDetailActivity.this, response.getDetail(), Toasty.LENGTH_SHORT).show();
            }
            else {
                tvIssueBroken.setText(response.getIssue_broken());
                tvKmToFailure.setText(response.getKm_to_failure() + "км");
                Toasty.success(CarDetailActivity.this, response.getDetail(), Toasty.LENGTH_SHORT).show();
            }
        });

        carViewModel.getCarToSendSensors().observe(CarDetailActivity.this, result -> {
            loadingSensors.setVisibility(View.GONE);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (result.status == Result.Status.SUCCESS) {
                Log.w("Sensor car id send",String.valueOf(sensorDto.getCarId()));
                Log.w(" car id send",String.valueOf(selectedCar.server_id));
                selectedCar.setServer_id(result.data.getNew_id());

               sensorDto.setCarId(result.data.getNew_id());
                sensorViewModel.exctractSensors(sensorDto);
                sensorViewModel.sendSensors(result.data.getNew_id());
                loadingSensors.setVisibility(View.VISIBLE);
            } else {
                Toasty.error(CarDetailActivity.this, result.message, Toasty.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carViewModel.deleteCar(selectedCar);
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

                    obdAdapter.initializeObdProtocol();
                    extractSensorsFromObd();
                } catch (InterruptedException e){
                    Toasty.error(CarDetailActivity.this,"Проблемы с соединением адаптера",LENGTH_SHORT).show();
                }
                catch (IOException e){
                    Toasty.error(CarDetailActivity.this,"Адаптер не подключен ",LENGTH_SHORT).show();
                    generateRandomSensorDto();
                }
                catch (NullPointerException e){
                    Toasty.error(CarDetailActivity.this,"Проблемы с соединением адаптера",LENGTH_SHORT).show();
                    generateRandomSensorDto();
                }
                tbSensors.setVisibility(View.VISIBLE);
                tbErrorCodes.setVisibility(View.VISIBLE);
                divide1.setVisibility(View.VISIBLE);
                divide2.setVisibility(View.VISIBLE);


                btnSendSensors.setVisibility(View.VISIBLE);
            }
        });

        btnSendSensors.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                loadingSensors.setVisibility(View.VISIBLE);
                sensorDto = saveSensors();
                if(!selectedCar.isCar_synchronized()) {
                    carViewModel.sendCar(selectedCar);
                }
                else {
                    sensorDto.setCarId(selectedCar.getServer_id());
                    sensorViewModel.exctractSensors(sensorDto);
                    sensorViewModel.sendSensors(selectedCar.getServer_id());
                }
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
            if( selectedCar.getImageUri()!=null && !selectedCar.getImageUri().equals(NO_IMAGE))
                deleteImagesByName(CarDetailActivity.this,selectedCar.getImageUri());
            String imageFileName = selectedCar.getId()+"_Car"+System.currentTimeMillis()+".jpg";
            String internalPath=copyImageToInternalStorage(imageUri,CarDetailActivity.this,imageFileName);

            selectedCar.setImageUri(internalPath);
            selectedCar.setCar_synchronized(false);
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
        tvKmToFailure = findViewById(R.id.tvKmToFailureRes);
        divide1 = findViewById(R.id.view3);
        divide2 = findViewById(R.id.view4);
        btnSendSensors = findViewById(R.id.btnSendSensors);
        btnDelete = findViewById(R.id.btnDelete);
        SensorsList= new ArrayList<String>();
        loadingSensors = findViewById(R.id.loading5);
    }


    private void extractSensorsFromObd() {
        Map<String, SensorAction> actionMap = new LinkedHashMap<>();
        actionMap.put("temp_cool",obdAdapter::EngineCoolTemp);
        actionMap.put("RPM",obdAdapter::EngineRpm);
        actionMap.put("fuel_rate",obdAdapter::calculateFuelConsumption);
        actionMap.put("voltage",obdAdapter::Voltage);
        actionMap.put("MAF",obdAdapter::MAF);
        actionMap.put("IAT",obdAdapter::IntakeAirTemperature);
        actionMap.put("TPS",obdAdapter::ThrottlePos);
        actionMap.put("speed",obdAdapter::Speed);
        actionMap.put("fuel_trim",obdAdapter::getFuelTrim);
        actionMap.put("time_advance",obdAdapter::getTimingAdvance);

        setupDataToUi(actionMap);

    }

    public  void generateRandomSensorDto() {
        Random random = new Random();

        String coolTemp = String.format(Locale.US,"%.0f", 70 + random.nextDouble() * 50);         // 70–120 °C
        String rpm = String.format(Locale.US,"%.0f", 600 + random.nextDouble() +250);           // 600–7000 об/мин
        String fuelRate = String.format(Locale.US,"%.1f", 3 + random.nextDouble() * 17);          // 3–20 л/100км
        String voltage = String.format(Locale.US,"%.1f", 11 + random.nextDouble() * 3.7);         // 11–14.7 В
        String maf = String.format(Locale.US,"%.1f", 2 + random.nextDouble() * 48);               // 2–50 г/сек
        String iat = String.format(Locale.US,"%.0f", -20 + random.nextDouble() * 80);             // -20–60 °C
        String tps = String.format(Locale.US,"%.0f", random.nextDouble() * 100);                  // 0–100 %
        String speed = String.format(Locale.US,"%.0f", 0f);                // 0–180 км/ч
        String timingAdvance = String.format(Locale.US,"%.1f", -10 + random.nextDouble() * 50);   // -10–40 °
        String fuelTrim = String.format(Locale.US,"%.1f", -25 + random.nextDouble() * 50);        // -25–+25 %
       SensorDto sensorAction= new SensorDto(
                coolTemp,
                rpm,
                fuelRate,
                voltage,
                maf,
                iat,
                tps,
                speed,
                fuelTrim,
               timingAdvance,
               selectedCar.getId()
        );
        setupDataToUi2(setToMapSensors(sensorAction));
    }
    private  Map<String, SensorAction2> setToMapSensors(SensorDto sensorAction){
        Map<String, SensorAction2> actionMap = new LinkedHashMap<>();
        actionMap.put("temp_cool",sensorAction::getCoolTemp);
        actionMap.put("RPM",sensorAction::getRpm);
        actionMap.put("fuel_rate",sensorAction::getFuelRate);
        actionMap.put("voltage",sensorAction::getVoltage);
        actionMap.put("MAF",sensorAction::getMaf);
        actionMap.put("IAT",sensorAction::getIat);
        actionMap.put("TPS",sensorAction::getTps);
        actionMap.put("speed",sensorAction::getSpeed);
        actionMap.put("fuel_trim",sensorAction::getFuelTrim);
        actionMap.put("time_advance",sensorAction::getTimingAdvance);

        return actionMap;
    }
    void setupDataToUi(Map<String,SensorAction>actionMap){
        int i=1;
        for (SensorAction action : actionMap.values()) {
            String sensorId = "sensor" + i;
            int resID = getResources().getIdentifier(sensorId, "id", getPackageName());
            TextView sensor = findViewById(resID);
            if (sensor != null) {
                try {
                    Double result = action.execute();
                    String value = sensor.getText().toString();
                    sensor.setText(value.replace("--", result.toString()));
                    SensorsList.add(result.toString());
                } catch (NoDataException e) {
                    Log.e("SENSOR", e.getMessage());
                } catch (NullPointerException e) {
                    Log.e("SENSOR", e.getMessage());
                }

            }
            i++;
        }

//        String TroubleCode = obdAdapter.TroubleCode();
//        Toasty.info(this,"Код ошибок"+TroubleCode,Toasty.LENGTH_LONG).show();
    }
    void setupDataToUi2(Map<String,SensorAction2>actionMap){
        int i=1;
        for (SensorAction2 action : actionMap.values()) {
            String sensorId = "sensor" + i;
            int resID = getResources().getIdentifier(sensorId, "id", getPackageName());
            TextView sensor = findViewById(resID);
            if (sensor != null) {
                try {
                    String result = action.execute();
                    String value = sensor.getText().toString();
                    sensor.setText(value.replace("--", result.toString()));
                    SensorsList.add(result.toString());
                } catch (NoDataException e) {
                    Log.e("SENSOR", e.getMessage());
                } catch (NullPointerException e) {
                    Log.e("SENSOR", e.getMessage());
                }

            }
            i++;
        }

    }
    private SensorDto saveSensors(){
        if(!SensorsList.isEmpty()) {
            SensorDto sensorDto = new SensorDto(SensorsList.get(0),
                    SensorsList.get(1),
                    SensorsList.get(2),
                    SensorsList.get(3),
                    SensorsList.get(4),
                    SensorsList.get(5),
                    SensorsList.get(6),
                    SensorsList.get(7),
                    SensorsList.get(8),
                    SensorsList.get(9),
                    selectedCar.getServer_id());
            return sensorDto;
        }
        Log.w("SENSOR_SAVE","sensorList is null");
        return new SensorDto("Nodata","Nodata","Nodata","Nodata","Nodata","Nodata","Nodata","Nodata","Nodata","Nodata",selectedCar.getId());
    }
}
