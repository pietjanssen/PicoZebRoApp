package hogeschoolrotterdam.picozebroapp;


import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.UUID;

public class ValueActivity extends BaseActivity {
    @Override
    int getContentViewId() {
        return R.layout.activity_value;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.menu_value;
    }

    private TextView textview_value_status;
    private ToggleButton buttonLED1_R;
    private ToggleButton buttonLED2_R;
    private ToggleButton buttonLED3_R;
    private ToggleButton buttonLED4_R;
    private ToggleButton buttonLED5_R;
    private ToggleButton buttonLED6_R;

    private ToggleButton buttonLED1_G;
    private ToggleButton buttonLED2_G;
    private ToggleButton buttonLED3_G;
    private ToggleButton buttonLED4_G;
    private ToggleButton buttonLED5_G;
    private ToggleButton buttonLED6_G;

    private ToggleButton buttonLED1_B;
    private ToggleButton buttonLED2_B;
    private ToggleButton buttonLED3_B;
    private ToggleButton buttonLED4_B;
    private ToggleButton buttonLED5_B;
    private ToggleButton buttonLED6_B;

    private byte led1_rgb;
    private byte led2_rgb;
    private byte led3_rgb;
    private byte led4_rgb;
    private byte led5_rgb;
    private byte led6_rgb;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());

        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);

        textview_value_status = (TextView) findViewById(R.id.textview_status);

        buttonLED1_R = (ToggleButton) findViewById(R.id.button_led1_r);
        buttonLED2_R = (ToggleButton) findViewById(R.id.button_led2_r);
        buttonLED3_R = (ToggleButton) findViewById(R.id.button_led3_r);
        buttonLED4_R = (ToggleButton) findViewById(R.id.button_led4_r);
        buttonLED5_R = (ToggleButton) findViewById(R.id.button_led5_r);
        buttonLED6_R = (ToggleButton) findViewById(R.id.button_led6_r);

        buttonLED1_G = (ToggleButton) findViewById(R.id.button_led1_g);
        buttonLED2_G = (ToggleButton) findViewById(R.id.button_led2_g);
        buttonLED3_G = (ToggleButton) findViewById(R.id.button_led3_g);
        buttonLED4_G = (ToggleButton) findViewById(R.id.button_led4_g);
        buttonLED5_G = (ToggleButton) findViewById(R.id.button_led5_g);
        buttonLED6_G = (ToggleButton) findViewById(R.id.button_led6_g);

        buttonLED1_B = (ToggleButton) findViewById(R.id.button_led1_b);
        buttonLED2_B = (ToggleButton) findViewById(R.id.button_led2_b);
        buttonLED3_B = (ToggleButton) findViewById(R.id.button_led3_b);
        buttonLED4_B = (ToggleButton) findViewById(R.id.button_led4_b);
        buttonLED5_B = (ToggleButton) findViewById(R.id.button_led5_b);
        buttonLED6_B = (ToggleButton) findViewById(R.id.button_led6_b);

        buttonLED1_R_onClick();
        buttonLED2_R_onClick();
        buttonLED3_R_onClick();
        buttonLED4_R_onClick();
        buttonLED5_R_onClick();
        buttonLED6_R_onClick();

        buttonLED1_G_onClick();
        buttonLED2_G_onClick();
        buttonLED3_G_onClick();
        buttonLED4_G_onClick();
        buttonLED5_G_onClick();
        buttonLED6_G_onClick();

        buttonLED1_B_onClick();
        buttonLED2_B_onClick();
        buttonLED3_B_onClick();
        buttonLED4_B_onClick();
        buttonLED5_B_onClick();
        buttonLED6_B_onClick();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(g.isConnected()){
            showStatus(R.string.status_connected);
            updateButtons();
        }
        else{
            showStatus(R.string.status_disconnected);
        }
    }

    private void showStatus(final int StrId){
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                textview_value_status.setText(StrId);
            }
        });
    }

    private void updateButtons(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                led1_rgb = readCharacteristic(SERVICEUUID_L, CHARACTERUUID_L_1)[0];
                led2_rgb = readCharacteristic(SERVICEUUID_L, CHARACTERUUID_L_2)[0];
                led3_rgb = readCharacteristic(SERVICEUUID_L, CHARACTERUUID_L_3)[0];
                led4_rgb = readCharacteristic(SERVICEUUID_L, CHARACTERUUID_L_4)[0];
                led5_rgb = readCharacteristic(SERVICEUUID_L, CHARACTERUUID_L_5)[0];
                led6_rgb = readCharacteristic(SERVICEUUID_L, CHARACTERUUID_L_6)[0];

                if((led1_rgb | 0b11111011) == 0xFF)
                    buttonLED1_R.setChecked(true);
                if((led1_rgb | 0b11111101) == 0xFF)
                    buttonLED1_G.setChecked(true);
                if((led1_rgb | 0b11111110) == 0xFF)
                    buttonLED1_B.setChecked(true);

                if((led2_rgb | 0b11111011) == 0xFF)
                    buttonLED2_R.setChecked(true);
                if((led2_rgb | 0b11111101) == 0xFF)
                    buttonLED2_G.setChecked(true);
                if((led2_rgb | 0b11111110) == 0xFF)
                    buttonLED2_B.setChecked(true);

                if((led3_rgb | 0b11111011) == 0xFF)
                    buttonLED3_R.setChecked(true);
                if((led3_rgb | 0b11111101) == 0xFF)
                    buttonLED3_G.setChecked(true);
                if((led3_rgb | 0b11111110) == 0xFF)
                    buttonLED3_B.setChecked(true);

                if((led4_rgb | 0b11111011) == 0xFF)
                    buttonLED4_R.setChecked(true);
                if((led4_rgb | 0b11111101) == 0xFF)
                    buttonLED4_G.setChecked(true);
                if((led4_rgb | 0b11111110) == 0xFF)
                    buttonLED4_B.setChecked(true);

                if((led5_rgb | 0b11111011) == 0xFF)
                    buttonLED5_R.setChecked(true);
                if((led5_rgb | 0b11111101) == 0xFF)
                    buttonLED5_G.setChecked(true);
                if((led5_rgb | 0b11111110) == 0xFF)
                    buttonLED5_B.setChecked(true);

                if((led6_rgb | 0b11111011) == 0xFF)
                    buttonLED6_R.setChecked(true);
                if((led6_rgb | 0b11111101) == 0xFF)
                    buttonLED6_G.setChecked(true);
                if((led6_rgb | 0b11111110) == 0xFF)
                    buttonLED6_B.setChecked(true);
            }
        });
    }

    private void writeCharacteristic(UUID service , UUID characteristic, int newnumber){
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
        value[0] = (byte) (newnumber & 0xFF);
        charac.setValue(value);
        g.getmGatt().writeCharacteristic(charac);
    }

    private void buttonLED1_R_onClick() {
        buttonLED1_R.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "LED1_R clicked");
                led1_rgb ^= 0b100;
                writeCharacteristic(SERVICEUUID_L, CHARACTERUUID_L_1, led1_rgb);
            }
        });
    }

    private void buttonLED2_R_onClick() {
        buttonLED2_R.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "LED2_R clicked");
                led2_rgb ^= 0b100;
                writeCharacteristic(SERVICEUUID_L, CHARACTERUUID_L_2, led2_rgb);
            }
        });
    }

    private void buttonLED3_R_onClick() {
        buttonLED3_R.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "LED3_R clicked");
                led3_rgb ^= 0b100;
                writeCharacteristic(SERVICEUUID_L, CHARACTERUUID_L_3, led3_rgb);
            }
        });
    }

    private void buttonLED4_R_onClick() {
        buttonLED4_R.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "LED4_R clicked");
                led4_rgb ^= 0b100;
                writeCharacteristic(SERVICEUUID_L, CHARACTERUUID_L_4, led4_rgb);
            }
        });
    }

    private void buttonLED5_R_onClick() {
        buttonLED5_R.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "LED5_R clicked");
                led5_rgb ^= 0b100;
                writeCharacteristic(SERVICEUUID_L, CHARACTERUUID_L_5, led5_rgb);
            }
        });
    }

    private void buttonLED6_R_onClick() {
        buttonLED6_R.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "LED6_R clicked");
                led6_rgb ^= 0b100;
                writeCharacteristic(SERVICEUUID_L, CHARACTERUUID_L_6, led6_rgb);
            }
        });
    }

    private void buttonLED1_G_onClick() {
        buttonLED1_G.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "LED1_G clicked");
                led1_rgb ^= 0b010;
                writeCharacteristic(SERVICEUUID_L, CHARACTERUUID_L_1, led1_rgb);
            }
        });
    }

    private void buttonLED2_G_onClick() {
        buttonLED2_G.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "LED2_G clicked");
                led2_rgb ^= 0b010;
                writeCharacteristic(SERVICEUUID_L, CHARACTERUUID_L_2, led2_rgb);
            }
        });
    }

    private void buttonLED3_G_onClick() {
        buttonLED3_G.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "LED3_G clicked");
                led3_rgb ^= 0b010;
                writeCharacteristic(SERVICEUUID_L, CHARACTERUUID_L_3, led3_rgb);
            }
        });
    }

    private void buttonLED4_G_onClick() {
        buttonLED4_G.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "LED4_G clicked");
                led4_rgb ^= 0b010;
                writeCharacteristic(SERVICEUUID_L, CHARACTERUUID_L_4, led4_rgb);
            }
        });
    }

    private void buttonLED5_G_onClick() {
        buttonLED5_G.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "LED5_G clicked");
                led5_rgb ^= 0b010;
                writeCharacteristic(SERVICEUUID_L, CHARACTERUUID_L_5, led5_rgb);
            }
        });
    }

    private void buttonLED6_G_onClick() {
        buttonLED6_G.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "LED6_G clicked");
                led6_rgb ^= 0b010;
                writeCharacteristic(SERVICEUUID_L, CHARACTERUUID_L_6, led6_rgb);
            }
        });
    }

    private void buttonLED1_B_onClick() {
        buttonLED1_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "LED1_B clicked");
                led1_rgb ^= 0b001;
                writeCharacteristic(SERVICEUUID_L, CHARACTERUUID_L_1, led1_rgb);
            }
        });
    }

    private void buttonLED2_B_onClick() {
        buttonLED2_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "LED2_B clicked");
                led2_rgb ^= 0b001;
                writeCharacteristic(SERVICEUUID_L, CHARACTERUUID_L_2, led2_rgb);
            }
        });
    }

    private void buttonLED3_B_onClick() {
        buttonLED3_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "LED3_B clicked");
                led3_rgb ^= 0b001;
                writeCharacteristic(SERVICEUUID_L, CHARACTERUUID_L_3, led3_rgb);
            }
        });
    }

    private void buttonLED4_B_onClick() {
        buttonLED4_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "LED4_B clicked");
                led4_rgb ^= 0b001;
                writeCharacteristic(SERVICEUUID_L, CHARACTERUUID_L_4, led4_rgb);
            }
        });
    }

    private void buttonLED5_B_onClick() {
        buttonLED5_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "LED5_B clicked");
                led5_rgb ^= 0b001;
                writeCharacteristic(SERVICEUUID_L, CHARACTERUUID_L_5, led5_rgb);
            }
        });
    }

    private void buttonLED6_B_onClick() {
        buttonLED6_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TEST", "LED6_B clicked");
                led6_rgb ^= 0b001;
                writeCharacteristic(SERVICEUUID_L, CHARACTERUUID_L_6, led6_rgb);
            }
        });
    }
}
