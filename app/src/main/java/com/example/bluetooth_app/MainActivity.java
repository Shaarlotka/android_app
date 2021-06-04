package com.example.bluetooth_app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.bluetoothapp.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private ListView listView;
    private ArrayList<String> pairedDeviceArrayList;
    private ArrayAdapter<String> pairedDeviceAdapter;
    public static BluetoothSocket clientSocket;
    private Button buttonStartControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonStartFind = (Button) findViewById(R.id.button_start_find);
        listView = (ListView) findViewById(R.id.list_device);
        buttonStartFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(permissionGranted()) {
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if(bluetoothEnabled()) {
                        findArduino();
                    }
                }
            }
        });
    }
}