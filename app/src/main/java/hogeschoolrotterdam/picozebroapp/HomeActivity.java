package hogeschoolrotterdam.picozebroapp;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.UUID;

public class HomeActivity extends BaseActivity {

    private Button buttonUp;
    private Button buttonDown;
    private Button buttonLeft;
    private Button buttonRight;
    private Button buttonStop;
    private Button buttonCharge;
    private Button buttonnotConnected;

    @Override
    int getContentViewId() {
        return R.layout.activity_home;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.menu_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        buttonUp = (Button) findViewById(R.id.button_up);
        buttonDown = (Button) findViewById(R.id.button_down);
        buttonLeft = (Button) findViewById(R.id.button_left);
        buttonRight = (Button) findViewById(R.id.button_right);
        buttonStop = (Button) findViewById(R.id.button_stop);
        buttonCharge = (Button) findViewById(R.id.button_charge);
        buttonnotConnected = (Button) findViewById(R.id.button_notConnected);

        addListenerOnButtonUp();
        addListenerOnButtonDown();
        addListenerOnButtonLeft();
        addListenerOnButtonRight();
        addListenerOnButtonStop();
        addListenerOnButtonCharge();
        addListenerOnButtonNotConnected();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(g.isConnected()){
            buttonnotConnected.setVisibility(View.INVISIBLE);
            buttonnotConnected.setEnabled(false);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    private void addListenerOnButtonUp() {
        buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "UP clicked");
                writeCharacteristic(SERVICEUUID_M, CHARACTERUUID_M_1, 1);
            }
        });
    }

    private void addListenerOnButtonDown() {
        buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "DOWN clicked");
                writeCharacteristic(SERVICEUUID_M, CHARACTERUUID_M_1, 2);
            }
        });
    }

    private void addListenerOnButtonLeft() {
        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "LEFT clicked");
                writeCharacteristic(SERVICEUUID_M, CHARACTERUUID_M_1, 3);
            }
        });
    }

    private void addListenerOnButtonRight() {
        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "RIGHT clicked");
                writeCharacteristic(SERVICEUUID_M, CHARACTERUUID_M_1, 4);
            }
        });
    }

    private void addListenerOnButtonStop() {
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "STOP clicked");
                writeCharacteristic(SERVICEUUID_M, CHARACTERUUID_M_1, 0);
            }
        });
    }

    private void addListenerOnButtonCharge() {
        buttonCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "CHARGE clicked");
                writeCharacteristic(SERVICEUUID_M, CHARACTERUUID_M_1, 5);
            }
        });
    }

    private void addListenerOnButtonNotConnected() {
        buttonnotConnected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "NOTCONNECTED clicked");
                startActivity(new Intent(view.getContext(), OptionActivity.class));
            }
        });
    }

    private void writeCharacteristic(UUID service , UUID characteristic, int number){
        if (g.getmGatt() == null){
            Log.e("HOME error", "No bluetooth connection");
            return;
        }
        BluetoothGattService serv = g.getmGatt().getService(service);
        if(service == null){
            Log.e("HOME error", "No service found with given UUID");
            return;
        }
        BluetoothGattCharacteristic charac = serv.getCharacteristic(characteristic);
        if(charac == null){
            Log.e("HOME error", "No characteristic found with given service");
            return;
        }

        byte[] value = new byte[1];
        value[0] = (byte) (number & 0xFF);
        charac.setValue(value);
        g.getmGatt().writeCharacteristic(charac);
    }
}
