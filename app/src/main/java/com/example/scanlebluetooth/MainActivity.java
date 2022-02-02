package com.example.scanlebluetooth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final int BLUETOOTH_SCAN_CODE = 102;
    Button button0;
    BluetoothAdapter bluetoothAdapter;
    BluetoothLeScanner bluetoothLeScanner;
    LeDeviceListAdapter leDeviceListAdapter;
//    final BluetoothManager bluetoothManager =
//            (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//    bluetoothAdapter = bluetoothManager.getAdapter();
//BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    //BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
    //bluetoothAdapter = bluetoothManager.getAdapter();
    private boolean scanning;
    private Handler handler = new Handler();
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    //LeDeviceListAdapter leDeviceListAdapter = new LeDeviceListAdapter();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // o panagiotis leei apo katw na grafw kwdika.

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        leDeviceListAdapter = new LeDeviceListAdapter();
        button0 = findViewById(R.id.button0);
        button0.setOnClickListener(view -> {
            if (areAllPermissionsGranted()) {
                Toast.makeText(MainActivity.this, "All Permission already granted", Toast.LENGTH_SHORT).show();
            } else {
                askAllPermissions();
            }
            FindDevices();
        });
    }
    public void FindDevices(){
        // Device scan callback.
        ScanCallback leScanCallback =
        new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                Log.d("Result", String.valueOf(result.getDevice()));
                Log.d("Result", String.valueOf(result.describeContents()));
                Log.d("Result", String.valueOf(result.getRssi()));
                Log.d("Result", result.toString());

                //leDeviceListAdapter.addDevice(result.getDevice());
                //leDeviceListAdapter.notifyDataSetChanged();
            }
        };


        //Scan Process
        if (!scanning) {
            // Stops scanning after a predefined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    bluetoothLeScanner.stopScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            scanning = true;
            Log.d("Scan", "...Scan is Working");
            bluetoothLeScanner.startScan(leScanCallback);
        } else {
            Log.d("Scan", "...Scan stop scannnig? wtf?");
            scanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
        }
    }
    public void askAllPermissions() {
        ArrayList<String> permissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkAndAdd(permissionsList, Manifest.permission.BLUETOOTH_SCAN);
            checkAndAdd(permissionsList, Manifest.permission.BLUETOOTH_CONNECT);
            checkAndAdd(permissionsList, Manifest.permission.BLUETOOTH_ADVERTISE);
        }
        checkAndAdd(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION);
        checkAndAdd(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (!permissionsList.isEmpty()) {
            String[] temp = new String[permissionsList.size()];
            ActivityCompat.requestPermissions(this, permissionsList.toArray(temp), 903);
        }
    }

    public void checkAndAdd(ArrayList<String> permissionList, String permission) {
        if ((ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED)) {
            permissionList.add(permission);
        }
    }

    public boolean areAllPermissionsGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED)
                    && (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED)
                    && (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_ADVERTISE) == PackageManager.PERMISSION_GRANTED);
        }
        return (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    // This function is called when user accept or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when user is prompt for permission.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 903) {
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; ++i) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MainActivity.this, permissions[i] + " Granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, permissions[i] + " Denied", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;
        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = MainActivity.this.getLayoutInflater();
        }
        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }
        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }
        public void clear() {
            mLeDevices.clear();
        }
        @Override
        public int getCount() {
            return mLeDevices.size();
        }
        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }
        @Override
        public long getItemId(int i) {
            return i;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.activity_main, null); //TODO activity_main ??? einai swsto??
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.app_name); // TODO
            viewHolder.deviceAddress.setText(device.getAddress());
            return view;
        }
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
}