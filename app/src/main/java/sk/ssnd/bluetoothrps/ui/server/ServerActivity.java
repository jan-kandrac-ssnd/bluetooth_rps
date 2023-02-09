package sk.ssnd.bluetoothrps.ui.server;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import sk.ssnd.bluetoothrps.R;
import sk.ssnd.bluetoothrps.bluetooth.CommunicationBluetoothThread;
import sk.ssnd.bluetoothrps.bluetooth.ServerBluetoothThread;
import sk.ssnd.bluetoothrps.bluetooth.SocketReceivedInterface;

public class ServerActivity extends AppCompatActivity implements SocketReceivedInterface, CommunicationBluetoothThread.OnMessageReceiveListener {

    private CommunicationBluetoothThread thread;
    private ServerBluetoothThread serverThread;

    // region Bluetooth Permissions
    @SuppressLint("MissingPermission")
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    doOnPermissionsGranted();
                } else {
                    doOnPermissionsDenied();
                }
            });

    private void doOnPermissionsNotGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT);
        }
    }

    @RequiresPermission("android.permission.BLUETOOTH_CONNECT")
    private void doOnPermissionsGranted() {
        BluetoothManager manager = getSystemService(BluetoothManager.class);
        serverThread = new ServerBluetoothThread(manager.getAdapter(), this);
        serverThread.start();
    }

    private void doOnPermissionsDenied() {
        Toast.makeText(this, "Bluetooth permissions were rejected - can't continue", Toast.LENGTH_SHORT).show();
    }
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            doOnPermissionsNotGranted();
        } else {
            doOnPermissionsGranted();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (thread != null) { thread.interrupt(); }
        if (serverThread != null) {
            serverThread.cancel();
            serverThread.interrupt();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onSocketReceived(BluetoothSocket socket) {
        Log.e("ServerActivity", "Socket open with client -> " + socket.getRemoteDevice().getName());
        thread = new CommunicationBluetoothThread(socket, this);
        thread.start();
    }

    @Override
    public void onMessage(String message) {
        Log.e("ServerActivity", "Message received -> " + message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
