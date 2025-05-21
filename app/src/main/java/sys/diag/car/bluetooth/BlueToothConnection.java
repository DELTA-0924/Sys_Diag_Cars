package sys.diag.car.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.RequiresPermission;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.UUID;

public class BlueToothConnection {

    private final BluetoothAdapter bluetoothAdapter;

    private  BluetoothDevice device;
    private static final String TAG ="ConnectToObdAdapter";
    private BluetoothSocket socket;

    public BlueToothConnection(){

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void findObdDevice(String deviceName) {

        for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
            if (device.getName().equalsIgnoreCase(deviceName)) {
                this.device = device;
            }
        }

    }
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public void connectToObdDevice(SocketCallback callback) {
    new Thread(()->{
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // SPP UUID
        try {
            this.socket = device.createRfcommSocketToServiceRecord(uuid);
            this.socket.connect();
            callback.onSocketReady(this.socket);
        } catch (IOException e) {
            Log.e(TAG, "Failed to connect to OBD device", e);

            callback.onError(e);
        }catch (Exception e){
            callback.onError(e);
        }
    }).start();


    }
    public BluetoothSocket getSocket(){
        return this.socket;
    }
}
