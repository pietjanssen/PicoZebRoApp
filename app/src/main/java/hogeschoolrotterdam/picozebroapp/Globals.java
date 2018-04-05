package hogeschoolrotterdam.picozebroapp;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.widget.ArrayAdapter;

public class Globals {
    private static Globals instance;
    private Globals(){}

    private BluetoothGatt mGatt;
    private BluetoothDevice btDevice;
    private ArrayAdapter<String> dataAdapter;
    private boolean Connecting;
    private boolean Connected;

    public boolean isConnecting() {
        return Connecting;
    }

    public void setConnecting(boolean connecting) {
        Connecting = connecting;
    }

    public boolean isConnected() {
        return Connected;
    }

    public void setConnected(boolean connected) {
        Connected = connected;
    }

    public ArrayAdapter<String> getDataAdapter() {
        return dataAdapter;
    }

    public void setDataAdapter(ArrayAdapter<String> dataAdapter) {
        this.dataAdapter = dataAdapter;
    }

    public BluetoothGatt getmGatt() {
        return mGatt;
    }

    public void setmGatt(BluetoothGatt mGatt) {
        this.mGatt = mGatt;
    }

    public BluetoothDevice getBtDevice() {
        return btDevice;
    }

    public void setBtDevice(BluetoothDevice btDevice) {
        this.btDevice = btDevice;
    }

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance = new Globals();
        }
        return instance;
    }


}
