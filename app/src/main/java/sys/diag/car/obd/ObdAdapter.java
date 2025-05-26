package sys.diag.car.obd;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.control.ModuleVoltageCommand;
import com.github.pires.obd.commands.control.TimingAdvanceCommand;
import com.github.pires.obd.commands.control.TroubleCodesCommand;
import com.github.pires.obd.commands.engine.MassAirFlowCommand;
import com.github.pires.obd.commands.engine.OilTempCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.ThrottlePositionCommand;
import com.github.pires.obd.commands.fuel.FuelTrimCommand;
import com.github.pires.obd.commands.pressure.IntakeManifoldPressureCommand;
import com.github.pires.obd.commands.protocol.AvailablePidsCommand_01_20;
import com.github.pires.obd.commands.protocol.AvailablePidsCommand_21_40;
import com.github.pires.obd.commands.protocol.AvailablePidsCommand_41_60;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;


public class ObdAdapter {

    private static final String TAG ="ObdAdapter";
    private BluetoothSocket socket;
    private InputStream in;
    private OutputStream out;
    public ObdAdapter(BluetoothSocket socket){
        this.socket = socket;
    }

    /**
     * Инициализация OBD протокола.
     */
    public void initializeObdProtocol() throws IOException, InterruptedException {
        new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
        new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
        new TimeoutCommand(62).run(socket.getInputStream(), socket.getOutputStream());
        new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();

    }
    public int EngineRpm() {
        try {
            RPMCommand rpmCommand = new RPMCommand();
            rpmCommand.run(socket.getInputStream(), socket.getOutputStream());
            return rpmCommand.getRPM();
        } catch (IOException | InterruptedException e) {
            Log.e(TAG, "Failed to get RPM", e);
            return 404;
        }
    }

    /**
     * Получить текущую скорость автомобиля.
     */
    public int Speed() {
        try {
            SpeedCommand speedCommand = new SpeedCommand();
            speedCommand.run(socket.getInputStream(), socket.getOutputStream());
            return speedCommand.getMetricSpeed();
        } catch (IOException | InterruptedException e) {
            Log.e(TAG, "Failed to get Speed", e);
            return 404;
        }
    }
    public float EngineCoolTemp(){
        try{
            EngineCoolantTemperatureCommand coolTempCommand=new EngineCoolantTemperatureCommand();
            coolTempCommand.run(socket.getInputStream(),socket.getOutputStream());
            return coolTempCommand.getTemperature();
        }catch(IOException | InterruptedException e){
            Log.e(TAG,"Failed to get Coolant tempereture",e);
            return 404;
        }
    }
    public double Voltage(){
        try{
            ModuleVoltageCommand moduleVoltageCommand = new ModuleVoltageCommand();
            moduleVoltageCommand.run(socket.getInputStream(),socket.getOutputStream());
            return moduleVoltageCommand.getVoltage();
        }catch(IOException | InterruptedException e){
            Log.e(TAG,"Failed to get Coolant tempereture",e);
            return 404;
        }
    }
    public Double MAF(){
        try{
            MassAirFlowCommand mafCommand=new MassAirFlowCommand();
            mafCommand.run(socket.getInputStream(),socket.getOutputStream());
            return mafCommand.getMAF();
        }catch(IOException |InterruptedException e){
            Log.e(TAG,"Failed to get Mass Air Flow",e);
            return 404d;
        }
    }
    public float IntakeAirTemperature(){
        try{
            AirIntakeTemperatureCommand getAirIntakeTempCommad = new AirIntakeTemperatureCommand ();
            getAirIntakeTempCommad.run(socket.getInputStream(),socket.getOutputStream());

            return getAirIntakeTempCommad.getTemperature();
        }catch(IOException |InterruptedException e){
            Log.e(TAG,"Failed to get Intake Air Temperature",e);
            return 404f;
        }
    }
    public String IntakeManifoldPressureCommand(){
        try{
            IntakeManifoldPressureCommand intakeManifoldPressureCommand = new IntakeManifoldPressureCommand ();
            intakeManifoldPressureCommand.run(socket.getInputStream(),socket.getOutputStream());

            return intakeManifoldPressureCommand.getFormattedResult();
        }catch(IOException |InterruptedException e){
            Log.e(TAG,"Failed to get Intake Air Temperature",e);
            return "Error";
        }
    }

