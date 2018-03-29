package hogeschoolrotterdam.picozebroapp;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.widget.ArrayAdapter;

public class Globals {
    private static Globals instance;
    private Globals(){}

    private BluetoothGatt mGatt;
    private BluetoothDevice btDevice;
    private ArrayAdapter<BluetoothDevice> dataAdapter;
    private String dataInPrint;
    private boolean Connecting;
    private boolean Connected;
    private String testString;                              // This string is used in other Activities to check if certain valuables are correctly read from Globals

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

    public String getTestString() {
        return testString;
    }

    public void setTestString(String testString) {
        this.testString = testString;
    }

    public String getDataInPrint() {
        return dataInPrint;
    }

    public void setDataInPrint(String dataInPrint) {
        this.dataInPrint = dataInPrint;
    }

    public ArrayAdapter<BluetoothDevice> getDataAdapter() {
        return dataAdapter;
    }

    public void setDataAdapter(ArrayAdapter<BluetoothDevice> dataAdapter) {
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
