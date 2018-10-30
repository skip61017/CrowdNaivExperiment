package com.mslab.experience.crowdnaivexperiment;



import com.hereapps.ibeacon.IBeacon;
import com.hereapps.ibeacon.IBeaconLibrary;
import com.hereapps.ibeacon.IBeaconListener;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class BLEService extends Service implements IBeaconListener{

	
	private IBeaconLibrary iBeaconLibrary;
	
	//private static int 
	
	private IBeaconListener activityListener; 
	
	private MyBinder myBinder = new MyBinder();
	
	
	@Override
	public void onStart(Intent intent, int startId) {
		//Log.e("felix", "onStart:");
		// handler.postDelayed(showTime, 1000);
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		//Log.e("felix", "onDestroy:");
		// handler.removeCallbacks(showTime);
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		//Log.e("felix", "onBind:");
		iBeaconLibrary = IBeaconLibrary.getInstance();
		iBeaconLibrary.setBluetoothAdapter(this);
		//timeout for last seen beacon;
		IBeaconLibrary.SCANNING_TIMEOUT=5000;
		iBeaconLibrary.setListener(this);
		
		return myBinder;
	}
	
	
	
	public class MyBinder extends Binder {
		
		public void setListener(IBeaconListener listener){
			activityListener = listener;
		}
		public void setScanPeriod(int period){
			IBeaconLibrary.SCANNING_TIMEOUT=period;
		}
		public void startScan(){
			scanBeacons();
		}
		public void stopScan(){
			iBeaconLibrary.stopScan();
		}
		
	}
	//====================================
	private void scanBeacons(){
		//Log.i("felix","Scanning");
		//Log.i("felix","iBeaconLibrary.isScanning()="+iBeaconLibrary.isScanning());
		
		if(iBeaconLibrary.isScanning()){
			iBeaconLibrary.stopScan();
			iBeaconLibrary.reset();
			iBeaconLibrary.startScan();	
		}else{
			iBeaconLibrary.startScan();	
		}
				
	}
	
	//=====================================
	@Override
	public void beaconEnter(IBeacon arg0) {
		//Log.i("felix","beaconEnter");
		if(activityListener!=null)activityListener.beaconEnter(arg0);
		
	}
	@Override
	public void beaconExit(IBeacon arg0) {
		//Log.i("felix","beaconExit");
		if(activityListener!=null)activityListener.beaconExit(arg0);
		
	}
	@Override
	public void beaconFound(IBeacon arg0) {
		//Log.i("felix","beaconFound");
		if(activityListener!=null)activityListener.beaconFound(arg0);
		
	}
	@Override
	public void operationError(int arg0) {
		//Log.i("felix","operationError");
		if(activityListener!=null)activityListener.operationError(arg0);
		
	}
	@Override
	public void scanState(int state) {
		//Log.i("felix","scanState");
		//if(activityListener!=null)activityListener.scanState(arg0);
		switch(state){
		case IBeaconLibrary.SCAN_STARTED:
			//Log.i("felix", "state= SCAN_STARTED");	
			break;
		case IBeaconLibrary.SCAN_END_SUCCESS:
			//Log.i("felix", "state= SCAN_END_SUCCESS");
			//here you can scan again.
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					iBeaconLibrary.startScan();	
				}				
			},1000);//wait 1000ms
			break;
		case IBeaconLibrary.SCAN_END_EMPTY:
			//Log.i("felix", "state= SCAN_END_EMPTY");	
			//here you can scan again.
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					iBeaconLibrary.startScan();	
				}				
			},1000);//wait 1000ms
			break;
		
		}
		
	}
	//=============================================

}
