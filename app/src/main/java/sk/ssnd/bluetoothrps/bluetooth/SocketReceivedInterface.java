package sk.ssnd.bluetoothrps.bluetooth;

import android.bluetooth.BluetoothSocket;

public interface SocketReceivedInterface {
    void onSocketReceived(BluetoothSocket socket);
}
