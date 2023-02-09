package sk.ssnd.bluetoothrps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import sk.ssnd.bluetoothrps.ui.client.ClientActivity;
import sk.ssnd.bluetoothrps.ui.server.ServerActivity;

// TODO 1: počúvať na zmeny v settings ohľadom bluetooth - https://stackoverflow.com/a/24888208
// TODO 2: povoliť otvorenie nastavení po kliknutí na bluetooth_off ikonu
public class MainActivity extends AppCompatActivity {

    private TextView statusText;
    private ImageView statusImage;
    private CardView client;
    private CardView server;
    private TextView clientText;
    private TextView serverText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusImage = findViewById(R.id.status_image);
        statusText = findViewById(R.id.status);
        client = findViewById(R.id.client);
        server = findViewById(R.id.server);
        clientText = findViewById(R.id.client_text);
        serverText = findViewById(R.id.server_text);

        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null) {
            statusText.setText("Vaše zariadenie nepodporuje technológiu bluetooth, nemôžete pokračovať");
            statusImage.setImageResource(R.drawable.bluetooth_off);
            clientText.setEnabled(false);
            serverText.setEnabled(false);
            return;
        } else if (!bluetoothAdapter.isEnabled()) {
            statusText.setText("Je potrebné zapnúť Bluetooth");
            statusImage.setImageResource(R.drawable.bluetooth_off);
            clientText.setEnabled(false);
            serverText.setEnabled(false);
        } else {
            statusText.setText("Zvoľte, či je toto zariadenie klientom alebo serverom");
            statusImage.setImageResource(R.drawable.bluetooth);
            clientText.setEnabled(true);
            serverText.setEnabled(true);
        }

        findViewById(R.id.client).setOnClickListener(v -> {
            if (clientText.isEnabled()) startActivity(new Intent(this, ClientActivity.class));
        });

        findViewById(R.id.server).setOnClickListener(v -> {
            if (serverText.isEnabled()) startActivity(new Intent(this, ServerActivity.class));
        });
    }
}