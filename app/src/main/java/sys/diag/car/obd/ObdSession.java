package sys.diag.car.obd;

import android.bluetooth.BluetoothSocket;

import javax.inject.Singleton;

@Singleton
public class ObdSession {
    private BluetoothSocket socket;

    public void setSocket(BluetoothSocket socket) {
        this.socket = socket;
    }

    public BluetoothSocket getSocket() {
        return socket;
    }
}

