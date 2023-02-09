package sk.ssnd.bluetoothrps.ui.client;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import sk.ssnd.bluetoothrps.R;
import sk.ssnd.bluetoothrps.bluetooth.ClientBluetoothThread;
import sk.ssnd.bluetoothrps.bluetooth.CommunicationBluetoothThread;
import sk.ssnd.bluetoothrps.bluetooth.SocketReceivedInterface;

public class ClientActivity extends AppCompatActivity implements SocketReceivedInterface, ClientRecyclerAdapter.OnBluetoothDeviceSelectedListener {

    private ClientBluetoothThread clientThread;
    private CommunicationBluetoothThread thread;
    private ClientRecyclerAdapter recyclerAdapter;
    private BluetoothManager manager;

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

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void doOnPermissionsNotGranted() {
        requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT);
    }

    @RequiresPermission("android.permission.BLUETOOTH_CONNECT")
    private void doOnPermissionsGranted() {
        recyclerAdapter.clear();
        manager = getSystemService(BluetoothManager.class);
        for (BluetoothDevice device : manager.getAdapter().getBondedDevices()) {
            recyclerAdapter.addDevice(device);
        }
    }

    private void doOnPermissionsDenied() {
        Toast.makeText(this, "Bluetooth permissions were rejected - can't continue", Toast.LENGTH_SHORT).show();
    }
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);


        recyclerAdapter = new ClientRecyclerAdapter(this);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);

        manager = getSystemService(BluetoothManager.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            doOnPermissionsNotGranted();
        } else {
            doOnPermissionsGranted();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (thread != null) { thread.interrupt(); }
        if (clientThread != null) {
            clientThread.cancel();
            clientThread.interrupt();
        }
    }

    // region socket handling
    @Override
    public void onSocketReceived(BluetoothSocket socket) {
        Log.e("ClientActivity", "Socket open");
        thread = new CommunicationBluetoothThread(socket);
        thread.start();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBluetoothDeviceSelected(BluetoothDevice device) {
        Log.e("ClientActivity", "Device selected - " + device.getName());
        clientThread = new ClientBluetoothThread(device, manager.getAdapter(), this);
        clientThread.start();
    }
    // endregion
}
