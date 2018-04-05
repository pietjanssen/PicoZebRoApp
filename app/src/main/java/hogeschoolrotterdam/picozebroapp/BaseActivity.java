package hogeschoolrotterdam.picozebroapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

public abstract class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    protected BottomNavigationView navigationView;
    protected BluetoothAdapter mBluetoothAdapter;
    protected Handler mHandler;
    protected BluetoothLeScanner mLEScanner;
    protected ScanSettings settings;
    protected List<ScanFilter> filters;
    public Globals g;
    protected static final int REQUEST_ENABLE_BT = 1;
    protected static final long SCAN_PERIOD = 5000;
    protected static final UUID SERVICEUUID_M = UUID.fromString("65656565-6565-6565-0000-000000000000"); // Movement Service
    protected static final UUID SERVICEUUID_B = UUID.fromString("65656565-6565-6565-0001-000000000000"); // Battery Service
    protected static final UUID SERVICEUUID_L = UUID.fromString("65656565-6565-6565-0002-000000000000"); // LED Service
    protected static final UUID SERVICEUUID_C = UUID.fromString("65656565-6565-6565-0003-000000000000"); // Colour sensor Service

    protected static final UUID CHARACTERUUID_M_1 = UUID.fromString("65656565-6565-6565-0000-000000000001");

    protected static final UUID CHARACTERUUID_B_1 = UUID.fromString("65656565-6565-6565-0001-000000000001");
    protected static final UUID CHARACTERUUID_B_2 = UUID.fromString("65656565-6565-6565-0001-000000000002");

    protected static final UUID CHARACTERUUID_L_1 = UUID.fromString("65656565-6565-6565-0002-000000000001");
    protected static final UUID CHARACTERUUID_L_2 = UUID.fromString("65656565-6565-6565-0002-000000000002");
    protected static final UUID CHARACTERUUID_L_3 = UUID.fromString("65656565-6565-6565-0002-000000000003");
    protected static final UUID CHARACTERUUID_L_4 = UUID.fromString("65656565-6565-6565-0002-000000000004");
    protected static final UUID CHARACTERUUID_L_5 = UUID.fromString("65656565-6565-6565-0002-000000000005");
    protected static final UUID CHARACTERUUID_L_6 = UUID.fromString("65656565-6565-6565-0002-000000000006");

    protected static final UUID CHARACTERUUID_C_1 = UUID.fromString("65656565-6565-6565-0002-000000000001");
    protected static final UUID CHARACTERUUID_C_2 = UUID.fromString("65656565-6565-6565-0002-000000000002");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());

        mHandler = new Handler();
        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if(bluetoothManager != null)
            mBluetoothAdapter = bluetoothManager.getAdapter();
        else
            Log.e("ERROR", "Bluetoothmanager == NULL");

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE NOT SUPPORTED", Toast.LENGTH_SHORT).show();
            finish();
        }

        g = Globals.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateNavigationBarState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    // Remove inter-activity transition to avoid screen tossing on tapping bottom navigation items
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_CANCELED) {
                //Bluetooth not enabled.
                finish();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        navigationView.postDelayed(() -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                startActivity(new Intent(this, HomeActivity.class));
            } else if (itemId == R.id.menu_camera) {
                startActivity(new Intent(this, CameraActivity.class));
            } else if (itemId == R.id.menu_charge) {
                startActivity(new Intent(this, ChargeActivity.class));
            } else if (itemId == R.id.menu_option) {
                startActivity(new Intent(this, OptionActivity.class));
            } else if (itemId == R.id.menu_value) {
                startActivity(new Intent(this, ValueActivity.class));
            }
            finish();
        }, 300);
        return true;
    }

    private void updateNavigationBarState(){
        int actionId = getNavigationMenuItemId();
        selectBottomNavigationBarItem(actionId);
    }

    private void selectBottomNavigationBarItem(int itemId) {
        Menu menu = navigationView.getMenu();
        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            boolean shouldBeChecked = item.getItemId() == itemId;
            if (shouldBeChecked) {
                item.setChecked(true);
                break;
            }
        }
    }

    protected byte[] readCharacteristic(UUID service, UUID characteristic){
        if (g.getmGatt() != null) {
            BluetoothGattService serv = g.getmGatt().getService(service);
            BluetoothGattCharacteristic charac = serv.getCharacteristic(characteristic);
            g.getmGatt().readCharacteristic(charac);
            byte[] value = charac.getValue();
            return value;
        }
        else
            return null;
    }

    abstract int getContentViewId();

    abstract int getNavigationMenuItemId();

}

