package edu.calpoly.android.DebugRunRobot;

import android.bluetooth.*;

import java.io.*;

public class BluetoothHandler { 
	
	// The bluetooth device this is connected to (Bluetooth on the Robot board)
	protected static BluetoothDevice device; 
	// The stream outgoing from this phone to the Robot (input to this socket?)
	protected static InputStream outgoing;
    // The stream incoming from the Robot (output by this Socket?)
    protected static OutputStream incoming;
    // The socket used for communication with the robot bluetooth
    protected static BluetoothSocket socket;
}
