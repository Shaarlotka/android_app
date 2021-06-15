/**/package com.bluetooth.controller;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ImageButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static android.R.layout.simple_list_item_1;

public class MainActivity extends AppCompatActivity {
    ImageButton up, down, left, right;
    BluetoothAdapter bluetoothAdapter;
    GridLayout screen;
    ArrayList<String> deviceHeap;
    ListView listDevice;
    ArrayAdapter<String> deviceAdapter;
    private UUID uuid;
    private String deviceAddress;
    private final static int REQUEST_ENABLE_BT = 1;
    private final static int MACCons = 17;
    CreateConnectThread connectThreadtoDevice;
    ConnectedThread connectThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        up = findViewById(R.id.buttonUp);
        down = findViewById(R.id.buttonDown);
        left = findViewById(R.id.buttonLeft);
        right = findViewById(R.id.buttonRight);
        listDevice = findViewById(R.id.list);
        screen = findViewById(R.id.screen);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceAddress = getIntent().getStringExtra("deviceAddress");
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported on this hardware platform",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        }
        createListDevice();

        up.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    String command = "u";
                    connectThread.send(command);
                } else {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        String command = "m";
                        connectThread.send(command);
                    }
                }
                return true;
            }
        });

        down.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    String command = "d";
                    connectThread.send(command);
                } else {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        String command = "m";
                        connectThread.send(command);
                    }
                }
                return true;
            }
        });

        left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    String command = "l";
                    connectThread.send(command);
                } else {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        String command = "s";
                        connectThread.send(command);
                    }
                }
                return true;
            }
        });

        right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    String command = "r";
                    connectThread.send(command);
                } else {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        String command = "s";
                        connectThread.send(command);
                    }
                }
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                createListDevice();
            } else {
                Toast.makeText(this, "BlueTooth не включён", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void createListDevice() {
        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        if (devices.size() > 0) {
            deviceHeap = new ArrayList<>();
            for (BluetoothDevice device : devices) {
                deviceHeap.add(device.getName() + "\n" + device.getAddress());
            }
            deviceAdapter = new ArrayAdapter<>(this, simple_list_item_1, deviceHeap);
            listDevice.setAdapter(deviceAdapter);
            listDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                    listDevice.setVisibility(View.GONE);
                    String item = (String) listDevice.getItemAtPosition(pos);
                    String MAC = item.substring(item.length() - MACCons);
                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(MAC);
                    BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);
                    uuid = bluetoothDevice.getUuids()[0].getUuid();
                    connectThreadtoDevice = new CreateConnectThread(device);
                    connectThreadtoDevice.run();
                }
            });
        }
    }

    private class CreateConnectThread extends Thread {
        BluetoothSocket bluetoothSocket = null;
        private CreateConnectThread(BluetoothDevice device) {
            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run () {
            try {
                bluetoothSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    bluetoothSocket.close();
                    listDevice.setVisibility(View.VISIBLE);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if (bluetoothSocket.isConnected()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        screen.setVisibility(View.VISIBLE);
                    }
                });
                connectThread = new ConnectedThread(bluetoothSocket);
                connectThread.run();
            }
        }
    }

    private class ConnectedThread extends Thread {
        private InputStream inStream;
        private OutputStream outStream;
        private String massege;
        private ConnectedThread(BluetoothSocket socket) {
            InputStream in = null;
            OutputStream out = null;
            try {
                inStream = socket.getInputStream();
                outStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            while (true) {
                try {
                    int bytes = inStream.read(buffer);
                    massege = new String(buffer, 0, bytes);
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void send(String buffer) {
            byte[] bytes = buffer.getBytes();
            try {
                outStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}