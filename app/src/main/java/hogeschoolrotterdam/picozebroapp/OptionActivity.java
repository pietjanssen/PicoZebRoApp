package hogeschoolrotterdam.picozebroapp;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class OptionActivity extends BaseActivity {

    private Spinner spinner;
    private Button button_connect;
    private Button button_scan;
    private ArrayList<BluetoothDevice> btDevices = new ArrayList<>();
    private ArrayList<String> btString = new ArrayList<>();
    private BluetoothDevice selectedDevice;
    private ProgressBar loadingscan;
    private TextView textview_status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
        spinner = (Spinner) findViewById(R.id.dropdown_menu);
        button_scan = (Button) findViewById(R.id.button_scan);
        button_connect = (Button) findViewById(R.id.button_connect);
        loadingscan = (ProgressBar) findViewById(R.id.loading_scan);
        textview_status = (TextView) findViewById(R.id.textview_status);

        g.setDataAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, btString));

        addListenerOnButtonScan();
        addListenerOnButtonConnect();

        mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
        settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        filters = new ArrayList<>();
    }

    @Override
    public void onResume(){
        super.onResume();
        if (g.getBtDevice() != null) {
            showStatus(R.string.status_connected);
            changeButton(R.string.button_disconnect);
            btString.add(g.getBtDevice().getName());
            spinner.setAdapter(null);
            spinner.setAdapter(g.getDataAdapter());
        }
        else{
            showStatus(R.string.status_scanning);
            loadingscan.setVisibility(View.VISIBLE);
            scanLeDevice(true);
        }
    }

    // On click listener for the connect button.
    private void addListenerOnButtonConnect() {
        button_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "Connect clicked");
                if (!g.isConnected() & !g.isConnecting()) {
                    checkSelectedDevice(spinner.getSelectedItemPosition());
                } else {
                    disconnectFromDevice(selectedDevice);
                }
            }
        });
    }

    // On click listener for the scan button.
    private void addListenerOnButtonScan() {
        button_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "Scan clicked");
                spinner.setAdapter(null);
                showStatus(R.string.status_scanning);
                loadingscan.setVisibility(View.VISIBLE);
                scanLeDevice(true);
            }
        });
    }

    // Check if the user has selected any item in the spinner. Only then the app can try to connect to a device.
    private void checkSelectedDevice(int spinnerPosition){
        if(spinnerPosition >= 0)
            if(btDevices.size() > 0)
                selectedDevice = (BluetoothDevice) btDevices.get(spinnerPosition);
            else
                selectedDevice = g.getBtDevice();
        else {
            Toast.makeText(this, "Scan and select a device in the dropdown menu", Toast.LENGTH_SHORT).show();
            Log.d("OPTION DEBUG", "spinner = 0");
            return;
        }
            connectToDevice(selectedDevice);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLEScanner.stopScan(mScanCallback);
                    Log.d("SCAN", "Stopped scan");
                    loadingscan.setVisibility(View.INVISIBLE);
                    showStatus(R.string.status_scanningstopped);
                }
            }, SCAN_PERIOD);

            mLEScanner.startScan(filters, settings, mScanCallback);
            Log.d("SCAN", "Started Scan");
        } else {
            mLEScanner.stopScan(mScanCallback);
            Log.i("SCAN", "Stopped scan");
        }
    }

    // This function is used to filter out any duplicates in the spinner.
    // This way the spinner won't be cluttered with dozens of MAC addresses / Names
    private void checkDevicesinSpinner(BluetoothDevice device) {
        boolean present = false;
        String deviceName = device.getName();
        //if (deviceName.equals("00:07:80:E7:B5:7D")) {                      // Currently the mac address of our debugging PicoZebRo
        if(deviceName != null){
            for (String bd : btString) {
                if (bd.equals(deviceName)) {
                    present = true;
                    Log.d("DUP", "Device already in spinner");
                }
            }
            if (!present) {
                btDevices.add(device);
                btString.add(deviceName);
                Log.d("ADD", deviceName);
            }
        }
        g.setDataAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, btString));
    }

    // Callback for the scan
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("result", result.toString());
            checkDevicesinSpinner(result.getDevice());
            spinner.setAdapter(null);
            spinner.setAdapter(g.getDataAdapter());
            //g.setBtDevice(result.getDevice());
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    g.setConnecting(false);
                    g.setConnected(true);
                    gatt.discoverServices();
                    showStatus(R.string.status_connected);
                    changeButton(R.string.button_disconnect);
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    g.setConnecting(false);
                    g.setConnected(false);
                    showStatus(R.string.status_disconnected);
                    changeButton(R.string.button_connect);
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
                    g.setConnecting(false);
                    g.setConnected(false);
                    showStatus(R.string.status_connection_failure);
                    changeButton(R.string.button_connect);
            }
        }
    };

    // To make sure the user can't try to connect to multiple devices at once, the button is disabled once pressed.
    private void connectToDevice(BluetoothDevice device) {
        if (!g.isConnected() & !g.isConnecting()) {
            g.setConnecting(true);
            g.setBtDevice(device);
            g.setmGatt(device.connectGatt(this, false, gattCallback));
            loadingscan.setVisibility(View.VISIBLE);
            showStatus(R.string.status_connecting);
        } else {
            Toast.makeText(this, "Already connecting to a device", Toast.LENGTH_SHORT).show();
        }
    }

    private void notifyToLEDs(){
        g.getmGatt().setCharacteristicNotification(g.getmGatt().getService(SERVICEUUID_L).getCharacteristic(CHARACTERUUID_L_1), true);
        g.getmGatt().setCharacteristicNotification(g.getmGatt().getService(SERVICEUUID_L).getCharacteristic(CHARACTERUUID_L_2), true);
        g.getmGatt().setCharacteristicNotification(g.getmGatt().getService(SERVICEUUID_L).getCharacteristic(CHARACTERUUID_L_3), true);
        g.getmGatt().setCharacteristicNotification(g.getmGatt().getService(SERVICEUUID_L).getCharacteristic(CHARACTERUUID_L_4), true);
        g.getmGatt().setCharacteristicNotification(g.getmGatt().getService(SERVICEUUID_L).getCharacteristic(CHARACTERUUID_L_5), true);
        g.getmGatt().setCharacteristicNotification(g.getmGatt().getService(SERVICEUUID_L).getCharacteristic(CHARACTERUUID_L_6), true);
    }

    // To make sure the user can't try to disconnect mutiple times from the same device, the button is disabled once pressed.
    private void disconnectFromDevice(BluetoothDevice device){
        if (g.isConnecting() | g.isConnected()){
            g.getmGatt().close();
            g.setConnected(false);
            g.setConnecting(false);
            button_connect.setText("Connect");
        }
        else
            Toast.makeText(this, "Can't disconnect from device", Toast.LENGTH_SHORT).show();
    }

    private void showStatus(final int StrId){
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                textview_status.setText(StrId);
                loadingscan.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void changeButton(final int StrId){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button_connect.setText(StrId);
            }
        });
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_option;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.menu_option;
    }
}
