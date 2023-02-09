package sk.ssnd.bluetoothrps.bluetooth;

import static sk.ssnd.bluetoothrps.bluetooth.Shared.APPLICATION_UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import java.io.IOException;

public final class ServerBluetoothThread extends Thread {

    private final BluetoothServerSocket mmServerSocket;
    private final SocketReceivedInterface socketInterface;

    @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
    public ServerBluetoothThread(BluetoothAdapter bluetoothAdapter, SocketReceivedInterface socketInterface) {
        BluetoothServerSocket tmp = null;
        try {
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("APPID", APPLICATION_UUID);
        } catch (IOException e) {
            Log.e("Server", "Socket's listen() method failed", e);
        }
        mmServerSocket = tmp;
        this.socketInterface = socketInterface;
    }

    public void run() {
        BluetoothSocket socket;
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.e("Server", "Socket's accept() method failed", e);
                break;
            }

            if (socket != null) {
                socketInterface.onSocketReceived(socket);
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    Log.e("Server", "Can't close socket connection", e);
                }
                break;
            }
        }
    }

    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            Log.e("Server", "Could not close the connect socket", e);
        }
    }
}