package sys.diag.car.bluetooth;

import android.bluetooth.BluetoothSocket;

public interface SocketCallback {
    void onSocketReady(BluetoothSocket socket);
    void onError(Exception e);
}
