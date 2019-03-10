package com.example.bluetoothscanner;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class BluetoothActivity extends AppCompatActivity {
    //private static final String
    BluetoothAdapter bluetooth;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;
    ListView lvNewDevices;
    TextView title;

    /*
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals(bluetooth.ACTION_STATE_CHANGED)){
                final int state = intent.getIntExtra(bluetooth.EXTRA_STATE, bluetooth.ERROR);

                switch (state){
                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(getApplicationContext(), "broadcastReceiver : STATE_OFF",
                                Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Toast.makeText(getApplicationContext(), "broadcastReceiver : STATE_TURNING_OFF",
                                Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(getApplicationContext(), "broadcastReceiver : STATE_ON",
                                Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Toast.makeText(getApplicationContext(), "broadcastReceiver : STATE_TURNING_ON",
                                Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }
    };
    */

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(mBTDevices.size()<1) // this checks if the size of bluetooth device is 0,then add the
                {                                           // device to the arraylist.
                    mBTDevices.add(device);
                }
                else
                {
                    boolean flag = true;    // flag to indicate that particular device is already in the arraylist or not
                    for(int i = 0; i<mBTDevices.size();i++)
                    {
                        if(device.getAddress().equals(mBTDevices.get(i).getAddress()))
                        {
                            flag = false;
                        }
                    }
                    if(flag == true)
                    {
                        mBTDevices.add(device);
                    }
                }
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        lvNewDevices = (ListView) findViewById(R.id.lvNewDevices);
        title = (TextView) findViewById(R.id.tvTitle);
        mBTDevices = new ArrayList<>();

        bluetooth = BluetoothAdapter.getDefaultAdapter();

        if(bluetooth == null){
            Toast.makeText(getApplicationContext(), "Votre appareil ne supporte pas le Bluetooth",
                    Toast.LENGTH_LONG).show();
        }

        if(!bluetooth.isEnabled()){
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBT);

            IntentFilter scanDevices = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(receiver, scanDevices);
            /*
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(broadcastReceiver, BTIntent);
            */
        }

        if(bluetooth.isEnabled()){
            IntentFilter scanDevices = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(receiver, scanDevices);
            /*
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(broadcastReceiver, BTIntent);
            */
        }

        if(bluetooth.isDiscovering()){
            bluetooth.cancelDiscovery();

            checkBTPermissions();

            bluetooth.startDiscovery();
            IntentFilter scanDevices = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(receiver, scanDevices);
        }

        if(!bluetooth.isDiscovering()){
            checkBTPermissions();

            bluetooth.startDiscovery();
            IntentFilter scanDevices = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(receiver, scanDevices);
        }
    }

    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = 0;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            }

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            }

            if(permissionCheck != 0){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 2000);
                }
            }
        } else {
        }
    }
}