    public float ThrottlePos(){
        try{
            ThrottlePositionCommand throttlePosCommand=new ThrottlePositionCommand();
            throttlePosCommand.run(socket.getInputStream(),socket.getOutputStream());
            return throttlePosCommand.getPercentage();
        }catch(IOException |InterruptedException e){
            Log.e(TAG,"Failed to get throttle pos engine",e);
            return 404f;
        }
    }

    public String TroubleCode(){
        String troubleCodes = "";
        try {
            TroubleCodesCommand troubleCodesCommand = new TroubleCodesCommand();
            troubleCodesCommand.run(socket.getInputStream(), socket.getOutputStream());
             troubleCodes = troubleCodesCommand.getFormattedResult();
        }catch(IOException | InterruptedException e){
            Log.e(TAG,"Failed to get trouble codes",e);
            return "404f";
        }
        return  troubleCodes;
    }

    public double calculateFuelConsumption() {
        try {
            MassAirFlowCommand mafCommand = new MassAirFlowCommand();
            mafCommand.run(socket.getInputStream(), socket.getOutputStream());

            double maf = mafCommand.getMAF(); // г/сек

            // Параметры
            double afr = 14.7; // Коэффициент воздух-топливо для бензина
            double fuelDensity = 745; // г/л

            // Расчёт: топливоподача в литрах/час
            double fuelRateLph = (maf * 3600) / (afr * fuelDensity);

            return fuelRateLph;

        } catch (IOException | InterruptedException e) {
            Log.e(TAG, "Failed to calculate fuel consumption", e);
            return 404;
        }
    }
    public String OilTempCommand(){
        try{
            OilTempCommand oilTempCommand=new OilTempCommand();
            oilTempCommand.run(socket.getInputStream(),socket.getOutputStream());
            return oilTempCommand.getFormattedResult();
        }catch(IOException |InterruptedException e ) {
            Log.e(TAG,"Failed to get throttle pos engine",e);
            return "Error";
        }
    }

    public float getFuelTrim() {
        FuelTrimCommand cmd = new FuelTrimCommand();
        try {
            cmd.run(socket.getInputStream(), socket.getOutputStream());
            return cmd.getPercentage(); // Пример: "3.1 %"
        } catch (Exception e) {
            return 404;
        }
    }

    public float getTimingAdvance() {
        TimingAdvanceCommand cmd = new TimingAdvanceCommand();
        try {
            cmd.run(socket.getInputStream(), socket.getOutputStream());
            return cmd.getPercentage(); // Пример: "12.5 °"
        } catch (Exception e) {
            return 404;
        }
    }
    public double calculateMafManual(double rpm, double map, double iat, double engineDisplacementLiters, double ve) {
        double iatKelvin = iat + 273.15;
        double maf = (rpm * map * ve * engineDisplacementLiters * 100) / (120 * iatKelvin);
        return maf; // г/с
    }


    public void logSupportedPids() {
        try {
            // 1. Первый диапазон: 01 - 20
            AvailablePidsCommand_01_20 cmd1 = new AvailablePidsCommand_01_20();
            cmd1.run(in, out);
            String pids1 = cmd1.getCalculatedResult();
            Log.i("OBD_PIDS", "Supported PIDs [01-20]: " + pids1.toString());

            // 2. Второй диапазон: 21 - 40
            AvailablePidsCommand_21_40 cmd2 = new AvailablePidsCommand_21_40();
            cmd2.run(in, out);
            String pids2 = cmd2.getCalculatedResult();
            Log.i("OBD_PIDS", "Supported PIDs [21-40]: " + pids2.toString());

            // 3. Третий диапазон: 41 - 60
            AvailablePidsCommand_41_60 cmd3 = new AvailablePidsCommand_41_60();
            cmd3.run(in, out);
            String pids3 = cmd3.getCalculatedResult();
            Log.i("OBD_PIDS", "Supported PIDs [41-60]: " + pids3.toString());

        } catch (Exception e) {
            Log.e("OBD_PIDS", "Ошибка при получении PID: " + e.getMessage(), e);
        }
    }

}
