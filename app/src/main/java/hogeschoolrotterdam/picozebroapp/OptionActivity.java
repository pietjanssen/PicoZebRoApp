package hogeschoolrotterdam.picozebroapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.annotation.UiThread;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Array;
import java.security.cert.PKIXRevocationChecker;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class OptionActivity extends BaseActivity {

    private Spinner spinner;
    private Button button_connect;
    private Button button_scan;
    private ArrayList<BluetoothDevice> btDevices = new ArrayList<>();
    private ArrayList<String> btString = new ArrayList<>();
    private BluetoothDevice selectedDevice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        spinner = (Spinner) findViewById(R.id.dropdown_menu);
        button_scan = (Button) findViewById(R.id.button_scan);
        button_connect = (Button) findViewById(R.id.button_connect);

        g.setDataAdapter(new ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_spinner_item, btDevices));

        addListenerOnButtonScan();
        addListenerOnButtonConnect();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    // On click listener for the connect button.
    private void addListenerOnButtonConnect() {
        button_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "Connect clicked");
                if (!g.isConnected() & !g.isConnecting()) {
                    checkSelectedDevice();
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
                g.setTestString("Scan");
                mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();
                filters = new ArrayList<>();

                spinner.setAdapter(null);
                scanLeDevice(true);
            }
        });
    }

    // Check if the user has selected any item in the spinner. Only then the app can try to connect to a device.
    private void checkSelectedDevice(){
        selectedDevice = (BluetoothDevice) spinner.getSelectedItem();
        if(selectedDevice == null){
            Toast.makeText(this, "Scan and select a device in the dropdown menu", Toast.LENGTH_SHORT).show();
        }
        else {
            connectToDevice(selectedDevice);
        }
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLEScanner.stopScan(mScanCallback);
                    Log.d("SCAN", "Stopped scan");
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
    // This way the spinner won't be cluttered with dozens of MAC adresses
    private void checkDevicesinSpinner(BluetoothDevice device) {
        boolean present = false;
        String deviceAddress = device.getAddress();
        //if (deviceAddress.equals("00:07:80:E7:B5:7D")) {                      // Currently the mac address of our debugging PicoZebRo
            for (String bd : btString) {
                if (bd.equals(deviceAddress)) {
                    present = true;
                    Log.d("DUP", "Device already in spinner");
                }
            }
            if (!present) {
                btDevices.add(device);
                btString.add(deviceAddress);
                Log.d("ADD", deviceAddress);
            }
        //}
        g.setDataAdapter(new ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_spinner_item, btDevices));
    }

    // Callback for the scan
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("callbackType", String.valueOf(callbackType));
            Log.i("result", result.toString());
            checkDevicesinSpinner(result.getDevice());
            spinner.setAdapter(null);
            spinner.setAdapter(g.getDataAdapter());
            //g.setBtDevice(result.getDevice());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("onLeScan", device.toString());
                            checkDevicesinSpinner(device);
                            //btDevice = device;
                        }
                    });
                }
            };

    final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    g.setConnecting(false);
                    g.setConnected(true);
                    gatt.discoverServices();
                    try {
                        btSocket = createBluetoothSocket(g.getBtDevice());
                    } catch (IOException e){
                        Log.e("ERROR", "Failed to create socket" + e);
                    }
                    try{
                        btSocket.connect();
                    } catch (IOException e){
                        try {
                            btSocket.close();
                        }
                        catch(IOException e2){
                            Log.e("ERROR2", "Error when closing socket connection" + e2);
                        }
                        Log.e("ERROR", "Error when creating socket connection" + e);
                    }
                    mConnectedThread = new ConnectedThread(btSocket);
                    mConnectedThread.start();
                    Log.d("DEBUG", "BluetoothThread started");

                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    g.setConnecting(false);
                    g.setConnected(false);
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
                    g.setConnecting(false);
                    g.setConnected(false);
            }
        }
    };

    // To make sure the user can't try to connect to multiple devices at once, the button is disabled once pressed.
    private void connectToDevice(BluetoothDevice device) {
        g.setTestString("Connect");
        if (!g.isConnected() & !g.isConnecting()) {
            g.setConnecting(true);
            g.setBtDevice(device);
            g.setmGatt(device.connectGatt(this, false, gattCallback));
        } else {
            Toast.makeText(this, "Already connecting to a device", Toast.LENGTH_SHORT).show();
        }
    }

    // To make sure the user can't try to disconnect mutiple times from the same device, the button is disabled once pressed.
    private void disconnectFromDevice(BluetoothDevice device){
        g.setTestString("Disconnect");
        if (g.isConnecting() | g.isConnected()){
            g.getmGatt().close();
            g.setConnected(false);
            g.setConnecting(false);
        }
        else
            Toast.makeText(this, "Can't disconnect from device", Toast.LENGTH_SHORT).show();
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
