package hogeschoolrotterdam.picozebroapp;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.Set;

public class HomeActivity extends BaseActivity {

    private Button buttonUp;
    private Button buttonDown;
    private Button buttonLeft;
    private Button buttonRight;
    private Button buttonStop;
    private Button buttonCharge;

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

        addListenerOnButtonUp();
        addListenerOnButtonDown();
        addListenerOnButtonLeft();
        addListenerOnButtonRight();
        addListenerOnButtonStop();
        addListenerOnButtonCharge();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    private void addListenerOnButtonUp() {
        buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "UP clicked");
                if (mBluetoothAdapter.isEnabled()) {
                    if (g.getmGatt() != null) {
                        mConnectedThread.write("1");
                    } else
                        Log.e("MOV", "Device is not connected");
                }
                else
                    Log.e("MOV", "Bluetooth is not enabled");
            }
        });
    }

    private void addListenerOnButtonDown() {
        buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "DOWN clicked");
            }
        });
    }

    private void addListenerOnButtonLeft() {
        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "LEFT clicked");
            }
        });
    }

    private void addListenerOnButtonRight() {
        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "RIGHT clicked");
            }
        });
    }

    private void addListenerOnButtonStop() {
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "STOP clicked");
            }
        });
    }

    private void addListenerOnButtonCharge() {
        buttonCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "CHARGE clicked");
            }
        });
    }

}
