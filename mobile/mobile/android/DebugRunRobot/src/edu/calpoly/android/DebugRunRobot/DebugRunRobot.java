package edu.calpoly.android.DebugRunRobot;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class DebugRunRobot extends Activity {
	public static final String ROBOT_BLUETOOTH_NAME = "RobotBluetooth";
	public static final String TAG = "RobotBluetooth";
	
	//The device representing the Robot's Bluetooth
	private BluetoothHandler bt;
	
	/** Called when the activity is first created. */
    // @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetoothconnect);
        bt = new BluetoothHandler();
        Log.w("BluetoothRobot", "onCreate() called");
    }
    
    public void onStart() {
    	super.onStart();
    	//Get a reference to the Bluetooth Adapter on the phone 
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        //Grab all devices paired by bluetooth to the phone
        Set<BluetoothDevice> devices = null; 
        
        Log.w(TAG, "OnStart() called");
        
        if(btAdapter == null) {
           //bluetooth not supported
        	Log.e(TAG, "Error: Bluetooth Adapter not available");
        	DebugRunRobot.this.finish();
        }
        devices = btAdapter.getBondedDevices();
        //Check for our robot's bluetooth device by device name lookup 
        //May not be the best way but too early to tell
        for(BluetoothDevice d : devices) {
           //Check for the correct name AND that it is bonded already 
           if(d.getName().equals(ROBOT_BLUETOOTH_NAME) && 
            d.getBondState() == BluetoothDevice.BOND_BONDED) {
              bt.device = d;
              break;
           }
        }
        if(bt.device == null) {
        	Log.e(TAG, "Error: Robot Bluetooth not paired");
        	DebugRunRobot.this.finish();
        	
        	/** Robot bluetooth not found... do something...
           //Note: this will be executed if the device isn't found, or 
           // isn't ready relative to the bonding process (in the process 
           // process of being paired for example)
           */
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage("Robot bluetooth is not available!\nQuitting...");
        	AlertDialog alert = builder.create();
        	/**builder.setCancelable(false);
        	builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        	   public void onClick(DialogInterface dialog, int id) {
        	   */   DebugRunRobot.this.finish();
        	   /*}
            });
        	builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
        	   public void onClick(DialogInterface dialog, int id) {
        	      dialog.cancel();
        	   }
            });*/
        	
        }
        Log.w(TAG,  "Paired!");
        // Bluetooth device is now paired and "known"
        try { 
           BluetoothHandler.socket = BluetoothHandler.device.createRfcommSocketToServiceRecord(UUID.randomUUID());
           BluetoothHandler.socket.connect();
           // The data stream coming from the robot (incoming to this phone)
           BluetoothHandler.incoming = BluetoothHandler.socket.getOutputStream();
           BluetoothHandler.outgoing = BluetoothHandler.socket.getInputStream();
        }
        catch(IOException e) {
           //Bluetooth socket was unable to be created somehow... do something?	
        	Log.e(TAG, "Error: Could not connect to Robot Bluetooth and/or establish IO streams");
        	DebugRunRobot.this.finish();
        }
        Log.w(TAG, "Entering write loop");
        try { 
           while(true) {
        	   BluetoothHandler.incoming.write('5');
           }
        }
        catch(Exception e) {
           Log.e(TAG, "Failed to write a byte to the socket!");	
        }
        /* Streams connected, now switch to the GUI buttons to sending commands 
        through the outgoingData stream*/
        Intent intent = new Intent(DebugRunRobot.this, ControlRobot.class);
        startActivity(intent);
    }
}