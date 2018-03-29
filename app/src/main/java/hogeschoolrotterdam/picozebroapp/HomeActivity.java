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
    private TextView bluetoothOutput;
    private Thread t;
    private TextView lastActionOutput;

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
        bluetoothOutput = (TextView) findViewById(R.id.bluetooth_output);
        lastActionOutput = (TextView) findViewById(R.id.action_output);

        addListenerOnButtonUp();
        addListenerOnButtonDown();
        addListenerOnButtonLeft();
        addListenerOnButtonRight();
        addListenerOnButtonStop();
        addListenerOnButtonCharge();

        t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateTextView();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
    }

    private void updateTextView(){
        if(g.getDataInPrint() != null) {
            bluetoothOutput.setText(g.getDataInPrint());
        }else{
            bluetoothOutput.setText("Nothing yet.");
        }
        if(g.getTestString() != null){
            lastActionOutput.setText(g.getTestString());
        }else{
            lastActionOutput.setText("Nothing yet");
        }
    }

    @Override
    public void onResume(){
        super.onResume();
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
                g.setTestString("UP");
                if (mBluetoothAdapter.isEnabled()) {
                    if (g.getmGatt() != null & !g.getmGatt().getConnectedDevices().isEmpty()) {
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
                g.setTestString("DOWN");
            }
        });
    }

    private void addListenerOnButtonLeft() {
        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "LEFT clicked");
                g.setTestString("LEFT");
            }
        });
    }

    private void addListenerOnButtonRight() {
        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "RIGHT clicked");
                g.setTestString("RIGHT");
            }
        });
    }

    private void addListenerOnButtonStop() {
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "STOP clicked");
                g.setTestString("STOP");
            }
        });
    }

    private void addListenerOnButtonCharge() {
        buttonCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "CHARGE clicked");
                g.setTestString("CHARGE");
            }
        });
    }

}
