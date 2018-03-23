package hogeschoolrotterdam.picozebroapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.ParcelUuid;
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

/**
 * Created by Boris van Norren on 21-2-2018.
 */

public class OptionActivity extends BaseActivity {

    private Spinner spinner;
    private Button button_connect;
    private Button button_scan;
    private BluetoothDevice btDevice;
    private ArrayList<BluetoothDevice> btDevices = new ArrayList<>();
    private ArrayList<String> btString = new ArrayList<>();
    private ArrayAdapter<BluetoothDevice> dataAdapter;
    private OptionActivity that;
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

        dataAdapter = new ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_spinner_item, btDevices);

        addListenerOnButtonScan();
        addListenerOnButtonConnect();
    }

    private void addListenerOnButtonConnect() {
        button_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "Connect clicked");
                if (Build.VERSION.SDK_INT >= 21) {
                    if( mGatt == null){
                        checkSelectedDevice();
                    }
                    else{
                        disconnectFromDevice(selectedDevice);
                    }
                }
            }
        });
    }

    private void addListenerOnButtonScan() {
        button_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "Scan clicked");
                if (Build.VERSION.SDK_INT >= 21) {
                    mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                    settings = new ScanSettings.Builder()
                            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                            .build();
                    filters = new ArrayList<>();
                }
                spinner.setAdapter(null);
                scanLeDevice(true);
            }
        });
    }

    private void checkSelectedDevice(){
        selectedDevice = (BluetoothDevice) spinner.getSelectedItem();
        if(selectedDevice == null){
            Toast.makeText(this, "Scan and select a device in the dropdown menu", Toast.LENGTH_SHORT).show();
        }
        else {
            connectToDevice(selectedDevice);
        }
    }

    protected void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT < 21) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    } else {
                        mLEScanner.stopScan(mScanCallback);
                        Log.d("SCAN", "Stopped scan");
                    }
                }
            }, SCAN_PERIOD);
            if (Build.VERSION.SDK_INT < 21) {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                mLEScanner.startScan(filters, settings, mScanCallback);
                Log.d("SCAN", "Started Scan");
            }
        } else {
            if (Build.VERSION.SDK_INT < 21) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            } else {
                mLEScanner.stopScan(mScanCallback);
                Log.i("SCAN", "Stopped scan");
            }
        }
    }

    private void checkDevicesinSpinner(BluetoothDevice device) {
        boolean present = false;
        String deviceAddress = device.getAddress();
        if (deviceAddress.equals("00:07:80:E7:B5:7D")) {
            for (String bd : btString) {
                Log.d("BD", bd);
                Log.d("DEVICE", deviceAddress);
                if (bd.equals(deviceAddress)) {
                    present = true;
                    Log.d("DUP", "Device already in spinner");
                }
            }
            if (present == false) {
                btDevices.add(device);
                btString.add(deviceAddress);
                Log.d("ADD", deviceAddress);
            }
        }
        dataAdapter = new ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_spinner_item, btDevices);
    }

    protected ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("callbackType", String.valueOf(callbackType));
            Log.i("result", result.toString());
            checkDevicesinSpinner(result.getDevice());
            spinner.setAdapter(null);
            spinner.setAdapter(dataAdapter);
            btDevice = result.getDevice();
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

    protected BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("onLeScan", device.toString());
                            checkDevicesinSpinner(device);
                            btDevice = device;
                        }
                    });
                }
            };

    public void connectToDevice(BluetoothDevice device) {
        if (mGatt == null) {
            mGatt = device.connectGatt(this, false, gattCallback);
            button_connect.setText("Disconnect");
        } else {
            Toast.makeText(this, "Already connecting to a device", Toast.LENGTH_SHORT).show();
        }
    }

    public void disconnectFromDevice(BluetoothDevice device){
        if (mGatt != null){
            mGatt.disconnect();
            button_connect.setText("Connect");
        }
        else
            Toast.makeText(this, "Not connected to a device yet", Toast.LENGTH_SHORT).show();
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
