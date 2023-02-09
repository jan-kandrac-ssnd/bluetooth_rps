package sk.ssnd.bluetoothrps.bluetooth;

import static sk.ssnd.bluetoothrps.bluetooth.Shared.APPLICATION_UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import java.io.IOException;

public final class ClientBluetoothThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final SocketReceivedInterface socketInterface;

    @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
    public ClientBluetoothThread(BluetoothDevice device, SocketReceivedInterface socketInterface) {
        BluetoothSocket tmp = null;

        try {
            tmp = device.createRfcommSocketToServiceRecord(APPLICATION_UUID);
        } catch (IOException e) {
            Log.e("Client", "Socket's create() method failed", e);
        }
        mmSocket = tmp;
        this.socketInterface = socketInterface;
    }


    @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
    public void run() {
        try {
            mmSocket.connect();
        } catch (IOException connectException) {
            Log.e("Client", "Could not connect to server");
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e("Client", "Could not close the client socket", closeException);
            }
            return;
        }
        socketInterface.onSocketReceived(mmSocket);
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e("Client", "Could not close the client socket", e);
        }
    }
}