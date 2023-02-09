package sk.ssnd.bluetoothrps.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public final class CommunicationBluetoothThread extends Thread {

    private static final String TAG = "COMUNICATION";

    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    public interface OnMessageReceiveListener {
        void onMessage(String message);
        void onWriteError(Exception e);
    }

    private final OnMessageReceiveListener listener;

    public CommunicationBluetoothThread(BluetoothSocket socket, OnMessageReceiveListener listener) {
        mmSocket = socket;
        this.listener = listener;

        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating I/O streams");
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    @Override
    public void run() {
        Log.d(TAG, "Communication started");
        byte[] mmBuffer = new byte[1024];
        while (true) {
            try {
                Log.d(TAG, "Waiting for message");
                int readBytes = mmInStream.read(mmBuffer);
                Log.d(TAG, "Message of size -> " + readBytes);
                if (readBytes == -1) continue;
                String message = new String(mmBuffer, StandardCharsets.UTF_8);
                Log.d(TAG, "Message content -> " + message);
                new Handler(Looper.getMainLooper()).post(() -> listener.onMessage(message));
            } catch (IOException e) {
                Log.d(TAG, "Input stream was disconnected", e);
                break;
            }
        }
    }

    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when sending data", e);
            listener.onWriteError(e);
        }
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }
}