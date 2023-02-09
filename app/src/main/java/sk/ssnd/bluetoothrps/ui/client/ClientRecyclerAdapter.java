package sk.ssnd.bluetoothrps.ui.client;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sk.ssnd.bluetoothrps.R;

public final class ClientRecyclerAdapter extends RecyclerView.Adapter<ClientRecyclerAdapter.DeviceViewHolder> {

    public interface OnBluetoothDeviceSelectedListener {
        void onBluetoothDeviceSelected(BluetoothDevice device);
    }

    private final OnBluetoothDeviceSelectedListener listener;
    private int selectedDevice = -1;

    public ClientRecyclerAdapter(OnBluetoothDeviceSelectedListener listener) {
        this.listener = listener;
    }

    private final List<BluetoothDevice> devices = new ArrayList<>();

    public void addDevice(BluetoothDevice device) {
        devices.add(device);
        notifyItemInserted(devices.size() - 1);
    }

    public void clear() {
        int size = devices.size();
        devices.clear();
        notifyItemRangeRemoved(0, size - 1);
    }

    private void selectDevice(int position) {
        int oldSelected = selectedDevice;
        selectedDevice = position;

        if (oldSelected != -1) { notifyItemChanged(oldSelected); }
        notifyItemChanged(position);
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeviceViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bt_device, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        holder.bind(devices.get(position), position == selectedDevice, position);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder{

        private final TextView name;
        private final TextView address;
        private final View check;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            check = itemView.findViewById(R.id.check);
        }

        @SuppressLint("MissingPermission")
        public void bind(BluetoothDevice bluetoothDevice, boolean isSelected, int position) {
            check.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            name.setText(bluetoothDevice.getName());
            address.setText(bluetoothDevice.getAddress());
            itemView.setOnClickListener((v) -> {
                        listener.onBluetoothDeviceSelected(bluetoothDevice);
                        selectDevice(position);
                    }
            );
        }
    }
}
